package SUPERMENU.BACKEND_SERVICE.ServiceImpl;



import SUPERMENU.BACKEND_SERVICE.Dto.LoginDto;
import SUPERMENU.BACKEND_SERVICE.Dto.RegisterUserDto;
import SUPERMENU.BACKEND_SERVICE.Dto.ResetPasswordDto;
import SUPERMENU.BACKEND_SERVICE.Dto.UpdateUserDto;
import SUPERMENU.BACKEND_SERVICE.Exceptions.ResourceNotFoundException;
import SUPERMENU.BACKEND_SERVICE.Models.User;
import SUPERMENU.BACKEND_SERVICE.Repositories.UserRepository;
import SUPERMENU.BACKEND_SERVICE.Response.ApiResponse;
import SUPERMENU.BACKEND_SERVICE.Response.UserResponse;
import SUPERMENU.BACKEND_SERVICE.RoleEnum;
import SUPERMENU.BACKEND_SERVICE.Services.JwtService;
import SUPERMENU.BACKEND_SERVICE.Services.UserService;
import SUPERMENU.BACKEND_SERVICE.Utils.GetLoggedUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final GetLoggedUser getLoggedUser;

    @Override
    public ApiResponse<Object> createAccount(RegisterUserDto dto) throws Exception {
        try {
            if (dto.getEmail() == null || dto.getFirstname() == null
                    || dto.getLastname() == null || dto.getPassword() == null || dto.getPhone()==null || dto.getNationalid()==null) {
                return ApiResponse.builder()
                        .message("All details are required! Please try again with valid details!")
                        .success(false)
                        .build();
            }
            if(dto.getPhone().length()!=10){

                return ApiResponse.builder()
                        .data(null)
                        .message("phone number must be 10 digits")
                        .success(false)
                        .build();
            }

            // Validate nationalId: Must be 16 digits
            if (dto.getNationalid() < 1000000000000000L || dto.getNationalid() > 9999999999999999L) {
                return ApiResponse.builder()
                        .success(false)
                        .message("National ID must be 16 digits")
                        .build();
            }

            // Check if the account exists
            Optional<User> existingUser = userRepository.findByEmailOrPhoneOrNationalid(dto.getEmail() , dto.getPhone(),dto.getNationalid());
            if (existingUser.isPresent()) {
                return ApiResponse.builder()
                        .message("Account already exists. Please login or use another email / phone /nationalid to continue.")
                        .success(false)
                        .build();
            }

            RoleEnum roleEnum =RoleEnum.ROLE_CUSTOMER;

            // Create a new account for user
            User newUser =new  User();
            newUser.setEmail(dto.getEmail());
            newUser.setPhone(dto.getPhone());
            newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
            newUser.setFirstname(dto.getFirstname());
            newUser.setLastname(dto.getLastname());
            newUser.setNationalid(dto.getNationalid());
            newUser.setRole(roleEnum);


            // Save the new account for user
            User savedUser = userRepository.save(newUser);
            UserResponse userResponse = new UserResponse();
            userResponse.setEmail(savedUser.getEmail());
            userResponse.setPhone(savedUser.getPhone());
            userResponse.setNationalid(savedUser.getNationalid());
            userResponse.setRole(savedUser.getRole());
            userResponse.setFirstname(savedUser.getFirstname());
            userResponse.setLastname(savedUser.getLastname());


            // Return success response
            return ApiResponse.builder()
                    .data(userResponse)
                    .message("Account created successfully")
                    .success(true)
                    .build();

        } catch (Exception e) {
            throw new Exception("Internal server error: " + e.getMessage());
        }
    }
    @Override
    @Transactional
    public ApiResponse<Object> getAllUsers() throws Exception {

        User loggeduser = getLoggedUser.getLoggedUserEntity();
        if(loggeduser.getRole() != RoleEnum.ROLE_ADMIN){
            return ApiResponse.builder()
                    .message("You are not allowed to access this resource")
                    .success(false)
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

//        This is to find the data for all users
        List<User> users =  userRepository.findAll();
        UserResponse userResponse = new UserResponse();

//        let me loop in the users
        for (User user : users) {
            userResponse.setEmail(user.getEmail());
            userResponse.setPhone(user.getPhone());
            userResponse.setRole(user.getRole());
            userResponse.setFirstname(user.getFirstname());
            userResponse.setLastname(user.getLastname());
            userResponse.setNationalid(user.getNationalid());
            userResponse.setPhone(user.getPhone());
            userResponse.setId(user.getId());
        }

        return ApiResponse.builder()
                .data(userResponse)
                .message("Users  retrieved successfully")
                .success(true)
                .status(HttpStatus.OK)
                .build();
    }

    @Override
    public ApiResponse<Object> loginUser(LoginDto dto) throws BadRequestException, AuthenticationException {
        // Validate input
        if (dto.getEmail()==null || dto.getPassword()==null) {
            throw new BadRequestException("Email and password are required");
        }

        try {
            // Authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );

            // Get user details
            User user = (User) authentication.getPrincipal();

            // Generate token
            String token = jwtService.generateToken(user);

            return ApiResponse.builder()
                    .success(true)
                    .data(Map.of(
                            "access_token", token,
                            "user_id", user.getId()
                    ))
                    .message("Login successful")
                    .status(HttpStatus.OK)
                    .build();

        } catch (BadCredentialsException ex) {
            throw new AuthenticationException("Invalid email or password");
        }
    }


    public ApiResponse<Object> logoutUser(HttpServletRequest request) {
        // Clear the security context
        SecurityContextHolder.clearContext();

        // Get the JWT token from the request header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // Invalidate the token
            jwtService.invalidateToken(token);
        }

        return ApiResponse.builder()
                .success(true)
                .data(null)
                .message("Logged out successfully")
                .status(HttpStatus.OK)
                .build();
    }

    public ApiResponse<Object> resetPassword(ResetPasswordDto dto) {
        // Validate input parameters
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            return ApiResponse.builder()
                    .success(false)
                    .message("Email  is required")
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        try {
            // Find user by email
            User user = userRepository.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            // Validate password match
            if (!Objects.equals(dto.getNew_password(), dto.getConfirm_password())) {
                return ApiResponse.builder()
                        .success(false)
                        .message("Password and confirmation do not match")
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
            }

            // Validate password strength
            if (!isPasswordValid(dto.getNew_password())) {
                return ApiResponse.builder()
                        .success(false)
                        .message("Password does not meet security requirements")
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
            }

            // Update and save user
            user.setPassword(passwordEncoder.encode(dto.getNew_password()));
            User savedUser = userRepository.save(user);

            return ApiResponse.builder()
                    .success(true)
                    .data(savedUser)
                    .message("Password reset successfully")
                    .status(HttpStatus.OK)
                    .build();

        } catch (ResourceNotFoundException ex) {
            return ApiResponse.builder()
                    .success(false)
                    .message(ex.getMessage())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        } catch (Exception ex) {
            return ApiResponse.builder()
                    .success(false)
                    .message("Failed to reset password. Please try again later.")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
    @Override
    public ApiResponse<Object> updateUser(UpdateUserDto dto) {
        try {
            // Get the actual User entity from the logged-in user
            User userEntity = getLoggedUser.getLoggedUserEntity();

            // Validate and update email
            if (dto.getEmail() != null && !dto.getEmail().equals(userEntity.getEmail())) {
                if (userRepository.existsByEmail(dto.getEmail())) {
                    return ApiResponse.builder()
                            .success(false)
                            .message("Email already taken")
                            .status(HttpStatus.BAD_REQUEST)
                            .build();
                }
                userEntity.setEmail(dto.getEmail());
            }

            // Validate and update phone
            if (dto.getPhone() != null) {
                if (!dto.getPhone().matches("\\d{10}")) {
                    return ApiResponse.builder()
                            .success(false)
                            .message("Phone must be 10 digits")
                            .status(HttpStatus.BAD_REQUEST)
                            .build();
                }
                if (!dto.getPhone().equals(userEntity.getPhone())) {
                    if (userRepository.existsByPhone(dto.getPhone())) {
                        return ApiResponse.builder()
                                .success(false)
                                .message("Phone already taken")
                                .status(HttpStatus.BAD_REQUEST)
                                .build();
                    }
                    userEntity.setPhone(dto.getPhone());
                }
            }

            // Validate and update national ID
            if (dto.getNationalid() != null) {
                if (dto.getNationalid() < 1000000000000000L || dto.getNationalid() > 9999999999999999L) {
                    return ApiResponse.builder()
                            .success(false)
                            .message("National ID must be 16 digits")
                            .status(HttpStatus.BAD_REQUEST)
                            .build();
                }
                if (!dto.getNationalid().equals(userEntity.getNationalid())) {
                    if (userRepository.existsByNationalid(dto.getNationalid())) {
                        return ApiResponse.builder()
                                .success(false)
                                .message("National ID already taken")
                                .status(HttpStatus.BAD_REQUEST)
                                .build();
                    }
                    userEntity.setNationalid(dto.getNationalid());
                }
            }

            // Update other fields
            if (dto.getFirstname() != null) userEntity.setFirstname(dto.getFirstname());
            if (dto.getLastname() != null) userEntity.setLastname(dto.getLastname());

            // Save the updated entity
            User updatedUser = userRepository.save(userEntity);

            UserResponse userResponse = new UserResponse();
            userResponse.setId(updatedUser.getId());
            userResponse.setEmail(updatedUser.getEmail());
            userResponse.setPhone(updatedUser.getPhone());
            userResponse.setNationalid(updatedUser.getNationalid());
            userResponse.setFirstname(updatedUser.getFirstname());
            userResponse.setLastname(updatedUser.getLastname());
            userResponse.setRole(updatedUser.getRole());
            userResponse.setId(updatedUser.getId());

            return ApiResponse.builder()
                    .success(true)
                    .data(userResponse)
                    .message("User updated")
                    .status(HttpStatus.OK)
                    .build();

        } catch (ResourceNotFoundException ex) {
            return ApiResponse.builder()
                    .success(false)
                    .message(ex.getMessage())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        } catch (Exception ex) {
            return ApiResponse.builder()
                    .success(false)
                    .message("Update failed: " + ex.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
    private boolean isPasswordValid(String password) {
        return password != null && password.length() >= 8;
    }

}

