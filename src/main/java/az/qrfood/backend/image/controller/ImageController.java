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

/**
 * REST controller for serving image files.
 * <p>
 * This controller provides endpoints to retrieve images for eateries, categories, and dishes.
 * It handles file retrieval from the local file system and serves them as byte arrays.
 * A fallback image is provided if the requested image is not found.
 * </p>
 */
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

    /**
     * Retrieves a map of base image paths configured for different entities.
     *
     * @return A {@link Map} containing the base paths for eatery, category, and dish images.
     */
    public Map<String, String> getImagePaths() {
        Map<String, String> paths = new HashMap<>();
        paths.put("eatery", eateryImagePath);
        paths.put("categories", categoryImagePath);
        paths.put("dishes", dishImagePath);
        return paths;
    }

    /**
     * Retrieves an image for a specific eatery.
     * The image is identified by its directory (eatery ID) and file name.
     *
     * @param dir      The directory name, typically the eatery ID.
     * @param photo    The file name of the image.
     * @param response The {@link HttpServletResponse} to set content type.
     * @return A {@link ResponseEntity} containing the image as a byte array.
     */
    @RequestMapping(value = {"${eatery}/{id}/{fileName}"})
    public ResponseEntity<byte[]> getEateryImage(@PathVariable("id") String dir,
                                                 @PathVariable("fileName") String photo, HttpServletResponse response) {
        log.debug("Requested eatery image [{}]", photo);
        String path = eateryImagePath + "/" + dir + "/" + photo;
        return getImage(path, response);
    }

    /**
     * Retrieves an image for a specific category.
     * The image is identified by its directory (category ID) and file name.
     *
     * @param dir      The directory name, typically the category ID.
     * @param photo    The file name of the image.
     * @param response The {@link HttpServletResponse} to set content type.
     * @return A {@link ResponseEntity} containing the image as a byte array.
     */
    @RequestMapping(value = {"/category/{id}/{fileName}"})
    public ResponseEntity<byte[]> getCategoryImage(@PathVariable("id") String dir,
                                                 @PathVariable("fileName") String photo, HttpServletResponse response) {
        log.debug("Requested category image [{}]", photo);
        String path = categoryImagePath + "/" + dir + "/" + photo;
        return getImage(path, response);
    }

    /**
     * Retrieves an image for a specific dish.
     * The image is identified by its directory (dish ID) and file name.
     *
     * @param dir      The directory name, typically the dish ID.
     * @param photo    The file name of the image.
     * @param response The {@link HttpServletResponse} to set content type.
     * @return A {@link ResponseEntity} containing the image as a byte array.
     */
    @RequestMapping(value = {"/dish/{id}/{fileName}"})
    public ResponseEntity<byte[]> getDishImage(@PathVariable("id") String dir,
                                                 @PathVariable("fileName") String photo, HttpServletResponse response) {
        log.debug("Requested dish image [{}]", photo);
        String path = dishImagePath + "/" + dir + "/" + photo;
        return getImage(path, response);
    }

    /**
     * Helper method to retrieve an image from the file system.
     * If the image is not found, a fallback image is loaded.
     *
     * @param imgPath  The full path to the image file.
     * @param response The {@link HttpServletResponse} to set content type.
     * @return A {@link ResponseEntity} containing the image data.
     */
    private ResponseEntity<byte[]> getImage(String imgPath, HttpServletResponse response) {
        byte[] data;
        try {
            data = Files.readAllBytes(Paths.get(imgPath));
        } catch (IOException ioe) {
            log.warn("Unable to get image from {} caused by {}", imgPath, ioe.getClass().getName());
            data = loadDefaultImage(response);
        }
        return ResponseEntity.ok().contentType(MediaType.valueOf("image/webp")).body(data);
    }

    /**
     * Loads a default fallback image if the requested image is not found.
     * It attempts to find the fallback image with common image extensions.
     *
     * @param response The {@link HttpServletResponse} to set status if fallback image is not found.
     * @return A byte array of the fallback image, or {@code null} if no fallback image can be loaded.
     */
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
