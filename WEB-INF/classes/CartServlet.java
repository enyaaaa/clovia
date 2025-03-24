// CartServlet.java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    public CartServlet() {
        System.out.println("CartServlet - Initialized and mapped to /cart");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("CartServlet doPost - Method entered");
        HttpSession session = request.getSession(true);
        
        // Debug: Log the raw request body
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        System.out.println("CartServlet doPost - Raw request body: " + requestBody.toString());

        String productId = request.getParameter("productId");
        System.out.println("CartServlet doPost - Session ID: " + session.getId());
        System.out.println("CartServlet doPost - Product ID: " + productId);
        System.out.println("CartServlet doPost - Request Parameters: " + request.getParameterMap().entrySet());

        if (session.getAttribute(Config.LOGIN_IDENTIFIER) == null) {
            response.sendRedirect(Config.LOGIN_PAGE + "?" + Config.ERROR_PARAM + "=Please log in to add items to cart&redirect=" + Config.CART_PAGE);
            return;
        }

        if (productId == null || productId.trim().isEmpty()) {
            System.out.println("CartServlet doPost - Invalid productId: " + productId);
            response.sendRedirect(Config.HOME_PAGE + "?" + Config.ERROR_PARAM + "=Invalid product ID");
            return;
        }

        @SuppressWarnings("unchecked")
        List<String> cart = (List<String>) session.getAttribute(Config.CART_ITEMS);
        @SuppressWarnings("unchecked")
        Map<String, Integer> quantities = (Map<String, Integer>) session.getAttribute(Config.CART_QUANTITIES);
        if (cart == null) {
            cart = new ArrayList<>();
            quantities = new HashMap<>();
            session.setAttribute(Config.CART_ITEMS, cart);
            session.setAttribute(Config.CART_QUANTITIES, quantities);
        }

        if (!cart.contains(productId)) {
            cart.add(productId);
            quantities.put(productId, 1);
        } else {
            quantities.put(productId, quantities.get(productId) + 1);
        }

        System.out.println("CartServlet doPost - Cart after adding: " + cart);
        System.out.println("CartServlet doPost - Quantities: " + quantities);

        Cookie cartCountCookie = new Cookie("cartCount", String.valueOf(cart.size()));
        cartCountCookie.setMaxAge(3600);
        response.addCookie(cartCountCookie);

        response.sendRedirect(Config.CART_PAGE + "?" + Config.SUCCESS_PARAM + "=Item added to cart");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if (session != null) {
            System.out.println("CartServlet doGet - Session ID: " + session.getId());
        } else {
            System.out.println("CartServlet doGet - Session is null");
        }

        if (session == null || session.getAttribute(Config.LOGIN_IDENTIFIER) == null) {
            out.println("<p>Please log in to view your cart.</p>");
            return;
        }

        @SuppressWarnings("unchecked")
        List<String> cart = (List<String>) session.getAttribute(Config.CART_ITEMS);
        @SuppressWarnings("unchecked")
        Map<String, Integer> quantities = (Map<String, Integer>) session.getAttribute(Config.CART_QUANTITIES);

        System.out.println("CartServlet doGet - Cart: " + cart);
        System.out.println("CartServlet doGet - Quantities: " + quantities);

        if (cart == null || cart.isEmpty()) {
            out.println("<p>Your cart is empty.</p>");
            return;
        }

        cart.removeIf(id -> id == null || id.trim().isEmpty());
        if (cart.isEmpty()) {
            out.println("<p>Your cart is empty (contained invalid items).</p>");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT " + Config.PRODUCT_ID_FIELD + ", " + Config.PRODUCT_NAME_FIELD + ", " + Config.PRODUCT_PRICE_FIELD +
                         " FROM " + Config.PRODUCTS_TABLE + " WHERE " + Config.PRODUCT_ID_FIELD + " IN (" +
                         String.join(",", java.util.Collections.nCopies(cart.size(), "?")) + ")";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            for (int i = 0; i < cart.size(); i++) {
                pstmt.setString(i + 1, cart.get(i));
            }
            ResultSet rs = pstmt.executeQuery();

            out.println("<table border='1'>");
            out.println("<tr><th>ID</th><th>Name</th><th>Price</th><th>Quantity</th><th>Total</th></tr>");
            double grandTotal = 0;
            while (rs.next()) {
                String id = rs.getString(Config.PRODUCT_ID_FIELD);
                String name = rs.getString(Config.PRODUCT_NAME_FIELD);
                double price = rs.getDouble(Config.PRODUCT_PRICE_FIELD);
                int quantity = quantities.getOrDefault(id, 0);
                double total = price * quantity;
                grandTotal += total;

                out.println("<tr>");
                out.println("<td>" + id + "</td>");
                out.println("<td>" + name + "</td>");
                out.println("<td>$" + String.format("%.2f", price) + "</td>");
                out.println("<td>" + quantity + "</td>");
                out.println("<td>$" + String.format("%.2f", total) + "</td>");
                out.println("</tr>");
            }
            out.println("<tr><td colspan='4'>Grand Total</td><td>$" + String.format("%.2f", grandTotal) + "</td></tr>");
            out.println("</table>");
        } catch (SQLException ex) {
            out.println("<p>Error loading cart: " + ex.getMessage() + "</p>");
        }
    }
}