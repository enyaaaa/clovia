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
            session.removeAttribute("username"); // Assuming Config.LOGIN_IDENTIFIER is "username"
            session.invalidate();
        }

        // Clear username cookie
        Cookie usernameCookie = new Cookie("username", "");
        usernameCookie.setMaxAge(0);
        usernameCookie.setPath("/");
        response.addCookie(usernameCookie);

        // Clear cartCount cookie
        Cookie cartCountCookie = new Cookie("cartCount", "0");
        cartCountCookie.setMaxAge(0);
        cartCountCookie.setPath("/");
        response.addCookie(cartCountCookie);

        // Redirect to home page with success message
        response.sendRedirect("/clovia/home.html?message=Logged%20out%20successfully");
    }
}