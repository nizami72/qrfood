package az.qrfood.backend.common.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Setter
@Getter
@RestController
@Log4j2
@RequestMapping("/api/config")
public class FrontendPathConfig {

    @Value("${full.path.fe.eatery.image}")
    private String imagesEateryUri;
    @Value("${full.path.fe.category.image}")
    private String imagesCategoriesUri;
    @Value("${full.path.fe.dish.image}")
    private String imagesDishesUri;
    @Value("${full.path.fe.api.tables.image}")
    private String urlApiTables;
    @Value("${full.path.fe.add.dish.2.order}")
    private String urlAddDish2Order;
    @Value("${full.path.fe.delete.menu-item}")
    private String urlDeleteMenuItem;
    @Value("${relative.path.api.client.eatery.arg.table.arg}")
    private String clientGetMenuUrl;

    // new new

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
    @Value("${order.item.order.id}")
    String orderItemOrderId;
    @Value("${order.item.id}")
    String orderItemId;
    @Value("${order.item}")
    String orderItem;


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
        paths.put("ORDER ITEMS", "--------------------");
        paths.put("orderItem", orderItem);
        paths.put("orderItemId", orderItemId);
        paths.put("orderItemOrderId", orderItemOrderId);
        //.



        log.debug("FE requested path config [{}]", prettyPrintMao(paths));
        return paths;
    }

    private String prettyPrintMao(Map<String, String> map) {
        String out = "Unable to pretty print map";
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // включаем отступы
        try {
            out = mapper.writeValueAsString(map);
        } catch (JsonProcessingException ex) {
            log.error(ex.getMessage());
            return out;
        }
        return out;
    }
}
