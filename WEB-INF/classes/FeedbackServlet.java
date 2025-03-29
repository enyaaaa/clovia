import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/submit-feedback")
public class FeedbackServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // ✅ Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.html?error=Please login to give feedback");
            return;
        }

        String username = (String) session.getAttribute("username");
        String message = request.getParameter("message");

        // ✅ Validate input
        if (message == null || message.trim().isEmpty()) {
            response.sendRedirect("feedback.html?error=Feedback cannot be empty");
            return;
        }

        // ✅ Insert feedback into DB
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO feedback (username, message) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, message);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("feedback.html?error=Something went wrong while saving your feedback.");
            return;
        }

        // ✅ Show thank you message directly
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h3>Thank you for your feedback, " + username + "!</h3>");
        out.println("<a href='home.html'>Back to Home</a>");
        out.println("</body></html>");
    }

    // ✅ Redirect GET requests to feedback form
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect("feedback.html");
    }
}
