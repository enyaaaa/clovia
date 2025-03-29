import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/check-session")
public class CheckSessionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        System.out.println("[DEBUG] Session ID: " + (session != null ? session.getId() : "null"));
        System.out.println("[DEBUG] Username in session: " + (session != null ? session.getAttribute("username") : "null"));
        boolean loggedIn = (session != null && session.getAttribute("username") != null);
        String username = loggedIn ? (String) session.getAttribute("username") : "";

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if (loggedIn) {
            // String username = (String) session.getAttribute("username");
            out.print("{\"loggedIn\": true, \"username\": \"" + username + "\"}");
        } else {
            out.print("{\"loggedIn\": false}");
        }

        out.flush();
    }
}
