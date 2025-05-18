package SUPERMENU.BACKEND_SERVICE.Controllers;




import SUPERMENU.BACKEND_SERVICE.Dto.LoginDto;
import SUPERMENU.BACKEND_SERVICE.Dto.RegisterUserDto;
import SUPERMENU.BACKEND_SERVICE.Dto.ResetPasswordDto;
import SUPERMENU.BACKEND_SERVICE.Dto.UpdateUserDto;
import SUPERMENU.BACKEND_SERVICE.Exceptions.JwtExpiredException;
import SUPERMENU.BACKEND_SERVICE.Exceptions.UnauthorisedException;
import SUPERMENU.BACKEND_SERVICE.Models.User;
import SUPERMENU.BACKEND_SERVICE.Response.ApiResponse;
import SUPERMENU.BACKEND_SERVICE.Response.UserResponse;
import SUPERMENU.BACKEND_SERVICE.ServiceImpl.UserServiceImpl;
import SUPERMENU.BACKEND_SERVICE.Utils.GetLoggedUser;
import SUPERMENU.BACKEND_SERVICE.Utils.ResponseHandler;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/users")
@Tag(name = "User Controller", description = "User and Auth management API")

public class UserController {
    private final UserServiceImpl userServiceImpl;
    private final GetLoggedUser getLoggedUser;

    //this is to register user
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> createAccount(@Valid @RequestBody RegisterUserDto dto) throws Exception {
        Object ob = userServiceImpl.createAccount(dto);
        return ResponseHandler.success(ob, HttpStatus.CREATED);
    }
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Object>> updateAccount(@Valid @RequestBody UpdateUserDto dto) throws Exception {
        Object ob = userServiceImpl.updateUser(dto);
        return ResponseHandler.success(ob, HttpStatus.CREATED);
    }

    //this is to login the user
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> loginUser(@Valid @RequestBody LoginDto dto) throws Exception{
        Object ob =  userServiceImpl.loginUser(dto);
        return ResponseHandler.success(ob, HttpStatus.OK);
    }
    @GetMapping("/all_users")
    public ResponseEntity<ApiResponse<Object>> listAllUsers() throws Exception {
        Object users = userServiceImpl.getAllUsers();
        return ResponseHandler.success(users, HttpStatus.OK);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(@Valid @RequestBody ResetPasswordDto dto) throws  Exception{
        Object user= userServiceImpl.resetPassword(dto);
        return  ResponseHandler.success(user,HttpStatus.OK);
    }

    //this is for logout
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logoutUser(HttpServletRequest request) {
        ApiResponse<Object> response =  userServiceImpl.logoutUser(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    //this is to get the current logged-in user
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Object>> getCurrentUser() {
        try {
            // Get the user response DTO for safe API exposure
            UserResponse userResponse = getLoggedUser.getLoggedUserResponse();
            return ResponseHandler.success(userResponse, HttpStatus.OK);
        } catch (UnauthorisedException e) {
            return ResponseHandler.error(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseHandler.error("User not found", HttpStatus.NOT_FOUND);
        } catch (JwtExpiredException e) {
            return ResponseHandler.error("Session expired", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return ResponseHandler.error("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
