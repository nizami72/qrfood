package az.qrfood.backend.alive.dto;

public record Alive(String appName,
                    String version,
                    String message,
                    String commitDate,
                    String now) {
}
