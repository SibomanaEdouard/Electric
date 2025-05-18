package SUPERMENU.BACKEND_SERVICE.Controllers;


import SUPERMENU.BACKEND_SERVICE.Dto.RegisterVehicleDto;
import SUPERMENU.BACKEND_SERVICE.Models.Vehicle;
import SUPERMENU.BACKEND_SERVICE.Response.ApiResponse;
import SUPERMENU.BACKEND_SERVICE.ServiceImpl.VehicleServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/vehicle")
@Tag(name="Vehicle management apis",description = "This is the vehicle management end points ")
public class VehicleController {
    private final VehicleServiceImpl vehicleServiceImpl;

    @PostMapping("/register")
   public ResponseEntity<ApiResponse<Object>> createVehicle(@Valid @RequestBody RegisterVehicleDto dto) throws Exception {
      Object vehicle = vehicleServiceImpl.createVehicle(dto) ;
      return new ResponseEntity<>(new ApiResponse<>(vehicle), HttpStatus.OK);

   }
   @GetMapping("/{vehicleId}")
  public ResponseEntity<ApiResponse<Object>>getVehicle(@Valid @PathVariable("vehicleId") UUID vehicleId) throws Exception {
       Object vehicle = vehicleServiceImpl.getVehicleBYId(vehicleId) ;
       return new ResponseEntity<>(new ApiResponse<>(vehicle), HttpStatus.OK);
  }

  @PutMapping("/{vehicleId}")
    public ResponseEntity<ApiResponse<Object>>updateVehicleDetails(@Valid @PathVariable("vehicleId") UUID vehicleId,RegisterVehicleDto dto) throws Exception {
        Object Vehicle = vehicleServiceImpl.updateVehicle(vehicleId,dto);
        return new ResponseEntity<>(new ApiResponse<>(Vehicle), HttpStatus.OK);
  }

}
