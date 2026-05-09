package edu.casas.budgetbuddy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.casas.budgetbuddy.features.auth.AuthService;
import edu.casas.budgetbuddy.shared.store.BudgetBuddyStore;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class TestSupport {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected BudgetBuddyStore store;

    @Autowired
    protected AuthService authService;

    protected final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    protected void resetStore() {
        store.reset();
        authService.clearSessions();
    }

    protected Session register(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "Password123!",
                                  "firstname": "Test",
                                  "lastname": "User"
                                }
                                """.formatted(email)))
                .andReturn();
        JsonNode root = mapper.readTree(result.getResponse().getContentAsString());
        return new Session(root.at("/data/token").asText(), root.at("/data/user/id").asLong(), email);
    }

    protected String bearer(Session session) {
        return "Bearer " + session.token();
    }

    protected record Session(String token, Long userId, String email) {
    }
}
