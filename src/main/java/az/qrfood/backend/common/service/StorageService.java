package az.qrfood.backend.common.service;

import az.qrfood.backend.common.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;

@Service
public class StorageService {

    @Value("${folder.root.images.eatery}")
    private String APP_IMAGES_FOLDER;

    /**
     * Creates folder to hold the images related to eatery with particular id.

     * @param eateryId eatery ID
     */
    public void createEateryFolder(Long eateryId) {
        String folder = APP_IMAGES_FOLDER + File.separator + eateryId;
        Util.createFolderIfNotExists(folder);
    }

    public void createCategoryFolder(Long eateryId, Long categoryId) {
        String folder = APP_IMAGES_FOLDER + File.separator + eateryId + File.separator + categoryId;
        Util.createFolderIfNotExists(folder);
    }

    public void saveCategoryFile(Long eateryId, Long categoryId, MultipartFile fileName) {
        String folder = APP_IMAGES_FOLDER + File.separator + eateryId + File.separator + categoryId;
        Util.saveFile(folder, fileName);
    }
}