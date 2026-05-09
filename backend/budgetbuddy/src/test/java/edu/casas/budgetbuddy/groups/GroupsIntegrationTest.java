package edu.casas.budgetbuddy.groups;

import edu.casas.budgetbuddy.TestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GroupsIntegrationTest extends TestSupport {
    @Test
    void tc20CreateGroup() throws Exception {
        Session admin = register("admin@mail.com");
        mockMvc.perform(post("/api/v1/groups").header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Trip","description":"Cebu"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test
    void tc21CreateGroupWithNoName() throws Exception {
        Session admin = register("group-no-name@mail.com");
        mockMvc.perform(post("/api/v1/groups").header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description":"Missing"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void tc22GetUsersGroups() throws Exception {
        Session admin = register("groups@mail.com");
        createGroup(admin);
        mockMvc.perform(get("/api/v1/groups").header("Authorization", bearer(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void tc23GetGroupDetailAsMember() throws Exception {
        Session admin = register("detail@mail.com");
        createGroup(admin);
        mockMvc.perform(get("/api/v1/groups/1").header("Authorization", bearer(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.members[0].role").value("ADMIN"));
    }

    @Test
    void tc24GetGroupDetailAsNonMember() throws Exception {
        Session admin = register("detail-admin@mail.com");
        Session outsider = register("outsider@mail.com");
        createGroup(admin);
        mockMvc.perform(get("/api/v1/groups/1").header("Authorization", bearer(outsider)))
                .andExpect(status().isForbidden());
    }

    @Test
    void tc25AddMemberByEmailAsAdmin() throws Exception {
        Session admin = register("add-admin@mail.com");
        Session member = register("member@mail.com");
        createGroup(admin);
        addMember(admin, member.email());
        mockMvc.perform(get("/api/v1/groups/1").header("Authorization", bearer(admin)))
                .andExpect(jsonPath("$.data.members.length()").value(2));
    }

    @Test
    void tc26AddMemberAsNonAdmin() throws Exception {
        Session admin = register("non-admin-owner@mail.com");
        Session member = register("non-admin-member@mail.com");
        Session next = register("next@mail.com");
        createGroup(admin);
        addMember(admin, member.email());
        mockMvc.perform(post("/api/v1/groups/1/members").header("Authorization", bearer(member))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s"}
                                """.formatted(next.email())))
                .andExpect(status().isForbidden());
    }

    @Test
    void tc27AddAlreadyExistingMember() throws Exception {
        Session admin = register("dup-member-admin@mail.com");
        Session member = register("dup-member@mail.com");
        createGroup(admin);
        addMember(admin, member.email());
        mockMvc.perform(post("/api/v1/groups/1/members").header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s"}
                                """.formatted(member.email())))
                .andExpect(status().isConflict());
    }

    @Test
    void tc28RemoveMemberAsAdmin() throws Exception {
        Session admin = register("remove-admin@mail.com");
        Session member = register("remove-member@mail.com");
        createGroup(admin);
        addMember(admin, member.email());
        mockMvc.perform(delete("/api/v1/groups/1/members/" + member.userId()).header("Authorization", bearer(admin)))
                .andExpect(status().isOk());
    }

    @Test
    void tc29RemoveLastAdmin() throws Exception {
        Session admin = register("last-admin@mail.com");
        createGroup(admin);
        mockMvc.perform(delete("/api/v1/groups/1/members/" + admin.userId()).header("Authorization", bearer(admin)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void tc30SoftDeleteGroup() throws Exception {
        Session admin = register("delete-group@mail.com");
        createGroup(admin);
        mockMvc.perform(delete("/api/v1/groups/1").header("Authorization", bearer(admin)))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/groups").header("Authorization", bearer(admin)))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    private void createGroup(Session admin) throws Exception {
        mockMvc.perform(post("/api/v1/groups").header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name":"Trip","description":"Cebu"}
                        """));
    }

    private void addMember(Session admin, String email) throws Exception {
        mockMvc.perform(post("/api/v1/groups/1/members").header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"%s"}
                        """.formatted(email)));
    }
}
