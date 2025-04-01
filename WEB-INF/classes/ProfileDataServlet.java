import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/profile-data")
public class ProfileDataServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        System.out.println("[DEBUG] Session: " + session);
        if (session != null) {
            System.out.println("[DEBUG] Username in session: " + session.getAttribute("username"));
        }
        
        if (session == null || session.getAttribute("username") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            return;
        }
        response.setContentType("application/json");

        String username = (String) session.getAttribute("username");

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT id, username, email, mobile, profilePic FROM user WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String profileJson = String.format(
                    "{\"id\":\"%s\",\"username\":\"%s\",\"email\":\"%s\",\"mobile\":\"%s\",\"profilePic\":\"%s\"}",
                    rs.getString("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("mobile"),
                    rs.getString("profilePic") == null ? "default-profile.png" : rs.getString("profilePic")
                );
                response.getWriter().write(profileJson);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
