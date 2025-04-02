import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;

@WebServlet("/confirmOrder")
public class ConfirmOrderServlet extends HttpServlet {

    public ConfirmOrderServlet() {
        System.out.println("ConfirmOrderServlet - Initialized and mapped to /confirmOrder");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("ConfirmOrderServlet doPost - Method entered");
        HttpSession session = request.getSession(false);
        System.out.println("Session: " + (session != null ? session.getId() : "null"));
        if (session != null) {
            System.out.println("Session Creation Time: " + new java.util.Date(session.getCreationTime()));
            System.out.println("Session Last Accessed Time: " + new java.util.Date(session.getLastAccessedTime()));
            System.out.println("Session Max Inactive Interval: " + session.getMaxInactiveInterval() + " seconds");
            System.out.println("All Session Attributes: ");
            java.util.Enumeration<String> attributeNames = session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String name = attributeNames.nextElement();
                System.out.println("  " + name + ": " + session.getAttribute(name));
            }
        }
        System.out.println("LOGIN_IDENTIFIER (" + Config.LOGIN_IDENTIFIER + "): " + (session != null ? session.getAttribute(Config.LOGIN_IDENTIFIER) : "no session"));
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if (session == null || session.getAttribute(Config.LOGIN_IDENTIFIER) == null) {
            String contextPath = request.getContextPath();
            response.sendRedirect(contextPath + "/" + Config.LOGIN_PAGE + "?" + Config.ERROR_PARAM + "=Please log in to confirm your order");
            return;
        }

        String username = (String) session.getAttribute(Config.LOGIN_IDENTIFIER);
        System.out.println("ConfirmOrderServlet doPost - Username: " + username);

        // Retrieve userId from the database using the username
        int userId = -1; // Default value to indicate failure
        try (Connection conn = DBConnection.getConnection()) {
            String userSql = "SELECT id FROM user WHERE username = ?";
            PreparedStatement userStmt = conn.prepareStatement(userSql);
            userStmt.setString(1, username);
            try (var rs = userStmt.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getInt("id");
                    System.out.println("ConfirmOrderServlet doPost - Retrieved User ID: " + userId);
                } else {
                    System.out.println("ConfirmOrderServlet doPost - User not found for username: " + username);
                    String contextPath = request.getContextPath();
                    response.sendRedirect(contextPath + "/" + Config.LOGIN_PAGE + "?" + Config.ERROR_PARAM + "=User not found");
                    return;
                }
            }
        } catch (SQLException ex) {
            System.out.println("ConfirmOrderServlet doPost - Error retrieving user ID: " + ex.getMessage());
            String contextPath = request.getContextPath();
            response.sendRedirect(contextPath + "/" + Config.LOGIN_PAGE + "?" + Config.ERROR_PARAM + "=Error retrieving user data");
            return;
        }

        String address = request.getParameter("address");
        String method = request.getParameter("payment");
        String cartData = request.getParameter("cartData");

        if (address == null || address.trim().isEmpty() || method == null || cartData == null || cartData.trim().isEmpty()) {
            String contextPath = request.getContextPath();
            response.sendRedirect(contextPath + "/" + Config.CART_PAGE + "?" + Config.ERROR_PARAM + "=Missing required fields");
            return;
        }

        List<Map<String, Object>> cart = new ArrayList<>();
        try {
            String json = cartData.trim();
            if (json.startsWith("[") && json.endsWith("]")) {
                json = json.substring(1, json.length() - 1);
                String[] items = json.split("},\\s*\\{");
                for (String item : items) {
                    item = item.replace("{", "").replace("}", "").replace("\"", "");
                    String[] pairs = item.split(",");
                    Map<String, Object> map = new HashMap<>();
                    for (String pair : pairs) {
                        String[] keyValue = pair.split(":");
                        if (keyValue.length == 2) {
                            map.put(keyValue[0].trim(), keyValue[1].trim());
                        }
                    }
                    cart.add(map);
                }
            }
            System.out.println("ConfirmOrderServlet doPost - Cart parsed manually: " + cart);
        } catch (Exception e) {
            System.out.println("ConfirmOrderServlet doPost - Error parsing cart data: " + e.getMessage());
            String contextPath = request.getContextPath();
            response.sendRedirect(contextPath + "/" + Config.CART_PAGE + "?" + Config.ERROR_PARAM + "=Invalid cart data");
            return;
        }

        if (cart.isEmpty()) {
            String contextPath = request.getContextPath();
            response.sendRedirect(contextPath + "/" + Config.CART_PAGE + "?" + Config.ERROR_PARAM + "=Your cart is empty");
            return;
        }

        Connection conn = null;
        PreparedStatement orderStmt = null;
        PreparedStatement orderItemStmt = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String orderSql = "INSERT INTO `order` (id, userId, address, method, amount, created_at) VALUES (?, ?, ?, ?, ?, ?)";
            orderStmt = conn.prepareStatement(orderSql);
            int orderId = generateUniqueOrderId(conn);
            double totalAmount = 0;
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());

            String productSql = "SELECT price FROM product WHERE id = ?";
            PreparedStatement productStmt = conn.prepareStatement(productSql);
            for (Map<String, Object> item : cart) {
                String productId = String.valueOf(item.get("id"));
                int quantity = Integer.parseInt(String.valueOf(item.get("quantity")));
                productStmt.setString(1, productId);
                try (var rs = productStmt.executeQuery()) {
                    if (rs.next()) {
                        totalAmount += rs.getDouble("price") * quantity;
                    } else {
                        throw new SQLException("Product ID " + productId + " not found");
                    }
                }
            }

            orderStmt.setInt(1, orderId);
            orderStmt.setInt(2, userId); // Use the retrieved userId (integer)
            orderStmt.setString(3, address);
            orderStmt.setString(4, method);
            orderStmt.setDouble(5, totalAmount);
            orderStmt.setTimestamp(6, now);
            orderStmt.executeUpdate();
            System.out.println("ConfirmOrderServlet doPost - Order inserted with ID: " + orderId);

            String orderItemSql = "INSERT INTO orderItem (id, orderId, productId, quantity) VALUES (?, ?, ?, ?)";
            orderItemStmt = conn.prepareStatement(orderItemSql);
            int orderItemId = generateUniqueOrderItemId(conn);
            for (Map<String, Object> item : cart) {
                String productId = String.valueOf(item.get("id"));
                int quantity = Integer.parseInt(String.valueOf(item.get("quantity")));
                orderItemStmt.setInt(1, orderItemId++);
                orderItemStmt.setInt(2, orderId);
                orderItemStmt.setString(3, productId);
                orderItemStmt.setInt(4, quantity);
                orderItemStmt.addBatch();
            }
            orderItemStmt.executeBatch();
            System.out.println("ConfirmOrderServlet doPost - Order items inserted");

            conn.commit();

            Cookie cartCountCookie = new Cookie("cartCount", "0");
            cartCountCookie.setMaxAge(3600);
            response.addCookie(cartCountCookie);

            String contextPath = request.getContextPath();
            response.sendRedirect(contextPath + "/" + Config.HOME_PAGE + "?" + Config.SUCCESS_PARAM + "=Order placed successfully! Total: $" + String.format("%.2f", totalAmount));

        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("ConfirmOrderServlet doPost - Transaction rolled back");
                } catch (SQLException rollbackEx) {
                    System.out.println("ConfirmOrderServlet doPost - Rollback failed: " + rollbackEx.getMessage());
                }
            }
            System.out.println("ConfirmOrderServlet doPost - SQL Error: " + ex.getMessage());
            String contextPath = request.getContextPath();
            response.sendRedirect(contextPath + "/" + Config.CART_PAGE + "?" + Config.ERROR_PARAM + "=Error placing order: " + ex.getMessage());
        } finally {
            try {
                if (orderStmt != null) orderStmt.close();
                if (orderItemStmt != null) orderItemStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                System.out.println("ConfirmOrderServlet doPost - Error closing resources: " + ex.getMessage());
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String contextPath = request.getContextPath();
        response.sendRedirect(contextPath + "/" + Config.CART_PAGE);
    }

    private int generateUniqueOrderId(Connection conn) throws SQLException {
        String sql = "SELECT MAX(id) FROM `order`";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             var rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
            return 1;
        }
    }

    private int generateUniqueOrderItemId(Connection conn) throws SQLException {
        String sql = "SELECT MAX(id) FROM orderItem";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             var rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
            return 1;
        }
    }
}