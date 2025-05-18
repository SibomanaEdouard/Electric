package SUPERMENU.BACKEND_SERVICE.Response;




import SUPERMENU.BACKEND_SERVICE.RoleEnum;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    public UUID id;
    public RoleEnum role;
    public Long nationalid;


}


