package az.qrfood.backend.common;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import javax.imageio.ImageIO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * A utility class providing various helper methods for common tasks
 * such as object property copying, image manipulation, file operations,
 * and URL generation.
 */
@Log4j2
public class Util {

    private static final String LINKS_FILE_PATH = "links.md";

    /**
     * Copies properties from a source object to a new instance of a destination class.
     * This method uses Spring's {@link BeanUtils#copyProperties(Object, Object)} internally.
     *
     * @param source           The source object from which properties will be copied.
     * @param destinationClass The {@link Class} object representing the destination type.
     * @param <SR>             The type of the source object.
     * @param <DS>             The type of the destination object.
     * @return A new instance of the destination class with properties copied from the source,
     *         or {@code null} if an error occurs during instantiation.
     */
    public static <SR, DS> DS copyProperties(SR source, Class<DS> destinationClass) {
        DS target = null;
        try {
            target = destinationClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            log.error("Error instantiating [{}]", destinationClass, e);
        }
        assert target != null;
        BeanUtils.copyProperties(source, target);
        return target;
    }

    /**
     * Creates a {@link BufferedImage} from a byte array representing image data.
     *
     * @param imageBytes The byte array containing the image data.
     * @return A {@link BufferedImage} created from the provided bytes.
     * @throws IOException If an I/O error occurs during image reading.
     */
    public static BufferedImage createImageFromBytes(byte[] imageBytes) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes)) {
            return ImageIO.read(bis);
        }
    }

    /**
     * Saves a given URL to a markdown file named "links.md".
     * Each link is prepended with a timestamp and formatted as a markdown link.
     * The file is created if it does not already exist.
     *
     * @param url The URL string to be saved.
     */
    public static void saveLinkToFile(String url) {
        try {
            // Create the file if it does not exist
            Path path = Paths.get(LINKS_FILE_PATH);
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            // Format: link + timestamp
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String line = timestamp + " - " + "[" + url + "]" + "(" + url +")" + System.lineSeparator();

            // Append to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(LINKS_FILE_PATH, true))) {
                writer.write(line);
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("Error writing link: " + e.getMessage());
        }
    }

    /**
     * Creates a folder at the specified path if it does not already exist.
     *
     * @param folderName The absolute or relative path of the folder to create.
     * @return {@code true} if the folder exists after the operation (either created or already existed),
     *         {@code false} if the folder could not be created.
     */
    public static boolean createFolderIfNotExists(String folderName) {
        File folder = new File(folderName);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (created) {
                log.info("Folder created [{}]", folderName);
            } else {
                log.info("Failed to create folder at [{}] ", folderName);
            }
        } else {
            log.info("Folder already exists at [{}]", folderName);
        }
        return folder.exists();
    }

    /**
     * Saves a {@link MultipartFile} to a specified path, optionally renaming the file.
     *
     * @param path      The directory path where the file should be saved.
     * @param imageFile The {@link MultipartFile} to save.
     * @param rename    An optional new name for the file. If {@code null} or empty,
     *                  the original file name from {@code imageFile} will be used.
     * @return The absolute path to the saved file as a string.
     * @throws RuntimeException If an I/O error occurs during file storage or if the file name is invalid.
     */
    public static String saveFile(String path, MultipartFile imageFile, String rename) {

        String fileName;
        if (rename != null) {
            fileName = rename;
        } else {
            fileName = imageFile.getOriginalFilename();
        }

        try {
            if (fileName == null || fileName.isEmpty()) {
                throw new IOException("Invalid file name");
            }

            Path directory = Paths.get(path).toAbsolutePath().normalize();
            Files.createDirectories(directory);

            Path filePath = directory.resolve(fileName);
            imageFile.transferTo(filePath.toFile());
            return filePath.toString();

        } catch (IOException e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage(), e);
        }
    }

    /**
     * Generates a universally unique identifier (UUID) as a string.
     * This is commonly used for creating unique file names or identifiers.
     *
     * @return A randomly generated UUID string.
     */
    public static String generateFileName() {
        return UUID.randomUUID().toString();
    }

    /**
     * Constructs the full URL of the current request from an {@link HttpServletRequest} object.
     * This includes the scheme, server name, port (if not default), request URI, and query string.
     *
     * @param request The {@link HttpServletRequest} object.
     * @return The complete URL of the current request.
     */
    public static String getFullURL(HttpServletRequest request) {
        StringBuilder url = new StringBuilder();
        url.append(request.getScheme()).append("://").append(request.getServerName());

        int serverPort = request.getServerPort();
        // Append port only if it's not the default for HTTP (80) or HTTPS (443)
        if (serverPort != 80 && serverPort != 443) {
            url.append(":").append(serverPort);
        }

        url.append(request.getRequestURI());

        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            url.append('?').append(queryString);
        }
        return url.toString();
    }
}

