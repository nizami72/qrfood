package az.qrfood.backend.common.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * REST controller that exposes frontend path configurations to the client.
 * <p>
 * This controller provides various URI paths and segments used by the frontend
 * application to construct API requests and image URLs. This centralizes path
 * management and makes it easier to update frontend configurations without
 * redeploying the frontend application.
 * </p>
 */
@Setter
@Getter
@RestController
@Log4j2
@RequestMapping("/api/config")
@Tag(name = "Frontend Configuration", description = "API endpoints for retrieving frontend path configurations")
public class FrontendPathConfig {

    // Image paths
    @Value("${full.path.fe.eatery.image}")
    private String imagesEateryUri;
    @Value("${full.path.fe.category.image}")
    private String imagesCategoriesUri;
    @Value("${full.path.fe.dish.image}")
    private String imagesDishesUri;
    @Value("${url.fe.predefined.category.image}")
    private String imagesPredefinedCategoryUri;
    @Value("${url.fe.predefined.dish.image}")
    private String imagesPredefinedDishUri;

    // API URLs for specific actions
    @Value("${full.path.fe.api.tables.image}")
    private String urlApiTables;
    @Value("${full.path.fe.add.dish.2.order}")
    private String urlAddDish2Order;
    @Value("${full.path.fe.delete.menu-item}")
    private String urlDeleteMenuItem;
    @Value("${relative.path.api.client.eatery.arg.table.arg}")
    private String clientGetMenuUrl;

    // New API path segments (from application.properties)
    @Value("${eatery}")
    String eatery;
    @Value("${eatery.id}")
    String eateryId;
    @Value("${eatery.owner}")
    String eateryOwner;
    @Value("${eatery.id.category}")
    String eateryIdCategory;
    @Value("${eatery.id.category.id}")
    String eateryIdCategoryId;
    @Value("${eatery.id.category.id.dish}")
    String eateryIdCategoryIdDish;
    @Value("${eatery.id.category.id.dish.id}")
    String eateryIdCategoryIdDishId;
    @Value("${table}")
    String eateryIdTable;
    @Value("${table.id}")
    String eateryIdTableId;
    @Value("${order.status}")
    String orderStatus;
    @Value("${order.id}")
    String orderId;
    @Value("${order.table.id}")
    String orderTableId;
    @Value("${order}")
    String order;
    @Value("${order.post}")
    String orderPost;
    @Value("${order.item.order.id}")
    String orderItemOrderId;
    @Value("${order.item.id}")
    String orderItemId;
    @Value("${order.item}")
    String orderItem;
    @Value("${user.n}")
    String userN;
    @Value("${users}")
    String users;
    @Value("${user.id}")
    String userId;
    @Value("${user.general}")
    String userGeneral;
    @Value("${usr}")
    String usr;
    @Value("${full.admin.eatery}")
    String apiAdminEatery;
    @Value("${api.user}")
    String apiUser;



    /**
     * Retrieves a map of image and API paths used by the frontend.
     * <p>
     * This endpoint provides a centralized way for the frontend to fetch
     * dynamic path configurations, reducing hardcoding and simplifying updates.
     * </p>
     *
     * @return A {@link Map} where keys are descriptive names and values are the corresponding URI paths.
     */
    @Operation(summary = "Get frontend path configurations", description = "Retrieves a map of image and API paths used by the frontend application.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved path configurations"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/image-paths")
    public Map<String, String> getImagePaths() {
        Map<String, String> paths = new LinkedHashMap<>();

        paths.put("OLD PATHS", "----------------------");
        paths.put("eateryImage", imagesEateryUri);
        paths.put("categories", imagesCategoriesUri);
        paths.put("dishes", imagesDishesUri);
        paths.put("tables", urlApiTables);
        paths.put("urlAddDish2Order", urlAddDish2Order);
        paths.put("urlDeleteMenuItemFromOrder", urlDeleteMenuItem);
        paths.put("clientGetMenuUrl", clientGetMenuUrl);
        paths.put("______________", "_________________");

        //NEW
        paths.put("EATERY", "--------------------");
        paths.put("eatery", eatery);
        paths.put("eateryId", eateryId);
        paths.put("eateryOwner", eateryOwner);
        paths.put("eateryIdCategory", eateryIdCategory);
        paths.put("eateryIdCategoryId", eateryIdCategoryId);
        paths.put("eateryIdCategoryIdDish", eateryIdCategoryIdDish);
        paths.put("eateryIdCategoryIdDishId", eateryIdCategoryIdDishId);
        paths.put("eateryIdTable", eateryIdTable);
        paths.put("eateryIdTableId", eateryIdTableId);
        paths.put("ORDERS", "--------------------");
        paths.put("orderStatus", orderStatus);
        paths.put("orderId", orderId);
        paths.put("orderTableId", orderTableId);
        paths.put("order", order);
        paths.put("orderPost", orderPost);
        paths.put("ORDER ITEMS", "--------------------");
        paths.put("orderItem", orderItem);
        paths.put("orderItemId", orderItemId);
        paths.put("orderItemOrderId", orderItemOrderId);
        paths.put("USERS", "--------------------");
        paths.put("userN", userN);
        paths.put("users", users);
        paths.put("userId", userId);
        paths.put("userGeneral", userGeneral);
        paths.put("usr", usr);
        paths.put("apiAdminEatery", apiAdminEatery);
        paths.put("predefinedCat", imagesPredefinedCategoryUri);
        paths.put("predefinedDish", imagesPredefinedDishUri);
        paths.put("apiUser", apiUser);
//        paths.put("", );
        //.


        log.debug("FE requested path config [{}]", prettyPrintMao(paths));
        return paths;
    }

    /**
     * Pretty prints a map of strings to strings as a JSON string with indentation.
     *
     * @param map The map to pretty print.
     * @return A pretty-printed JSON string representation of the map, or an error message if serialization fails.
     */
    private String prettyPrintMao(Map<String, String> map) {
        String out = "Unable to pretty print map";
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // Enable indentation
        try {
            out = mapper.writeValueAsString(map);
        } catch (JsonProcessingException ex) {
            log.error(ex.getMessage());
            return out;
        }
        return out;
    }
}
