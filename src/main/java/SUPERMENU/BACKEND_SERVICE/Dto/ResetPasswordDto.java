package SUPERMENU.BACKEND_SERVICE.Dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordDto {

    @Email
    @NotBlank
    private String email;
    @Size(min = 8, max = 100)
    private String new_password;
    private String confirm_password;
}
