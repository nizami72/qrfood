package az.qrfood.backend.common;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Log4j2
public class Util {

    public static <SR, DS> DS copyProperties(SR source, Class<DS> destinationClass) {
        DS target = null;
        try {
            target = destinationClass.getDeclaredConstructor().newInstance();
            // use instance
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
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

    private static final String LINKS_FILE_PATH = "/home/nizami/txt/tmp/links.txt";

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
            String line = timestamp + " - " + url;

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
}
