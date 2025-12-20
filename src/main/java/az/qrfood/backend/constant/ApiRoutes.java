package az.qrfood.backend.constant;

public final class ApiRoutes {

    private ApiRoutes() {}

    public static final String API = "/api";
    public static final String V1 = "/v1"; // Хорошая практика добавлять версию

    // --- AUTH MANAGEMENT ---
    public static final String AUTH = API + "/auth";
    public static final String AUTH_LOGIN = AUTH + "/login";
    public static final String AUTH_LOGOUT = AUTH + "/logout";
    public static final String AUTH_STATUS = AUTH + "/status";
    public static final String AUTH_REFRESH_ON_EATERY_CHANGE = AUTH + "/recreate-token-on-eatery-change";
    public static final String AUTH_REFRESH_TOKEN = AUTH + "/refresh-token";
    public static final String AUTH_MAGIC_LINK = AUTH + "/magic-link";
    public static final String AUTH_TEST_MAGIC_LINK = AUTH + "/test-magic-link";
    public static final String AUTH_VERIFY_TOKEN = AUTH + "/verify-token";
    public static final String AUTH_OAUTH_GOOGLE = AUTH + "/oauth/google";
    public static final String AUTH_PASSWORD_RESET_REQUEST = AUTH + "/password-reset/request";
    public static final String AUTH_PASSWORD_RESET_COMPLETE = AUTH + "/password-reset/complete";
    public static final String AUTH_CHANGE_PASSWORD = AUTH + "/change-password";

    // --- EATERY MANAGEMENT ---
    public static final String EATERY = API + "/eatery";
    public static final String EATERY_BY_ID = EATERY + "/{eateryId}";
    public static final String EATERY_BY_OWNER = EATERY + "/owner/{ownerId}";
    public static final String EATERY_STATUS = EATERY + "/status/{eateryId}";

    // --- CATEGORY MANAGEMENT ---
    public static final String CATEGORY_BASE = "/category"; // Обратите внимание, в оригинале было /category без \апи
    public static final String EATERY_CATEGORY = EATERY_BY_ID + "/category";
    public static final String EATERY_CATEGORY_PREDEFINED = EATERY_CATEGORY + "/predefined";
    public static final String EATERY_CATEGORY_BY_ID = EATERY_CATEGORY + "/{categoryId}";
    public static final String API_CATEGORY_COMMON = API + "/category/common";

    // --- DISH MANAGEMENT ---
    public static final String DISH_BASE = EATERY_CATEGORY_BY_ID + "/dish";
    public static final String DISH_BY_ID = DISH_BASE + "/{dishId}";
    public static final String DISH_COMMON_FROM_TEMPLATE = API + "/dish/common/eatery/{eateryId}/category/{categoryId}";
    public static final String DISH_COMMON_BY_CAT_ID = API + "/dish/common/{categoryId}";
    public static final String DISH_COMMON_BY_CAT_NAME = API + "/dish/common/{categoryName}";

    // --- TABLE MANAGEMENT ---
    public static final String TABLE = EATERY_BY_ID + "/table";
    public static final String TABLE_BY_ID = TABLE + "/{tableId}";

    // --- TABLE ASSIGNMENT ---
    public static final String TABLE_ASSIGNMENT = EATERY_BY_ID + "/table-assignment";
    public static final String TABLE_ASSIGNMENT_WAITER = TABLE_ASSIGNMENT + "/waiter/{waiterId}";
    public static final String TABLE_ASSIGNMENT_TABLE = TABLE_ASSIGNMENT + "/table/{tableId}";
    public static final String TABLE_ASSIGNMENT_BY_ID = TABLE_ASSIGNMENT + "/{assignmentId}";

    // --- ORDER MANAGEMENT ---
    public static final String ORDER = EATERY_BY_ID + "/order";
    public static final String ORDERS = EATERY_BY_ID + "/orders"; // GET ALL
    public static final String ORDER_POST = ORDER + "/post";
    public static final String ORDER_BY_TABLE = ORDER + "/table/{tableId}";
    public static final String ORDER_BY_ID = ORDER + "/{orderId}";
    public static final String ORDER_DELETE = ORDER_BY_ID + "/delete";
    public static final String ORDER_PUT = ORDER_BY_ID + "/put";
    public static final String ORDER_ADD_DISHES = ORDER_BY_ID + "/add-dishes";
    public static final String ORDER_STATUS_AUTH = ORDER + "/status/{status}/auth";
    public static final String ORDER_STATUS = ORDER + "/status/{status}";
    public static final String DEVICE_ORDERS_CREATED = EATERY_BY_ID + "/table/{tableId}/device-orders";

    // --- ORDER ITEM MANAGEMENT ---
    public static final String ORDER_ITEM = EATERY_BY_ID + "/order-item";
    public static final String ORDER_ITEM_BY_ID = ORDER_ITEM + "/{orderItemId}";
    public static final String ORDER_ITEM_BY_ORDER_ID = ORDER_ITEM + "/order/{orderId}";

    // --- USER MANAGEMENT ---
    public static final String API_USER = API + "/user";
    public static final String USER_AND_EATERY = "/user/eatery";
    public static final String USER_BY_EATERY = EATERY_BY_ID + "/user";
    public static final String USER_GENERAL_REGISTER = USER_BY_EATERY + "/register/general";
    public static final String USER_DELETE_GLOBAL = API_USER + "/{id}";
    public static final String USER_BY_ID = USER_BY_EATERY + "/{userId}";
    public static final String USER_BY_NAME = USER_BY_EATERY + "/name/{userName}";

    // --- ADMIN MANAGEMENT ---
    public static final String ADMIN_EATERY_RESOURCES = API + "/admin/eatery/{eateryId}/resources";
    public static final String ADMIN_EATERY_DETAILS = API + "/admin/eatery/{eateryId}/details";
    public static final String ADMIN_EATERY = API + "/admin/eatery/{eateryId}";
    public static final String ADMIN_EATERY_ADMINS = API + "/admin/eatery-admin/{admin}";
    public static final String ADMIN_REGISTER_SUCCESS = "/admin/register/success";

    // --- CLIENT & KITCHEN ---
    public static final String CLIENT_BASE = API + "/client";
    public static final String CLIENT_BY_EATERY = CLIENT_BASE + "/eatery/{eateryId}";
    public static final String CLIENT_BY_TABLE = CLIENT_BY_EATERY + "/table/{tableId}";
    public static final String CLIENT_BY_ID = CLIENT_BASE + "/{id}";
    
    public static final String KITCHEN_DEPT = EATERY_BY_ID + "/kitchen-department";
    public static final String KITCHEN_DEPT_ID = KITCHEN_DEPT + "/{departmentId}";

    // --- IMAGES & QR ---
    public static final String IMAGE_BASE = API + "/image";
    public static final String IMAGE_WILDCARD = IMAGE_BASE + "/**";
    public static final String IMAGE_EATERY = IMAGE_BASE + "/eatery/{eateryId}/file/{fileName}";
    public static final String IMAGE_CATEGORY = IMAGE_BASE + "/eatery/{eateryId}/category/{categoryId}/file/{fileName}";
    public static final String IMAGE_DISH = IMAGE_BASE + "/eatery/{eateryId}/dish/{dishId}/file/{fileName}";
    public static final String IMAGE_PREDEFINED_CAT = IMAGE_BASE + "/predefined/category/{fileName}";
    public static final String IMAGE_PREDEFINED_DISH = IMAGE_BASE + "/predefined/dish/{fileName}";
    
    public static final String QR_CODE = API + "/qrcode/eatery/{eateryId}/table/{tableId}";
    public static final String QR_CODE_CONTENTS = API + "/qrcode/eatery/{eateryId}/contents";

    // --- MISC ---
    public static final String ALIVE = API + "/alive";
    public static final String LOGS_FRONTEND = API + "/logs/frontend";
    public static final String CONFIG_IMAGE_PATHS = API + "/config/image-paths";
}