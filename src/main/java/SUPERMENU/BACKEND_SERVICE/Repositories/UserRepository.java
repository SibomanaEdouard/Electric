package SUPERMENU.BACKEND_SERVICE.Repositories;




import SUPERMENU.BACKEND_SERVICE.Models.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailOrPhoneOrNationalid(String email, String phone,Long nationalid);

    boolean existsByEmail(@Email @NotBlank String email);

    boolean existsByPhone(String phone);

    boolean existsByNationalid(Long nationalid);
}

