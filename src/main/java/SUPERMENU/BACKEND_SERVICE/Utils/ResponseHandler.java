package SUPERMENU.BACKEND_SERVICE.Utils;






import SUPERMENU.BACKEND_SERVICE.Response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHandler {
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, HttpStatus status) {
        return ResponseEntity.status(status).body(new ApiResponse<>(data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(String errorMessage, HttpStatus status) {
        ApiResponse<T> response = new ApiResponse<>(errorMessage);
        return ResponseEntity.status(status).body(response);
    }
}


