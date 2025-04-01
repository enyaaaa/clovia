    // SearchServlet.java
    import java.util.List;
    import java.util.ArrayList;
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
            String query = request.getParameter("searchTerm");
            if (query == null || query.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("{\"error\": \"Please provide a search term.\"}");
                return;
            }

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT " + Config.PRODUCT_ID_FIELD + ", " + Config.PRODUCT_TYPE_FIELD + ", " +
             Config.PRODUCT_NAME_FIELD + ", " + Config.PRODUCT_IMAGE_FIELD + ", " + Config.PRODUCT_PRICE_FIELD +
             " FROM " + Config.PRODUCTS_TABLE + " WHERE " + Config.PRODUCT_NAME_FIELD + " LIKE ?";

                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, "%" + query + "%");
                ResultSet rs = pstmt.executeQuery();

                List<String> products = new ArrayList<>();

                while (rs.next()) {
                    StringBuilder productJson = new StringBuilder();
                    productJson.append("{")
                            .append("\"id\":\"").append(rs.getString(Config.PRODUCT_ID_FIELD)).append("\",")
                            .append("\"name\":\"").append(rs.getString(Config.PRODUCT_NAME_FIELD)).append("\",")
                            .append("\"image\":\"").append(rs.getString(Config.PRODUCT_IMAGE_FIELD)).append("\",")
                            .append("\"price\":").append(rs.getDouble(Config.PRODUCT_PRICE_FIELD))
                            .append("}");
                    products.add(productJson.toString());
                }

                if (products.isEmpty()) {
                    out.println("{\"message\": \"No products found for the search term: " + query + "\"}");
                } else {
                    out.println("[" + String.join(",", products) + "]");
                }
            } catch (SQLException ex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println("{\"error\": \"" + ex.getMessage() + "\"}");
            }
        }
    }

    //                 String id = rs.getString(Config.PRODUCT_ID_FIELD);
    //                 String name = rs.getString(Config.PRODUCT_NAME_FIELD);
    //                 double price = rs.getDouble(Config.PRODUCT_PRICE_FIELD);
    //                 System.out.println("SearchServlet - Product ID for button: " + id);
    //                 out.println("<tr>");
    //                 out.println("<td>" + id + "</td>");
    //                 out.println("<td>" + name + "</td>");
    //                 out.println("<td>$" + String.format("%.2f", price) + "</td>");
    //                 out.println("<td><button onclick=\"addToCart('" + id + "')\">Add to Cart</button></td>");
    //                 out.println("</tr>");
    //             }
    //             out.println("</table>");
    //         } catch (SQLException ex) {
    //             System.out.println("SearchServlet doGet - SQLException: " + ex.getMessage());
    //             out.println("<p>Error: " + ex.getMessage() + "</p>");
    //         }
    //     }
    // }