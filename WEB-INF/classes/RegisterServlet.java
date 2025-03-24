// RegisterServlet.java
// Handles user registration with email and password confirmation
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(Config.REGISTER_PAGE);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter(Config.LOGIN_IDENTIFIER);
        String email = request.getParameter(Config.EMAIL_FIELD);
        String mobile = request.getParameter(Config.MOBILE_FIELD);
        String password = request.getParameter(Config.PASSWORD_FIELD);
        String confirmPassword = request.getParameter(Config.CONFIRM_PASSWORD_FIELD);

        if (!password.equals(confirmPassword)) {
            response.sendRedirect(Config.REGISTER_PAGE + "?" + Config.ERROR_PARAM + "=Passwords do not match");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String checkSql = "SELECT " + Config.LOGIN_IDENTIFIER + " FROM " + Config.USER_TABLE + " WHERE " + Config.LOGIN_IDENTIFIER + " = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                response.sendRedirect(Config.REGISTER_PAGE + "?" + Config.ERROR_PARAM + "=" + Config.LOGIN_IDENTIFIER + " already exists");
                return;
            }

            String insertSql = "INSERT INTO " + Config.USER_TABLE + " (" + String.join(", ", Config.REGISTRATION_FIELDS) + ") VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertSql);
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, password); 
            pstmt.setString(4, mobile);
            pstmt.executeUpdate();

            response.sendRedirect(Config.LOGIN_PAGE + "?" + Config.SUCCESS_PARAM + "=Registration successful");
        } catch (SQLException ex) {
            response.sendRedirect(Config.REGISTER_PAGE + "?" + Config.ERROR_PARAM + "=Registration failed: " + ex.getMessage());
        }
    }
}