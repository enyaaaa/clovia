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

        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.html?error=Please login to give feedback");
            return;
        }

        String username = (String) session.getAttribute("username");
        String message = request.getParameter("message");

        // Validate input
        if (message == null || message.trim().isEmpty()) {
            response.sendRedirect("feedback.html?error=Feedback cannot be empty");
            return;
        }

        // Insert feedback into DB
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

        // Show styled thank-you message
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"en\">");
        out.println("<head>");
        out.println("<meta charset=\"UTF-8\">");
        out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        out.println("<title>Thank You - Clovia</title>");
        out.println("<link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">");
        out.println("<link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>");
        out.println("<link href=\"https://fonts.googleapis.com/css2?family=Urbanist:wght@400;500;600;700;800&display=swap\" rel=\"stylesheet\">");
        out.println("<style>");
        out.println("body {");
        out.println("    font-family: 'Urbanist', sans-serif;");
        out.println("    background-color: hsl(0, 0%, 98%);");
        out.println("    display: flex;");
        out.println("    justify-content: center;");
        out.println("    align-items: center;");
        out.println("    height: 100vh;");
        out.println("    margin: 0;");
        out.println("    color: hsl(0, 0%, 49%);");
        out.println("}");
        out.println(".thank-you-container {");
        out.println("    background-color: hsl(0, 0%, 100%);");
        out.println("    padding: 40px;");
        out.println("    border-radius: 5px;");
        out.println("    box-shadow: 0 4px 10px hsla(0, 0%, 0%, 0.05);");
        out.println("    text-align: center;");
        out.println("    max-width: 600px;");
        out.println("}");
        out.println("h3 {");
        out.println("    font-size: 2.4rem;");
        out.println("    font-weight: 400;");
        out.println("    color: hsl(148, 20%, 38%);");
        out.println("    margin-bottom: 20px;");
        out.println("}");
        out.println("a {");
        out.println("    display: inline-block;");
        out.println("    font-size: 1.6rem;");
        out.println("    font-weight: 400;");
        out.println("    padding: 12px 30px;");
        out.println("    border-radius: 3px;");
        out.println("    background-color: hsl(148, 20%, 38%);");
        out.println("    color: hsl(0, 0%, 100%);");
        out.println("    text-decoration: none;");
        out.println("    transition: background-color 0.25s ease;");
        out.println("}");
        out.println("a:hover {");
        out.println("    background-color: hsl(148, 30%, 28%);");
        out.println("    box-shadow: 0 2px 8px hsla(148, 30%, 28%, 0.4);");
        out.println("}");
        out.println("@media (max-width: 768px) {");
        out.println("    .thank-you-container {");
        out.println("        padding: 20px;");
        out.println("    }");
        out.println("    h3 {");
        out.println("        font-size: 2rem;");
        out.println("    }");
        out.println("}");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class=\"thank-you-container\">");
        out.println("<h3>Thank you for your feedback, " + username + "!</h3>");
        out.println("<a href=\"home.html\">Back to Home</a>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }

    // Redirect GET requests to feedback form
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect("feedback.html");
    }
}