package az.qrfood.backend.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title = "QR Order API",
        version = "1.0",
        description = "Система бесконтактного заказа в ресторане через QR-код"
    )
)

@Configuration
public class OpenApiConfig {
}
