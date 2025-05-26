package az.qrfood.backend.common.service;

import az.qrfood.backend.common.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;

@Service
public class StorageService {

    @Value("${folder.root.images.eatery}")
    private String APP_IMAGES_FOLDER_EATERY;
    @Value("${folder.root.images.categories}")
    private String APP_IMAGES_FOLDER_CATEGORIES;
    @Value("${folder.root.images.dishes}")
    private String APP_IMAGES_FOLDER_DISHES;

    /**
     * Creates folder to hold the images related to eatery with particular id.

     * @param eateryId eatery ID as folder name
     */
    public void createEateryFolder(Long eateryId) {
        String folder = APP_IMAGES_FOLDER_EATERY + File.separator + eateryId;
        Util.createFolderIfNotExists(folder);
    }


    /**
     * Saves category image.
     *
     * @param folderName category id as folder name
     */
    public String createCategoryFolder(Long folderName) {
        String folder = APP_IMAGES_FOLDER_CATEGORIES + File.separator + folderName;
        if(Util.createFolderIfNotExists(folder)) {
            return folder;
        } else {
            return null;
        }
    }

    /**
     * Saves category image.
     *
     * @param folderName dish id as folder name
     */
    public String createDishesFolder(Long folderName) {
        String folder = APP_IMAGES_FOLDER_DISHES + File.separator + folderName;
        if(Util.createFolderIfNotExists(folder)) {
            return folder;
        } else {
            return null;
        }
    }

    public void saveFile(String folder, MultipartFile file, String rename) {
        Util.saveFile(folder, file, rename);
    }

}