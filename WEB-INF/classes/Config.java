// Config.java
// Central configuration file for reusable variables
public class Config {
    // Database configuration
    public static final String DB_URL_BASE = "jdbc:mysql://localhost:3306/";
    public static final String DB_NAME = "clovia";
    public static final String DB_PARAMS = "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "MySecurePassword";

    // User table and field names
    public static final String USER_TABLE = "user";
    public static final String LOGIN_IDENTIFIER = "username"; // Login field (e.g., "username" or "email")
    public static final String PASSWORD_FIELD = "password";
    public static final String EMAIL_FIELD = "email";
    public static final String MOBILE_FIELD = "mobile";
    public static final String CONFIRM_PASSWORD_FIELD = "confirm_password";
    public static final String[] REGISTRATION_FIELDS = {LOGIN_IDENTIFIER, EMAIL_FIELD, PASSWORD_FIELD, MOBILE_FIELD};

    // Product table and field names
    public static final String PRODUCTS_TABLE = "product"; // Change for different product table
    public static final String PRODUCT_ID_FIELD = "id";
    public static final String PRODUCT_TYPE_FIELD = "productType";
    public static final String PRODUCT_NAME_FIELD = "productName";
    public static final String PRODUCT_IMAGE_FIELD = "productImage";
    public static final String PRODUCT_PRICE_FIELD = "price";

    // Page paths (relative to context root)
    public static final String LOGIN_PAGE = "login.html";
    public static final String REGISTER_PAGE = "register.html";
    public static final String HOME_PAGE = "home.html";
    public static final String CART_PAGE = "cart.html";
    public static final String SEARCH_SERVLET = "search"; // URL mapping for SearchServlet
    public static final String CART_SERVLET = "cart";   // URL mapping for CartServlet

    // Session attribute names
    public static final String CART_ITEMS = "cart";         // List of product IDs
    public static final String CART_QUANTITIES = "quantities"; // Map of product ID to quantity

    // Message keys for feedback
    public static final String SUCCESS_PARAM = "message";
    public static final String ERROR_PARAM = "error";
}