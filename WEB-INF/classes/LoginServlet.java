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

        String username = request.getParameter("username");
        String password = request.getParameter(Config.PASSWORD_FIELD);
        String redirect = request.getParameter("redirect");

        System.out.println("LoginServlet doPost - Username: " + username);
        System.out.println("LoginServlet doPost - Password: " + password);

        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("[LoginServlet] Connected to DB successfully");
            String sql = "SELECT " + "username" + " FROM " + Config.USER_TABLE + " WHERE " + "username" + " = ? AND " + Config.PASSWORD_FIELD + " = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            System.out.println("[LoginServlet] Executing SQL query to check user credentials");
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("[LoginServlet] User authenticated successfully");
                HttpSession session = request.getSession(true);
                System.out.println("LoginServlet doPost - Session ID: " + session.getId());
                session.setAttribute("username", username);
                System.out.println("LoginServlet doPost - Set LOGIN_IDENTIFIER in session: " + session.getAttribute("username"));

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

                // Redirect to home page
                String redirectUrl = "/clovia/home.html";
                System.out.println("Redirecting to: " + redirectUrl + "?message=Login successful");
                response.sendRedirect(redirectUrl + "?message=Login%20successful");
            } else {
                response.sendRedirect(Config.LOGIN_PAGE + "?" + Config.ERROR_PARAM + "=Invalid " + "username" + " or password");
            }
        } catch (SQLException ex) {
            System.out.println("LoginServlet doPost - SQLException: " + ex.getMessage());
            ex.printStackTrace();
            response.sendRedirect(Config.LOGIN_PAGE + "?" + Config.ERROR_PARAM + "=Login failed: " + ex.getMessage());
        }
    }
}