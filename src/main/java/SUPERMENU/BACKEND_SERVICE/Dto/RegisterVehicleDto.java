package SUPERMENU.BACKEND_SERVICE.Dto;

import SUPERMENU.BACKEND_SERVICE.Models.User;
import lombok.Data;

@Data
public class RegisterVehicleDto {

    private String name;
    private String model;
    private String plate;
    private Long owner_national_id;

}
