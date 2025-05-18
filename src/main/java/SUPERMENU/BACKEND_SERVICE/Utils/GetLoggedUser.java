package SUPERMENU.BACKEND_SERVICE.Utils;






import SUPERMENU.BACKEND_SERVICE.Exceptions.JwtExpiredException;
import SUPERMENU.BACKEND_SERVICE.Exceptions.UnauthorisedException;
import SUPERMENU.BACKEND_SERVICE.Models.User;
import SUPERMENU.BACKEND_SERVICE.Repositories.UserRepository;
import SUPERMENU.BACKEND_SERVICE.Response.UserResponse;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

import java.util.Optional;
@RequiredArgsConstructor
@Service
public class GetLoggedUser {
    private final UserRepository userRepository;

    // Use this for database operations (returns User entity)
    public User getLoggedUserEntity() throws Exception {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (principal.equals("anonymousUser")) {
                throw new UnauthorisedException("You are not logged in");
            }

            String email = principal instanceof UserDetails ?
                    ((UserDetails) principal).getUsername() :
                    principal.toString();

            return userRepository.findByEmail(email)
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);

        } catch (JwtExpiredException e) {
            throw new JwtExpiredException("JWT expired: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Authentication error: " + e.getMessage());
        }
    }

    // Use this for API responses (returns UserResponse DTO)
    public UserResponse getLoggedUserResponse() throws Exception {
        User user = getLoggedUserEntity();
        return UserResponse.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .nationalid(user.getNationalid())
                .role(user.getRole())
                .build();
    }
}