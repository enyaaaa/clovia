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
        String username = request.getParameter(Config.LOGIN_IDENTIFIER);
        String password = request.getParameter(Config.PASSWORD_FIELD);
        String redirect = request.getParameter("redirect");

        System.out.println("LoginServlet doPost - Username: " + username);
        System.out.println("LoginServlet doPost - Password: " + password);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT " + Config.LOGIN_IDENTIFIER + " FROM " + Config.USER_TABLE + " WHERE " + Config.LOGIN_IDENTIFIER + " = ? AND " + Config.PASSWORD_FIELD + " = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                HttpSession session = request.getSession(true);
                System.out.println("LoginServlet doPost - Session ID: " + session.getId());
                session.setAttribute(Config.LOGIN_IDENTIFIER, username);
                System.out.println("LoginServlet doPost - Set LOGIN_IDENTIFIER: " + username);
                Cookie usernameCookie = new Cookie("username", username);
                usernameCookie.setMaxAge(3600);
                response.addCookie(usernameCookie);
                if (session.getAttribute(Config.CART_ITEMS) == null) {
                    session.setAttribute(Config.CART_ITEMS, new java.util.ArrayList<String>());
                    session.setAttribute(Config.CART_QUANTITIES, new java.util.HashMap<String, Integer>());
                }
                @SuppressWarnings("unchecked")
                List<String> cart = (List<String>) session.getAttribute(Config.CART_ITEMS);
                Cookie cartCountCookie = new Cookie("cartCount", String.valueOf(cart.size()));
                cartCountCookie.setMaxAge(3600);
                response.addCookie(cartCountCookie);

                String redirectUrl = (redirect != null && !redirect.isEmpty()) ? redirect : Config.HOME_PAGE;
                response.sendRedirect(redirectUrl + "?" + Config.SUCCESS_PARAM + "=Login successful");
            } else {
                response.sendRedirect(Config.LOGIN_PAGE + "?" + Config.ERROR_PARAM + "=Invalid " + Config.LOGIN_IDENTIFIER + " or password");
            }
        } catch (SQLException ex) {
            System.out.println("LoginServlet doPost - SQLException: " + ex.getMessage());
            response.sendRedirect(Config.LOGIN_PAGE + "?" + Config.ERROR_PARAM + "=Login failed: " + ex.getMessage());
        }
    }
}