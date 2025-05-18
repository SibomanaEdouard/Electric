package SUPERMENU.BACKEND_SERVICE.Response;


import org.springframework.http.HttpStatus;
import  lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String error;
    private HttpStatus status;
    private String message;
    private String headers;

    // Constructor for success response
    public ApiResponse(T data) {
        this.success = true;
        this.data = data;
        this.error = null;
    }

    // Constructor for error response
    public ApiResponse(String errorMessage) {
        this.success = false;
        this.error = errorMessage;
    }
}




