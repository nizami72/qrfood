package az.qrfood.backend.common.service;

import az.qrfood.backend.common.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;

/**
 * Service class for managing file storage operations within the application.
 * <p>
 * This service handles the creation of specific folders for different types of images
 * (eateries, categories, dishes) and provides a method for saving multipart files.
 * </p>
 */
@Service
public class StorageService {

    @Value("${folder.root.images.eatery}")
    private String APP_IMAGES_FOLDER_EATERY;
    @Value("${folder.root.images.categories}")
    private String APP_IMAGES_FOLDER_CATEGORIES;
    @Value("${folder.root.images.dishes}")
    private String APP_IMAGES_FOLDER_DISHES;

    /**
     * Creates a dedicated folder for storing images related to a specific eatery.
     * The folder name will be the eatery's ID.
     *
     * @param eateryId The ID of the eatery for which to create the folder.
     */
    public void createEateryFolder(Long eateryId) {
        String folder = APP_IMAGES_FOLDER_EATERY + File.separator + eateryId;
        Util.createFolderIfNotExists(folder);
    }


    /**
     * Creates a dedicated folder for storing images related to a specific category.
     * The folder name will be the category's ID.
     *
     * @param folderName The ID of the category, used as the folder name.
     * @return The absolute path to the created folder if successful, otherwise {@code null}.
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
     * Creates a dedicated folder for storing images related to a specific dish.
     * The folder name will be the dish's ID.
     *
     * @param folderName The ID of the dish, used as the folder name.
     * @return The absolute path to the created folder if successful, otherwise {@code null}.
     */
    public String createDishesFolder(Long folderName) {
        String folder = APP_IMAGES_FOLDER_DISHES + File.separator + folderName;
        if(Util.createFolderIfNotExists(folder)) {
            return folder;
        } else {
            return null;
        }
    }

    /**
     * Saves a {@link MultipartFile} to the specified folder, optionally renaming the file.
     *
     * @param folder The destination folder path.
     * @param file   The {@link MultipartFile} to save.
     * @param rename An optional new name for the file. If {@code null}, the original file name is used.
     */
    public void saveFile(String folder, MultipartFile file, String rename) {
        Util.saveFile(folder, file, rename);
    }


    /**
     * Saves a {@link MultipartFile} to the specified folder, optionally renaming the file.
     *
     * @param folder The destination folder path.
     * @param filePath   The {@link MultipartFile} to save.
     */
    public void saveFile(String folder, String filePath, String fileName) {
        Util.saveFile(folder, filePath, fileName);
    }

    public void deleteAllAndSaveFile(String folder, MultipartFile file, String rename) {
        Util.deleteFiles(folder);
        saveFile(folder, file, rename);
    }

}
