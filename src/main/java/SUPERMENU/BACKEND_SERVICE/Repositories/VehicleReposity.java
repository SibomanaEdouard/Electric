package SUPERMENU.BACKEND_SERVICE.Repositories;

import SUPERMENU.BACKEND_SERVICE.Models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VehicleReposity extends JpaRepository<Vehicle, UUID> {

}
