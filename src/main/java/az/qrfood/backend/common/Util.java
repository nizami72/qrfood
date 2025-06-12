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
@Log4j2
public class Util {

    private static final String LINKS_FILE_PATH = "links.md";


    public static <SR, DS> DS copyProperties(SR source, Class<DS> destinationClass) {
        DS target = null;
        try {
            target = destinationClass.getDeclaredConstructor().newInstance();
            // use instance
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            log.error("Error instantiating " + destinationClass, e);
        }
        assert target != null;
        BeanUtils.copyProperties(source, target);
        return target;
    }

    public static BufferedImage createImageFromBytes(byte[] imageBytes) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes)) {
            return ImageIO.read(bis);
        }
    }


    public static void saveLinkToFile(String url) {
        try {
            // Создание файла, если не существует
            Path path = Paths.get(LINKS_FILE_PATH);
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            // Форматирование: ссылка + временная метка
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String line = timestamp + " - " + "[" +url + "]" + "(" + url +")";

            // Запись в конец файла
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(LINKS_FILE_PATH, true))) {
                writer.write(line);
                writer.newLine();
            }

            System.out.println("Saved link to: " + LINKS_FILE_PATH);

        } catch (IOException e) {
            System.err.println("Error writing link: " + e.getMessage());
        }
    }

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

    public static String generateFileName() {
        return UUID.randomUUID().toString();
    }

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
