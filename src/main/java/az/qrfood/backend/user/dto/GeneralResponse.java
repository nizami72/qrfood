package az.qrfood.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
public class GeneralResponse<T> {

    private String timestamp = OffsetDateTime.now().toString();
    private int status;
    private String code;
    private String message;
    private T data;
    private String path;



    private boolean success;

    public GeneralResponse(int status, String code, String message, T data, String path) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
        this.path = path;
    }

}
