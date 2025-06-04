package az.qrfood.backend.common.controller;

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
    @Value("${full.path.fe.add.dishes.2.category}")
    private String urlAddDish;
    @Value("${full.path.fe.add.dish.2.order}")
    private String urlAddDish2Order;


    @GetMapping("/image-paths")
    public Map<String, String> getImagePaths() {
        Map<String, String> paths = new HashMap<>();
        paths.put("eatery", imagesEateryUri);
        paths.put("categories", imagesCategoriesUri);
        paths.put("dishes", imagesDishesUri);
        paths.put("tables", urlApiTables);
        paths.put("urlAddDish", urlAddDish);
        paths.put("urlAddDish2Order", urlAddDish2Order);
        log.debug("FE requested path config [{}]", paths);
        return paths;
    }
}
