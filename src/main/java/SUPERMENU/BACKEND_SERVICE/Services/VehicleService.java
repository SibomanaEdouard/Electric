package SUPERMENU.BACKEND_SERVICE.Services;

import SUPERMENU.BACKEND_SERVICE.Dto.RegisterVehicleDto;
import SUPERMENU.BACKEND_SERVICE.Models.Vehicle;
import SUPERMENU.BACKEND_SERVICE.Response.ApiResponse;

import java.util.UUID;

public interface VehicleService {
    ApiResponse<Object> createVehicle(RegisterVehicleDto dto) throws Exception;
    ApiResponse<Object> updateVehicle(UUID vehicleId,RegisterVehicleDto dto);

    ApiResponse<Vehicle> getVehicleBYId(UUID vehicleId) throws Exception;
}
