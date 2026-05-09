package edu.casas.budgetbuddy.features.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public final class AuthDtos {
    private AuthDtos() {
    }

    public record RegisterRequest(@Email @NotBlank String email, @NotBlank String password,
                                  @NotBlank String firstname, @NotBlank String lastname) {
    }

    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {
    }

    public record ChangePasswordRequest(@NotBlank String currentPassword, @NotBlank String newPassword) {
    }

    public record AuthUser(Long id, String email, String firstname, String lastname,
                           String role, String authProvider) {
    }

    public record AuthData(String token, AuthUser user) {
    }
}
