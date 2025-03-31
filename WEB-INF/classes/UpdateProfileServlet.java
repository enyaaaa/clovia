import java.io.*;
import java.nio.file.Paths;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

@WebServlet("/update-profile")
@MultipartConfig
public class UpdateProfileServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String username = (String) session.getAttribute("username");
        String phone = request.getParameter("phone");
        String newPassword = request.getParameter("password");
        Part profilePicPart = request.getPart("profilePic"); // Field name must match JS

        try (Connection conn = DBConnection.getConnection()) {
            StringBuilder sql = new StringBuilder("UPDATE user SET mobile = ?");
            if (newPassword != null && !newPassword.isEmpty()) {
                sql.append(", password = ?");
            }
            if (profilePicPart != null && profilePicPart.getSize() > 0) {
                sql.append(", profilePic = ?");
            }
            sql.append(" WHERE username = ?");

            PreparedStatement stmt = conn.prepareStatement(sql.toString());

            int paramIndex = 1;
            stmt.setString(paramIndex++, phone);

            if (newPassword != null && !newPassword.isEmpty()) {
                stmt.setString(paramIndex++, newPassword); // ⚠️ Consider hashing in real apps
            }

            if (profilePicPart != null && profilePicPart.getSize() > 0) {
                String originalFileName = Paths.get(profilePicPart.getSubmittedFileName()).getFileName().toString();
                String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String newFileName = username + "_profile" + extension;

                 // Save to /images folder
                 String imagesFolder = getServletContext().getRealPath("/images/");
                 File uploadDir = new File(imagesFolder);
                 if (!uploadDir.exists()) uploadDir.mkdirs();
 
                 String fullPath = imagesFolder + File.separator + newFileName;
                 profilePicPart.write(fullPath);
                
                //  String savePath = getServletContext().getRealPath("/images/" + newFileName);
                // profilePicPart.write(savePath);

                stmt.setString(paramIndex++, newFileName);
            }

            stmt.setString(paramIndex, username);
            stmt.executeUpdate();

            response.setContentType("text/plain");
            response.getWriter().write("Profile updated!");

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
