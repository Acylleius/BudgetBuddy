package edu.casas.budgetbuddy.features.users;

import edu.casas.budgetbuddy.features.auth.AuthDtos.AuthUser;
import edu.casas.budgetbuddy.features.auth.AuthService;
import edu.casas.budgetbuddy.shared.store.BudgetBuddyStore.UserRecord;
import org.springframework.stereotype.Service;

@Service
public class UsersService {
    private final AuthService authService;

    public UsersService(AuthService authService) {
        this.authService = authService;
    }

    public AuthUser profile(UserRecord user) {
        return authService.toAuthUser(user);
    }
}
