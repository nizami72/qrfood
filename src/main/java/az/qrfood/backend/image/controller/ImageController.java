package az.qrfood.backend.image.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
@Tag(name = "Image Management", description = "API endpoints for managing images")
public class ImageController {

    @Value("${folder.root.images.eatery}")
    private String eateryImagePath;
    @Value("${folder.root.images.categories}")
    private String categoryImagePath;
    @Value("${folder.root.images.dishes}")
    private String dishImagePath;
    @Value("${folder.predefined.category.images}")
    private String predefinedCatFolder;
    @Value("${folder.predefined.dish.images}")
    private String predefinedDishFolder;
    @Value("${fall.back.photo}")
    private String fallBackPhoto;

    /**
     * Retrieves an image for a specific eatery.
     * The image is identified by its directory (eatery ID) and file name.
     *
     * @param dir      The directory name, typically the eatery ID.
     * @param photo    The file name of the image.
     * @param response The {@link HttpServletResponse} to set content type.
     * @return A {@link ResponseEntity} containing the image as a byte array.
     */
    @Operation(summary = "Get eatery image", description = "Retrieves an image for a specific eatery by its ID and file name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the eatery image"),
            @ApiResponse(responseCode = "404", description = "Image not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = {"${eatery}/{id}/{fileName}"})
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
    @Operation(summary = "Get category image", description = "Retrieves an image for a specific category by its ID and file name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the category image"),
            @ApiResponse(responseCode = "404", description = "Image not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = {"/category/{id}/{fileName}"})
    public ResponseEntity<byte[]> getCategoryImage(@PathVariable("id") String dir,
                                                   @PathVariable("fileName") String photo, HttpServletResponse response) {
        log.debug("Requested category image [{}]", photo);
        String path = categoryImagePath + "/" + dir + "/" + photo;
        return getImage(path, response);
    }

    @Operation(summary = "Get predefined category image", description = "Retrieves a predefined category image by its file name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the predefined category image"),
            @ApiResponse(responseCode = "404", description = "Image not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = {"/predefined/category/{fileName}"})
    public ResponseEntity<byte[]> getPredefinedCatImage(@PathVariable("fileName") String fileName,
                                                        HttpServletResponse response) {

        log.debug("Requested predefined category image [{}]", fileName);
        String path = predefinedCatFolder + fileName;
        return getImage(path, response);
    }

    @Operation(summary = "Get predefined dish image", description = "Retrieves a predefined dish image by its file name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the predefined dish image"),
            @ApiResponse(responseCode = "404", description = "Image not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = {"/predefined/dish/{fileName}"})
    public ResponseEntity<byte[]> getPredefinedDishImage(@PathVariable("fileName") String fileName,
                                                         HttpServletResponse response) {

        log.debug("Requested predefined dish image [{}]", fileName);
        String path = predefinedDishFolder + fileName;
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
    @Operation(summary = "Get dish image", description = "Retrieves an image for a specific dish by its ID and file name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the dish image"),
            @ApiResponse(responseCode = "404", description = "Image not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = {"/dish/{id}/{fileName}"})
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
        log.debug("Requested image [{}]", imgPath);
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
