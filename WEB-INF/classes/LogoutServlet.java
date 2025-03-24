// LogoutServlet.java
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            System.out.println("LogoutServlet doGet - Invalidating session: " + session.getId());
            session.removeAttribute(Config.LOGIN_IDENTIFIER);
            session.invalidate();
        }

        Cookie usernameCookie = new Cookie("username", "");
        usernameCookie.setMaxAge(0);
        response.addCookie(usernameCookie);

        Cookie cartCountCookie = new Cookie("cartCount", "0");
        cartCountCookie.setMaxAge(0);
        response.addCookie(cartCountCookie);

        response.sendRedirect(Config.HOME_PAGE + "?" + Config.SUCCESS_PARAM + "=Logged out successfully");
    }
}