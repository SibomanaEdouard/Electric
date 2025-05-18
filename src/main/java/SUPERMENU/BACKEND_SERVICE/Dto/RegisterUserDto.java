package SUPERMENU.BACKEND_SERVICE.Dto;




import SUPERMENU.BACKEND_SERVICE.RoleEnum;
import lombok.*;

import javax.management.relation.Role;

@Data
public class RegisterUserDto {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String phone;
    private Long nationalid;

}

