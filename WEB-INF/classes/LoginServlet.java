import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    // Helper method to send error response
    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.getWriter().write(message);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("[LoginServlet] doGet called");
        String redirect = request.getParameter("redirect");
        sendError(response, "Please log in to continue");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("[LoginServlet] doPost called");

        String username = request.getParameter("username");
        String password = request.getParameter(Config.PASSWORD_FIELD);

        System.out.println("LoginServlet doPost - Username: " + username);
        System.out.println("LoginServlet doPost - Password: " + (password != null ? "[hidden]" : "null"));

        // Server-side format validation
        if (username == null || username.trim().isEmpty()) {
            System.out.println("[LoginServlet] Validation failed: Username is empty");
            sendError(response, "Username is required");
            return;
        }
        if (username.length() < 3) {
            System.out.println("[LoginServlet] Validation failed: Username too short");
            sendError(response, "Username must be at least 3 characters");
            return;
        }
        if (!username.matches("^[a-zA-Z0-9]+$")) {
            System.out.println("[LoginServlet] Validation failed: Username not alphanumeric");
            sendError(response, "Username must be alphanumeric");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            System.out.println("[LoginServlet] Validation failed: Password is empty");
            sendError(response, "Password is required");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("[LoginServlet] Connected to DB successfully");

            // Check credentials
            String sql = "SELECT username FROM " + Config.USER_TABLE + " WHERE username = ? AND " + Config.PASSWORD_FIELD + " = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("[LoginServlet] User authenticated successfully");
                HttpSession session = request.getSession(true);
                System.out.println("LoginServlet doPost - Session ID: " + session.getId());
                session.setAttribute("username", username);

                // Set username cookie
                Cookie usernameCookie = new Cookie("username", username);
                usernameCookie.setMaxAge(3600); // 1 hour
                usernameCookie.setPath("/clovia");
                response.addCookie(usernameCookie);

                // Initialize cart if not present
                if (session.getAttribute(Config.CART_ITEMS) == null) {
                    session.setAttribute(Config.CART_ITEMS, new java.util.ArrayList<String>());
                    session.setAttribute(Config.CART_QUANTITIES, new java.util.HashMap<String, Integer>());
                    System.out.println("[LoginServlet] Cart session attributes initialized");
                }

                // Set cart count cookie
                @SuppressWarnings("unchecked")
                List<String> cart = (List<String>) session.getAttribute(Config.CART_ITEMS);
                Cookie cartCountCookie = new Cookie("cartCount", String.valueOf(cart.size()));
                cartCountCookie.setMaxAge(3600); // 1 hour
                cartCountCookie.setPath("/");
                cartCountCookie.setHttpOnly(true);
                response.addCookie(cartCountCookie);

                response.setContentType("text/plain");
                response.setStatus(HttpServletResponse.SC_OK); // 200
                response.getWriter().write("Login successful");
            } else {
                System.out.println("[LoginServlet] Authentication failed: Invalid credentials");
                sendError(response, "Invalid username or password");
            }
        } catch (SQLException ex) {
            System.out.println("LoginServlet doPost - SQLException: " + ex.getMessage());
            ex.printStackTrace();
            sendError(response, "Login failed: " + ex.getMessage());
        }
    }
}