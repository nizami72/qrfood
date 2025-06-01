package az.qrfood.backend.common.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@RestController
@RequestMapping("/api/config")
public class FrontendPathConfig {

    @Value("${full.path.fe.eatery}")
    private String imagesEateryUri;
    @Value("${full.path.fe.category}")
    private String imagesCategoriesUri;
    @Value("${full.path.fe.dish}")
    private String imagesDishesUri;

    @GetMapping("/image-paths")
    public Map<String, String> getImagePaths() {
        Map<String, String> paths = new HashMap<>();
        paths.put("eatery", imagesEateryUri);
        paths.put("categories", imagesCategoriesUri);
        paths.put("dishes", imagesDishesUri);
        return paths;
    }
}
