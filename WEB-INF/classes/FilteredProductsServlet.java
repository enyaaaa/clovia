import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/filtered-products")
public class FilteredProductsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String productType = request.getParameter("type");
        if (productType == null || productType.trim().isEmpty()) {
            out.println("{\"error\": \"Product type is required.\"}");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT " + Config.PRODUCT_ID_FIELD + ", " + Config.PRODUCT_TYPE_FIELD + ", " +
                    Config.PRODUCT_NAME_FIELD + ", " + Config.PRODUCT_IMAGE_FIELD + ", " + Config.PRODUCT_PRICE_FIELD +
                    " FROM " + Config.PRODUCTS_TABLE + " WHERE " + Config.PRODUCT_TYPE_FIELD + " = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, productType);
            ResultSet rs = pstmt.executeQuery();

            List<String> products = new ArrayList<>();
            while (rs.next()) {
                StringBuilder productJson = new StringBuilder();
                productJson.append("{")
                        .append("\"id\":\"").append(rs.getString(Config.PRODUCT_ID_FIELD)).append("\",")
                        .append("\"type\":\"").append(rs.getString(Config.PRODUCT_TYPE_FIELD)).append("\",")
                        .append("\"name\":\"").append(rs.getString(Config.PRODUCT_NAME_FIELD)).append("\",")
                        .append("\"image\":\"").append(rs.getString(Config.PRODUCT_IMAGE_FIELD)).append("\",")
                        .append("\"price\":").append(rs.getDouble(Config.PRODUCT_PRICE_FIELD))
                        .append("}");
                products.add(productJson.toString());
            }

            if (products.isEmpty()) {
                out.println("{\"message\": \"No products found for type: " + productType + "\"}");
            } else {
                out.println("[" + String.join(",", products) + "]");
            }
        } catch (SQLException ex) {
            out.println("{\"error\": \"" + ex.getMessage() + "\"}");
        }
    }
}
