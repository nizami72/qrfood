package az.qrfood.backend.tool;

import az.qrfood.backend.constant.ApiRoutes;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsRoutesGenerator {

    // Укажите путь, куда сохранить JS файл (относительно корня проекта)
    private static final String OUTPUT_PATH = "qrfood/frontend/src/generated/api-routes.js";

    public static void main(String[] args) throws IOException {
        System.out.println("Generating JS routes from Java constants...");

        StringBuilder jsContent = new StringBuilder();
        jsContent.append("// THIS FILE IS AUTO-GENERATED DURING BUILD. DO NOT EDIT.\n");
        jsContent.append("// Source: az.qrfood.backend.constant.ApiRoutes\n\n");
        jsContent.append("export const ApiRoutes = {\n");

        Field[] fields = ApiRoutes.class.getDeclaredFields();
        List<String> lines = new ArrayList<>();

        for (Field field : fields) {
            // Берем только public static final String поля
            if (Modifier.isPublic(field.getModifiers()) &&
                Modifier.isStatic(field.getModifiers()) &&
                Modifier.isFinal(field.getModifiers()) &&
                field.getType().equals(String.class)) {

                try {
                    String name = field.getName();
                    String value = (String) field.get(null); // null, так как поле static
                    
                    // Форматируем строку для JS объекта
                    lines.add(String.format("  %s: '%s'", name, value));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        jsContent.append(String.join(",\n", lines));
        jsContent.append("\n};\n");

        // Сохранение файла
        Path path = Paths.get(args.length > 0 ? args[0] : OUTPUT_PATH);
        
        // Создаем директории, если их нет
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        
        Files.write(path, jsContent.toString().getBytes());
        
        System.out.println("Successfully generated: " + path.toAbsolutePath());
    }
}