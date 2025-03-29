// LoginServlet.java
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                System.out.println("[LoginServlet] doGet called");
        // Redirect to login page with an error message if needed
        String redirect = request.getParameter("redirect");
        if (redirect != null) {
            response.sendRedirect(Config.LOGIN_PAGE + "?" + Config.ERROR_PARAM + "=Please log in to continue");
        } else {
            response.sendRedirect(Config.LOGIN_PAGE);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                System.out.println("[LoginServlet] doPost called");

        // Get the username and password from the request parameters
        String username = request.getParameter("username");
        String password = request.getParameter(Config.PASSWORD_FIELD);
        String redirect = request.getParameter("redirect");

        System.out.println("LoginServlet doPost - Username: " + username);
        System.out.println("LoginServlet doPost - Password: " + password);

        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("[LoginServlet] Connected to DB successfully");
            // SQL query to check the credentials
            String sql = "SELECT " + "username" + " FROM " + Config.USER_TABLE + " WHERE " + "username" + " = ? AND " + Config.PASSWORD_FIELD + " = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            System.out.println("[LoginServlet] Executing SQL query to check user credentials");
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("[LoginServlet] User authenticated successfully");
                // If the user exists, create a session and set the username
                HttpSession session = request.getSession(true);  // Ensure a session is created if it doesn't exist
                System.out.println("LoginServlet doPost - Session ID: " + session.getId());
                session.setAttribute("username", username); // Store username in session
                System.out.println("LoginServlet doPost - Set LOGIN_IDENTIFIER in session: " + session.getAttribute("username"));

                // Set a cookie for the username that will persist for 1 hour
                Cookie usernameCookie = new Cookie("username", username);
                usernameCookie.setMaxAge(3600);  // 1 hour expiration
                usernameCookie.setPath("/clovia");
                // usernameCookie.setHttpOnly(false);
                response.addCookie(usernameCookie);  // Add the cookie to the response

                // Initialize cart if not already present in session
                if (session.getAttribute(Config.CART_ITEMS) == null) {
                    session.setAttribute(Config.CART_ITEMS, new java.util.ArrayList<String>());
                    session.setAttribute(Config.CART_QUANTITIES, new java.util.HashMap<String, Integer>());
                    System.out.println("[LoginServlet] Cart session attributes initialized");
                }

                // Get cart items and set a cookie for the cart count
                @SuppressWarnings("unchecked")
                List<String> cart = (List<String>) session.getAttribute(Config.CART_ITEMS);
                Cookie cartCountCookie = new Cookie("cartCount", String.valueOf(cart.size()));
                cartCountCookie.setMaxAge(3600);  // 1 hour expiration
                cartCountCookie.setPath("/");  // Path for the cookie
                cartCountCookie.setHttpOnly(true); 
                response.addCookie(cartCountCookie);  // Add cart count cookie to the response

                // Redirect to the appropriate page after successful login
                String redirectUrl = "/clovia/home.html";
                System.out.println("Redirecting to: " + redirectUrl + "?message=Login successful");
                response.sendRedirect(redirectUrl + "?message=Login%20successful");
            } else {
                // If the credentials are invalid, redirect to the login page with an error
                response.sendRedirect(Config.LOGIN_PAGE + "?" + Config.ERROR_PARAM + "=Invalid " + "username" + " or password");
            }
        } catch (SQLException ex) {
            // Handle any SQL exceptions
            System.out.println("LoginServlet doPost - SQLException: " + ex.getMessage());
            ex.printStackTrace();  
            response.sendRedirect(Config.LOGIN_PAGE + "?" + Config.ERROR_PARAM + "=Login failed: " + ex.getMessage());
        }
    }
}
