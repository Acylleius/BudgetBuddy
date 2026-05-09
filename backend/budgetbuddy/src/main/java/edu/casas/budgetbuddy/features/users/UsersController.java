package edu.casas.budgetbuddy.features.users;

import edu.casas.budgetbuddy.features.auth.AuthDtos.AuthUser;
import edu.casas.budgetbuddy.features.auth.AuthService;
import edu.casas.budgetbuddy.shared.utils.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {
    private final AuthService authService;
    private final UsersService usersService;

    public UsersController(AuthService authService, UsersService usersService) {
        this.authService = authService;
        this.usersService = usersService;
    }

    @GetMapping("/me")
    public ApiResponse<AuthUser> profile(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return ApiResponse.success(usersService.profile(authService.requireUser(authorization)), "Profile loaded");
    }
}
