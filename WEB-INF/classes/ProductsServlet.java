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

@WebServlet("/products")
public class ProductsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        // Use StringBuilder to build the JSON response
        StringBuilder jsonResponse = new StringBuilder();
        jsonResponse.append("[");

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT " + Config.PRODUCT_ID_FIELD + ", " + Config.PRODUCT_TYPE_FIELD + ", " + Config.PRODUCT_NAME_FIELD + ", " + Config.PRODUCT_IMAGE_FIELD + ", " + Config.PRODUCT_PRICE_FIELD +
                         " FROM " + Config.PRODUCTS_TABLE;
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            boolean first = true; // To handle commas between JSON objects

            while (rs.next()) {
                String id = rs.getString(Config.PRODUCT_ID_FIELD);
                String type = rs.getString(Config.PRODUCT_TYPE_FIELD);
                String name = rs.getString(Config.PRODUCT_NAME_FIELD);
                String image = rs.getString(Config.PRODUCT_IMAGE_FIELD);
                double price = rs.getDouble(Config.PRODUCT_PRICE_FIELD);

                // Append a comma if it's not the first product
                if (!first) {
                    jsonResponse.append(",");
                }
                first = false;

                // Manually build each product JSON object
                jsonResponse.append("{");
                jsonResponse.append("\"id\":\"").append(id).append("\",");
                jsonResponse.append("\"type\":\"").append(type).append("\",");
                jsonResponse.append("\"name\":\"").append(name).append("\",");
                jsonResponse.append("\"image\":\"").append(image).append("\",");
                jsonResponse.append("\"price\":").append(price);
                jsonResponse.append("}");
            }

            jsonResponse.append("]");

            // Output the JSON response
            out.print(jsonResponse.toString());

        } catch (SQLException ex) {
            System.out.println("ProductsServlet doGet - SQLException: " + ex.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"Error: " + ex.getMessage() + "\"}");
        }
    }
}
