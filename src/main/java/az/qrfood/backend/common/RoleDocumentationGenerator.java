package az.qrfood.backend.common;

import lombok.extern.log4j.Log4j2;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.security.RolesAllowed;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Log4j2
public class RoleDocumentationGenerator implements ApplicationRunner {

    private final Environment environment;

    @Autowired
    public RoleDocumentationGenerator(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        Set<String> urls = new TreeSet<>();
        try (PrintWriter writer = new PrintWriter(new FileWriter("END_POINTS_AND_ROLES.md"))) {

            Reflections reflections = new Reflections("az.qrfood.backend");
            Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(RestController.class);

            Map<String, List<EndpointInfo>> grouped = new TreeMap<>();

            for (Class<?> controller : controllers) {
                String basePath = Optional.ofNullable(controller.getAnnotation(RequestMapping.class))
                        .map(mapping -> mapping.value().length > 0 ? resolvePath(mapping.value()[0]) : "")
                        .orElse("");

                for (Method method : controller.getDeclaredMethods()) {
                    if (!Modifier.isPublic(method.getModifiers())) continue;

                    String subPath = getPathFromMethod(method);
                    String fullPath = normalizePath(basePath + subPath);
                    String roles = extractRoles(method);
                    String httpMethod = extractHttpMethod(method);
                    String methodSignature = formatMethodSignature(method);
                    String sourceFile = "src/main/java/" + controller.getName().replace('.', '/') + ".java";

                    EndpointInfo info = new EndpointInfo(
                            httpMethod,
                            methodSignature,
                            roles.isEmpty() ? "public" : roles,
                            fullPath,
                            sourceFile
                    );

                    grouped.computeIfAbsent(
                            controller.getSimpleName(),
                            k -> new ArrayList<>()).add(info);
                }
            }

            for (Map.Entry<String, List<EndpointInfo>> entry : grouped.entrySet()) {
                writer.println("### " + entry.getKey());
                writer.println();
                writer.println("| Method | Role(s) | URL Path |");
                writer.println("|--------|---------|----------|");

                List<String> out = new ArrayList<>();
                for (EndpointInfo info : entry.getValue()) {
                    String mdLink = String.format("[%s %s](%s)", info.httpMethod, info.methodName, info.sourceFileLink);
                    if (mdLink.contains("UNSPECIFIED")) continue;
                    String s = String.format("| %s | `%s` | `%s` |\n", mdLink, info.roles, info.path);
                    out.add(s);
                    urls.add(info.path);
                }

                Collections.sort(out);
                for (String mdLink : out) {
                    writer.printf(mdLink);
                }


                writer.println("\n---\n");
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate documentation", e);
        }


        try (PrintWriter writer = new PrintWriter(new FileWriter("END_POINTS.md"))) {

            writer.println("### End Points");
            for (String mdLink : urls) {
                writer.println("* " + mdLink);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate documentation", e);
        }

    }

    private String resolvePath(String rawPath) {
        if (rawPath.startsWith("${")) {
            return environment.resolvePlaceholders(rawPath);
        }
        return rawPath;
    }

    private String getPathFromMethod(Method method) {
        for (Class<? extends Annotation> mappingClass : List.of(
                GetMapping.class, PostMapping.class, PutMapping.class,
                DeleteMapping.class, PatchMapping.class, RequestMapping.class
        )) {
            Annotation annotation = method.getAnnotation(mappingClass);
            if (annotation != null) {
                try {
                    Method valueMethod = annotation.annotationType().getMethod("value");
                    String[] values = (String[]) valueMethod.invoke(annotation);
                    if (values.length > 0) {
                        return resolvePath(values[0]);
                    }
                } catch (Exception e) {
                    return "";
                }
            }
        }
        return "";
    }

    private String extractRoles(Method method) {
        String roles = "";

        PreAuthorize pre = method.getAnnotation(PreAuthorize.class);
        if (pre != null) {
            roles = extractFromPreAuthorize(pre.value());
        }

        RolesAllowed ra = method.getAnnotation(RolesAllowed.class);
        if (ra != null) {
            roles = String.join(", ", ra.value());
        }

        Secured secured = method.getAnnotation(Secured.class);
        if (secured != null) {
            roles = String.join(", ", secured.value());
        }

        return roles;
    }

    private String extractFromPreAuthorize(String value) {
        Pattern p = Pattern.compile("'(\\w+)'");
        Matcher m = p.matcher(value);
        List<String> roles = new ArrayList<>();
        while (m.find()) {
            roles.add(m.group(1));
        }
        return String.join(", ", roles);
    }

    private String extractHttpMethod(Method method) {
        if (method.isAnnotationPresent(GetMapping.class)) return "GET";
        if (method.isAnnotationPresent(PostMapping.class)) return "POST";
        if (method.isAnnotationPresent(PutMapping.class)) return "PUT";
        if (method.isAnnotationPresent(DeleteMapping.class)) return "DELETE";
        if (method.isAnnotationPresent(PatchMapping.class)) return "PATCH";

        RequestMapping rm = method.getAnnotation(RequestMapping.class);
        if (rm != null && rm.method().length > 0) {
            return rm.method()[0].name();
        }
        return "UNSPECIFIED";
    }

    private String formatMethodSignature(Method method) {
        String params = Arrays.stream(method.getParameterTypes())
                .map(Class::getSimpleName)
                .collect(Collectors.joining(", "));
        return method.getName() + "(" + params + ")";
    }

    private String normalizePath(String path) {
        return path.replaceAll("//+", "/").replaceAll("/$", "");
    }

    private static class EndpointInfo {
        String httpMethod;
        String methodName;
        String roles;
        String path;
        String sourceFileLink;

        public EndpointInfo(String httpMethod, String methodName, String roles, String path, String sourceFileLink) {
            this.httpMethod = httpMethod;
            this.methodName = methodName;
            this.roles = roles;
            this.path = path;
            this.sourceFileLink = sourceFileLink;
        }
    }
}
