// SearchServlet.java
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String query = request.getParameter("query");
        if (query == null) {
            query = "";
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT " + Config.PRODUCT_ID_FIELD + ", " + Config.PRODUCT_NAME_FIELD + ", " + Config.PRODUCT_PRICE_FIELD +
                         " FROM " + Config.PRODUCTS_TABLE + " WHERE " + Config.PRODUCT_NAME_FIELD + " LIKE ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + query + "%");
            ResultSet rs = pstmt.executeQuery();

            out.println("<table border='1'>");
            out.println("<tr><th>ID</th><th>Name</th><th>Price</th><th>Action</th></tr>");
            while (rs.next()) {
                String id = rs.getString(Config.PRODUCT_ID_FIELD);
                String name = rs.getString(Config.PRODUCT_NAME_FIELD);
                double price = rs.getDouble(Config.PRODUCT_PRICE_FIELD);
                System.out.println("SearchServlet - Product ID for button: " + id);
                out.println("<tr>");
                out.println("<td>" + id + "</td>");
                out.println("<td>" + name + "</td>");
                out.println("<td>$" + String.format("%.2f", price) + "</td>");
                out.println("<td><button onclick=\"addToCart('" + id + "')\">Add to Cart</button></td>");
                out.println("</tr>");
            }
            out.println("</table>");
        } catch (SQLException ex) {
            System.out.println("SearchServlet doGet - SQLException: " + ex.getMessage());
            out.println("<p>Error: " + ex.getMessage() + "</p>");
        }
    }
}