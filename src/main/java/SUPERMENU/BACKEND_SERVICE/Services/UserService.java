package SUPERMENU.BACKEND_SERVICE.Services;

import SUPERMENU.BACKEND_SERVICE.Dto.LoginDto;
import SUPERMENU.BACKEND_SERVICE.Dto.RegisterUserDto;
import SUPERMENU.BACKEND_SERVICE.Dto.ResetPasswordDto;
import SUPERMENU.BACKEND_SERVICE.Dto.UpdateUserDto;
import SUPERMENU.BACKEND_SERVICE.Models.User;
import SUPERMENU.BACKEND_SERVICE.Response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface UserService {
    ApiResponse<Object> createAccount(RegisterUserDto dto) throws  Exception;
    ApiResponse<Object> getAllUsers() throws  Exception;

    ApiResponse<Object> loginUser(LoginDto dto) throws Exception;

    ApiResponse<Object> logoutUser(HttpServletRequest request) throws Exception;

    ApiResponse<Object> resetPassword(ResetPasswordDto dto) throws  Exception;

    ApiResponse<Object> updateUser(UpdateUserDto dto) throws Exception;

}
