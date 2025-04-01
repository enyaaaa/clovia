import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Check if the user is logged in
        if (session == null || session.getAttribute(Config.LOGIN_ID) == null) {
            response.sendRedirect(Config.LOGIN_PAGE + "?" + Config.ERROR_PARAM + "=Please log in to confirm your order");
            return;
        }

        // Get the user ID from the session (assuming LOGIN_IDENTIFIER stores the user ID)
        String userId = (String) session.getAttribute(Config.LOGIN_ID);
        System.out.println("ConfirmOrderServlet doPost - User ID: " + userId);

        // Get the cart and quantities from the session
        @SuppressWarnings("unchecked")
        List<String> cart = (List<String>) session.getAttribute(Config.CART_ITEMS);
        @SuppressWarnings("unchecked")
        Map<String, Integer> quantities = (Map<String, Integer>) session.getAttribute(Config.CART_QUANTITIES);

        System.out.println("ConfirmOrderServlet doPost - Cart: " + cart);
        System.out.println("ConfirmOrderServlet doPost - Quantities: " + quantities);

        // Check if the cart is empty
        if (cart == null || cart.isEmpty()) {
            response.sendRedirect(Config.CART_PAGE + "?" + Config.ERROR_PARAM + "=Your cart is empty");
            return;
        }

        // Remove any invalid product IDs
        cart.removeIf(id -> id == null || id.trim().isEmpty());
        if (cart.isEmpty()) {
            response.sendRedirect(Config.CART_PAGE + "?" + Config.ERROR_PARAM + "=Your cart contained invalid items");
            return;
        }

        // Get the address from the request (assuming it's passed in the form)
        String address = request.getParameter("address");
        if (address == null || address.trim().isEmpty()) {
            response.sendRedirect(Config.CART_PAGE + "?" + Config.ERROR_PARAM + "=Please provide a valid address");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // SQL to insert into the orders table
            String sql = "INSERT INTO orders (userId, productId, quantity, address, amount, created_at, modified_at) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            // Calculate the total amount for each product and insert into the orders table
            double grandTotal = 0;
            String productSql = "SELECT " + Config.PRODUCT_PRICE_FIELD + " FROM " + Config.PRODUCTS_TABLE +
                               " WHERE " + Config.PRODUCT_ID_FIELD + " = ?";
            PreparedStatement productStmt = conn.prepareStatement(productSql);
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());

            for (String productId : cart) {
                // Get the product price
                productStmt.setString(1, productId);
                double price = 0;
                try (var rs = productStmt.executeQuery()) {
                    if (rs.next()) {
                        price = rs.getDouble(Config.PRODUCT_PRICE_FIELD);
                    }
                }

                int quantity = quantities.getOrDefault(productId, 0);
                double amount = price * quantity;
                grandTotal += amount;

                // Set parameters for the order insertion
                pstmt.setString(1, userId);
                pstmt.setString(2, productId);
                pstmt.setInt(3, quantity);
                pstmt.setString(4, address);
                pstmt.setDouble(5, amount);
                pstmt.setTimestamp(6, now);
                pstmt.setTimestamp(7, now);

                pstmt.addBatch();
            }

            // Execute the batch insert
            pstmt.executeBatch();
            conn.commit();
            System.out.println("ConfirmOrderServlet doPost - Order successfully placed for user: " + userId);

            // Clear the cart after successful order placement
            cart.clear();
            quantities.clear();
            session.setAttribute(Config.CART_ITEMS, cart);
            session.setAttribute(Config.CART_QUANTITIES, quantities);

            // Update the cart count cookie
            Cookie cartCountCookie = new Cookie("cartCount", "0");
            cartCountCookie.setMaxAge(3600);
            response.addCookie(cartCountCookie);

            // Redirect to a success page
            response.sendRedirect(Config.HOME_PAGE + "?" + Config.SUCCESS_PARAM + "=Order placed successfully! Total: $" + String.format("%.2f", grandTotal));

        } catch (SQLException ex) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback on error
                }
            } catch (SQLException rollbackEx) {
                System.out.println("ConfirmOrderServlet doPost - Rollback failed: " + rollbackEx.getMessage());
            }
            System.out.println("ConfirmOrderServlet doPost - SQL Error: " + ex.getMessage());
            response.sendRedirect(Config.CART_PAGE + "?" + Config.ERROR_PARAM + "=Error placing order: " + ex.getMessage());

        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                System.out.println("ConfirmOrderServlet doPost - Error closing resources: " + ex.getMessage());
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(Config.CART_PAGE); // Redirect GET requests to the cart page
    }
}