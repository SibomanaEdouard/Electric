package SUPERMENU.BACKEND_SERVICE.ServiceImpl;

import SUPERMENU.BACKEND_SERVICE.Dto.RegisterVehicleDto;
import SUPERMENU.BACKEND_SERVICE.Models.User;
import SUPERMENU.BACKEND_SERVICE.Models.Vehicle;
import SUPERMENU.BACKEND_SERVICE.Repositories.UserRepository;
import SUPERMENU.BACKEND_SERVICE.Repositories.VehicleReposity;
import SUPERMENU.BACKEND_SERVICE.Response.ApiResponse;
import SUPERMENU.BACKEND_SERVICE.RoleEnum;
import SUPERMENU.BACKEND_SERVICE.Services.VehicleService;
import SUPERMENU.BACKEND_SERVICE.Utils.GetLoggedUser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
@Service
public class VehicleServiceImpl implements VehicleService {
    final UserRepository userRepository;
    final VehicleReposity vehicleReposity;
    final GetLoggedUser getLoggedUser;

    public VehicleServiceImpl(UserRepository userRepository, VehicleReposity vehicleReposity, GetLoggedUser getLoggedUser) {
        this.userRepository = userRepository;
        this.vehicleReposity = vehicleReposity;
        this.getLoggedUser = getLoggedUser;
    }

    @Override
    public ApiResponse<Object> createVehicle(RegisterVehicleDto dto) throws Exception {

        try {

//        This is to check if the logged user if admin  so that s/he may register the vehicle
            User loggeduser = getLoggedUser.getLoggedUserEntity();
            if (loggeduser.getRole() != RoleEnum.ROLE_ADMIN) {
                return ApiResponse.builder()
                        .message("You are not allowed to access this resource")
                        .success(false)
                        .status(HttpStatus.UNAUTHORIZED)
                        .build();
            }
//        let me check the details  for the car
            if (dto.getModel() == null || dto.getModel().isEmpty() || dto.getName() == null || dto.getName().isEmpty() || dto.getOwner_national_id() == null || dto.getPlate() == null || dto.getPlate().isEmpty()) {
                return ApiResponse.builder()
                        .message("please provide all vehicle  details")
                        .success(false)
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
            }
//        This is to find the user by the email
            Optional<User> user = userRepository.findByNationalid(dto.getOwner_national_id());

            if (user.isEmpty()) {
                return ApiResponse.builder()
                        .message("No user with that national id  is found, please  register him or her in the system!")
                        .build();

            }
            Vehicle vehicle = new Vehicle();
            vehicle.setModel(dto.getModel());
            vehicle.setName(dto.getName());
            vehicle.setOwner(user.get());
            vehicle.setPlate(dto.getPlate());
//     This is to save the  vehicle
            vehicleReposity.save(vehicle);
            return ApiResponse.builder()
                    .data(vehicle)
                    .message("Vehicle created successfully")
                    .success(true)
                    .status(HttpStatus.CREATED)
                    .build();

        }catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

//    This is to update the vehicle
    @Override
    public ApiResponse<Object> updateVehicle(UUID vehicleId, RegisterVehicleDto dto) {
        try {
            // Check if logged-in user is an admin
            User loggedUser = getLoggedUser.getLoggedUserEntity();
            if (loggedUser.getRole() != RoleEnum.ROLE_ADMIN) {
                return ApiResponse.builder()
                        .message("Unauthorized access")
                        .success(false)
                        .status(HttpStatus.UNAUTHORIZED)
                        .build();
            }

            // Find the vehicle by ID
            Optional<Vehicle> optionalVehicle = vehicleReposity.findById(vehicleId);
            if (optionalVehicle.isEmpty()) {
                return ApiResponse.builder()
                        .message("Vehicle not found")
                        .success(false)
                        .status(HttpStatus.NOT_FOUND)
                        .build();
            }

            Vehicle vehicle = optionalVehicle.get();

            // Update owner if provided
            if (dto.getOwner_national_id() != null) {
                Optional<User> userOptional = userRepository.findByNationalid(dto.getOwner_national_id());
                if (userOptional.isEmpty()) {
                    return ApiResponse.builder()
                            .message("User with provided national ID not found")
                            .success(false)
                            .status(HttpStatus.BAD_REQUEST)
                            .build();
                }
                vehicle.setOwner(userOptional.get());
            }

            // Update name (if provided and not empty)
            if (dto.getName() != null && !dto.getName().isEmpty()) {
                vehicle.setName(dto.getName());
            }

            // Update model (if provided and not empty)
            if (dto.getModel() != null && !dto.getModel().isEmpty()) {
                vehicle.setModel(dto.getModel());
            }

            // Update plate (if provided and not empty)
            if (dto.getPlate() != null && !dto.getPlate().isEmpty()) {
                vehicle.setPlate(dto.getPlate());
            }

            // Save the updated vehicle
            vehicleReposity.save(vehicle);

            return ApiResponse.builder()
                    .data(vehicle)
                    .message("Vehicle updated successfully")
                    .success(true)
                    .status(HttpStatus.OK)
                    .build();

        } catch (Exception e) {
            return ApiResponse.builder()
                    .message("Error updating vehicle: " + e.getMessage())
                    .success(false)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @Override
    public ApiResponse<Vehicle> getVehicleBYId(UUID vehicleId) throws Exception {
       try{
//           This is to find the details of the vehicle
           Optional<Vehicle> vehicle = vehicleReposity.findById(vehicleId);
           if (vehicle.isPresent()) {
               return ApiResponse.<Vehicle>builder()
                       .data(vehicle.get())
                       .success(true)
                       .status(HttpStatus.OK)
                       .message("The vehicle is retrieved successfully")
                       .build();
           }
           return ApiResponse.<Vehicle>builder()
                   .success(false)
                   .message("No vehicle with that id is found")
                   .status(HttpStatus.NOT_FOUND)
                   .build();


       }catch (Exception e) {
           throw  new Exception(e.getMessage());
       }
    }

//    This is to update the  vehicle details such as owner and others


}
