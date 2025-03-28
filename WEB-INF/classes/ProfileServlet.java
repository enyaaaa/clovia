import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.html?error=Please login first");
            return;
        }

        String username = (String) session.getAttribute("username");

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT username, email, mobile, profilePic FROM user WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                request.setAttribute("username", rs.getString("username"));
                request.setAttribute("email", rs.getString("email"));
                request.setAttribute("mobile", rs.getString("mobile"));
                request.setAttribute("profilePic", rs.getString("profilePic"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        request.getRequestDispatcher("/profile.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.html?error=Please login first");
            return;
        }

        String username = (String) session.getAttribute("username");
        String phone = request.getParameter("phone");
        String newPassword = request.getParameter("password");
        Part profilePicPart = request.getPart("profile-picture");

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE user SET mobile = ?, password = ?, profilePic = ? WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, phone);
            stmt.setString(2, newPassword);  // You should hash this in real apps

            String picPath = null;
            if (profilePicPart != null && profilePicPart.getSize() > 0) {
                String fileName = username + "_profile.jpg";
                String savePath = getServletContext().getRealPath("/uploads/" + fileName);
                profilePicPart.write(savePath);
                picPath = "uploads/" + fileName;
            }

            stmt.setString(3, picPath);
            stmt.setString(4, username);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        response.sendRedirect("profile");
    }
}
