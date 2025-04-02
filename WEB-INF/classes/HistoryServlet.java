import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/history")
public class HistoryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check session
        HttpSession session = request.getSession(false);
        System.out.println("LOGIN_IDENTIFIER (" + Config.LOGIN_IDENTIFIER + "): " + 
            (session != null ? session.getAttribute(Config.LOGIN_IDENTIFIER) : "no session"));

        if (session == null || session.getAttribute(Config.LOGIN_IDENTIFIER) == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not logged in");
            return;
        }

        String username = (String) session.getAttribute(Config.LOGIN_IDENTIFIER);
        int userId = -1;

        // Get userId from username
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                throw new Exception("Database connection failed");
            }
            String userSql = "SELECT id FROM user WHERE username = ?";
            try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {
                userStmt.setString(1, username);
                try (ResultSet rs = userStmt.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt("id");
                        System.out.println("HistoryServlet - Retrieved User ID: " + userId + " for username: " + username);
                    } else {
                        System.out.println("HistoryServlet - User not found for username: " + username);
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving user data: " + e.getMessage());
            return;
        }

        // Set response type
        response.setContentType("application/json");
        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        // Fetch order history
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                throw new Exception("Database connection failed");
            }
            String sql = "SELECT o.id AS orderId, p.productName, oi.quantity, p.price, o.address, o.created_at, p.productImage " +
                         "FROM `order` o " +
                         "JOIN orderItem oi ON o.id = oi.orderId " +
                         "JOIN product p ON oi.productId = p.id " +
                         "WHERE o.userId = ? " +
                         "ORDER BY o.created_at DESC";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    int count = 0;
                    while (rs.next()) {
                        count++;
                        if (!first) {
                            json.append(",");
                        }
                        first = false;

                        String productImage = rs.getString("productImage");
                        if (productImage == null) {
                            productImage = ""; // Handle null image paths
                        }

                        json.append("{")
                            .append("\"id\":").append(rs.getInt("orderId")).append(",")
                            .append("\"productName\":\"").append(escapeJson(rs.getString("productName"))).append("\",")
                            .append("\"quantity\":").append(rs.getInt("quantity")).append(",")
                            .append("\"price\":").append(rs.getDouble("price")).append(",")
                            .append("\"address\":\"").append(escapeJson(rs.getString("address"))).append("\",")
                            .append("\"orderDate\":").append(rs.getTimestamp("created_at").getTime()).append(",")
                            .append("\"productImage\":\"").append(escapeJson(productImage)).append("\"")
                            .append("}");
                        System.out.println("Order " + count + ": " + rs.getString("productName"));
                    }
                    System.out.println("Total orders found: " + count);
                }
            }
            json.append("]");
            response.getWriter().write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        }
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"")
                    .replace("\\", "\\\\")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}