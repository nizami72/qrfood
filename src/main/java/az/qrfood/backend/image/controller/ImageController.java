package az.qrfood.backend.image.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${image}")
@Log4j2
public class ImageController {

    @Value("${folder.root.images.eatery}")
    private String eateryImagePath;
    @Value("${folder.root.images.categories}")
    private String categoryImagePath;
    @Value("${folder.root.images.dishes}")
    private String dishImagePath;
    @Value("${fall.back.photo}")
    private String fallBackPhoto;

    public Map<String, String> getImagePaths() {
        Map<String, String> paths = new HashMap<>();
        paths.put("eatery", eateryImagePath);
        paths.put("categories", categoryImagePath);
        paths.put("dishes", dishImagePath);
        return paths;
    }

    @RequestMapping(value = {"${eatery}/{id}/{fileName}"})
    public ResponseEntity<byte[]> getEateryImage(@PathVariable("id") String dir,
                                                 @PathVariable("fileName") String photo, HttpServletResponse response) {
        log.debug("Requested eatery image [{}]", photo);
        String path = eateryImagePath + "/" + dir + "/" + photo;
        return getImage(path, response);
    }

    @RequestMapping(value = {"/category/{id}/{fileName}"})
    public ResponseEntity<byte[]> getCategoryImage(@PathVariable("id") String dir,
                                                 @PathVariable("fileName") String photo, HttpServletResponse response) {
        log.debug("Requested category image [{}]", photo);
        String path = categoryImagePath + "/" + dir + "/" + photo;
        return getImage(path, response);
    }

    @RequestMapping(value = {"/dish/{id}/{fileName}"})
    public ResponseEntity<byte[]> getDishImage(@PathVariable("id") String dir,
                                                 @PathVariable("fileName") String photo, HttpServletResponse response) {
        log.debug("Requested dish image [{}]", photo);
        String path = dishImagePath + "/" + dir + "/" + photo;
        return getImage(path, response);
    }

    private ResponseEntity<byte[]> getImage(String imgPath, HttpServletResponse response) {
        byte[] data = null;
        try {
            data = Files.readAllBytes(Paths.get(imgPath));
        } catch (IOException ioe) {
            log.warn("Unable to get image from {} caused by {}", imgPath, ioe.getClass().getName());
            data = loadDefaultImage(response);
        }
        return ResponseEntity.ok().contentType(MediaType.valueOf("image/webp")).body(data);
    }

    private byte[] loadDefaultImage(HttpServletResponse response) {
        byte[] data = null;
        try {
            log.warn("Try to get fall back photo");
            String[] ptoFileExtensions = {".jpg", ".jpeg", ".png"};
            for (String ptoFileExtension : ptoFileExtensions) {
                if (new File(fallBackPhoto + ptoFileExtension).exists()) {
                    data = Files.readAllBytes(Paths.get(fallBackPhoto + ptoFileExtension));
                    log.warn("Fall back photo returned");
                    break;
                }
            }
        } catch (IOException ex) {
            log.error("Unable to get fallback photo from {} caused by {}", fallBackPhoto, ex.getClass().getName());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return data;
    }

}
