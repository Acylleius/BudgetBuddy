package edu.casas.budgetbuddy.auth;

import edu.casas.budgetbuddy.TestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthIntegrationTest extends TestSupport {
    @Test
    void tc01RegisterWithValidData() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"tc01@mail.com","password":"Password123!","firstname":"A","lastname":"B"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void tc02RegisterDuplicateEmail() throws Exception {
        register("dup@mail.com");
        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"dup@mail.com","password":"Password123!","firstname":"A","lastname":"B"}
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void tc03RegisterMissingFields() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void tc04LoginWithValidCredentials() throws Exception {
        register("login@mail.com");
        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"login@mail.com","password":"Password123!"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void tc05LoginWithWrongPassword() throws Exception {
        register("wrong@mail.com");
        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"wrong@mail.com","password":"WrongPassword"}
                        """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tc06LoginWithNonExistentEmail() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"missing@mail.com","password":"Password123!"}
                        """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tc07GoogleOAuthCallbackCreatesOrFindsUser() throws Exception {
        mockMvc.perform(get("/api/v1/auth/google/callback")
                        .param("email", "google@mail.com")
                        .param("firstname", "Google")
                        .param("lastname", "User")
                        .param("googleId", "gid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user.authProvider").value("google"));
    }

    @Test
    void tc08LogoutRedirects() throws Exception {
        Session session = register("logout@mail.com");
        mockMvc.perform(get("/api/v1/auth/logout").header("Authorization", bearer(session)))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void tc09ChangePasswordValid() throws Exception {
        Session session = register("change@mail.com");
        mockMvc.perform(post("/api/v1/auth/change-password")
                        .header("Authorization", bearer(session))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"currentPassword":"Password123!","newPassword":"NewPassword123!"}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void tc10ChangePasswordWrongCurrent() throws Exception {
        Session session = register("change-wrong@mail.com");
        mockMvc.perform(post("/api/v1/auth/change-password")
                        .header("Authorization", bearer(session))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"currentPassword":"BadPassword","newPassword":"NewPassword123!"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void tc11ProtectedRouteWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isUnauthorized());
    }
}
