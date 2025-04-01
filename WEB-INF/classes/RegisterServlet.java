import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
        response.getWriter().write(message);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Please register using the form");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username"); // Match HTML name
        String email = request.getParameter("email");
        String mobile = request.getParameter("mobile");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm_password");

        // Server-side validation
        if (username == null || username.trim().isEmpty()) {
            sendError(response, "Username is required");
            return;
        }
        if (username.length() < 3) {
            sendError(response, "Username must be at least 3 characters");
            return;
        }
        if (!username.matches("^[a-zA-Z0-9]+$")) {
            sendError(response, "Username must be alphanumeric");
            return;
        }

        if (email == null || email.trim().isEmpty()) {
            sendError(response, "Email is required");
            return;
        }
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            sendError(response, "Invalid email format");
            return;
        }

        if (mobile == null || mobile.trim().isEmpty()) {
            sendError(response, "Mobile number is required");
            return;
        }
        if (!mobile.matches("^\\d{8}$")) {
            sendError(response, "Mobile number must be 8 digits");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            sendError(response, "Password is required");
            return;
        }

        if (confirmPassword == null || !confirmPassword.equals(password)) {
            sendError(response, "Passwords do not match");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Check for existing username
            String checkSql = "SELECT username FROM " + Config.USER_TABLE + " WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                sendError(response, "Username already exists");
                return;
            }

            // Insert new user
            String insertSql = "INSERT INTO " + Config.USER_TABLE + " (username, email, password, mobile) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertSql);
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, password); // Note: Should hash in production
            pstmt.setString(4, mobile);
            pstmt.executeUpdate();

            response.setContentType("text/plain");
            response.setStatus(HttpServletResponse.SC_OK); // 200
            response.getWriter().write("Registration successful");
        } catch (SQLException ex) {
            ex.printStackTrace();
            sendError(response, "Registration failed: " + ex.getMessage());
        }
    }
}