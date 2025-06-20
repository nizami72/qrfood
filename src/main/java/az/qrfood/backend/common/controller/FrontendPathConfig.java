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
    @Value("${segment.dishes}")
    private String urlAddDish;
    @Value("${full.path.fe.add.dish.2.order}")
    private String urlAddDish2Order;
    @Value("${full.path.fe.delete.menu-item}")
    private String urlDeleteMenuItem;
    @Value("${relative.path.api.client.eatery.arg.table.arg}")
    private String clientGetMenuUrl;
    @Value("${api.eatery.id}")
    String apiEateryId;

    @GetMapping("/image-paths")
    public Map<String, String> getImagePaths() {
        Map<String, String> paths = new HashMap<>();
        paths.put("eatery", imagesEateryUri);
        paths.put("categories", imagesCategoriesUri);
        paths.put("dishes", imagesDishesUri);
        paths.put("tables", urlApiTables);
        paths.put("urlAddDish", urlAddDish);
        paths.put("urlAddDish2Order", urlAddDish2Order);
        paths.put("urlDeleteMenuItemFromOrder", urlDeleteMenuItem);
        paths.put("clientGetMenuUrl", clientGetMenuUrl);
        paths.put("apiEateryId", apiEateryId);
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
        }
        return out;
    }
}
