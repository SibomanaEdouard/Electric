package SUPERMENU.BACKEND_SERVICE.Dto;

import lombok.Data;

@Data
public class UpdateUserDto {
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private Long nationalid;
}
