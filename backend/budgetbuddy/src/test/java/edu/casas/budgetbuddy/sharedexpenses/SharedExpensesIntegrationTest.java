package edu.casas.budgetbuddy.sharedexpenses;

import edu.casas.budgetbuddy.TestSupport;
import edu.casas.budgetbuddy.features.sharedexpenses.SharedExpensesDtos.BalanceExpense;
import edu.casas.budgetbuddy.features.sharedexpenses.SharedExpensesDtos.BalanceMember;
import edu.casas.budgetbuddy.features.sharedexpenses.SharedExpensesService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SharedExpensesIntegrationTest extends TestSupport {
    @Test
    void tc31AddSharedExpenseEqualSplit() throws Exception {
        GroupFixture fixture = groupFixture();
        mockMvc.perform(post("/api/v1/groups/1/shared-expenses").header("Authorization", bearer(fixture.admin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expenseJson(fixture.admin().userId(), 300, "Food")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.splits.length()").value(3));
    }

    @Test
    void tc32AddExpenseWithZeroAmount() throws Exception {
        GroupFixture fixture = groupFixture();
        mockMvc.perform(post("/api/v1/groups/1/shared-expenses").header("Authorization", bearer(fixture.admin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expenseJson(fixture.admin().userId(), 0, "Food")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void tc33AddExpenseWithFutureDate() throws Exception {
        GroupFixture fixture = groupFixture();
        mockMvc.perform(post("/api/v1/groups/1/shared-expenses").header("Authorization", bearer(fixture.admin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":300,"category":"Food","paidBy":%d,"expenseDate":"%s"}
                                """.formatted(fixture.admin().userId(), LocalDate.now().plusDays(1))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void tc34AddExpensePaidByNotInGroup() throws Exception {
        GroupFixture fixture = groupFixture();
        Session outsider = register("payer-outsider@mail.com");
        mockMvc.perform(post("/api/v1/groups/1/shared-expenses").header("Authorization", bearer(fixture.admin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expenseJson(outsider.userId(), 300, "Food")))
                .andExpect(status().isForbidden());
    }

    @Test
    void tc35FetchGroupExpenses() throws Exception {
        GroupFixture fixture = groupFixture();
        createExpense(fixture.admin(), fixture.admin().userId());
        mockMvc.perform(get("/api/v1/groups/1/shared-expenses").header("Authorization", bearer(fixture.admin())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void tc36FetchGroupBalances() throws Exception {
        GroupFixture fixture = groupFixture();
        createExpense(fixture.admin(), fixture.admin().userId());
        mockMvc.perform(get("/api/v1/groups/1/balances").header("Authorization", bearer(fixture.admin())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].netBalance").value(200));
    }

    @Test
    void tc37BalanceCalculationAccuracy() {
        var result = SharedExpensesService.calculateBalances(
                List.of(new BalanceExpense(BigDecimal.valueOf(300), 1L)),
                List.of(new BalanceMember(1L), new BalanceMember(2L), new BalanceMember(3L)));
        assertEquals(0, BigDecimal.valueOf(200).compareTo(result.get(0).netBalance()));
        assertEquals(0, BigDecimal.valueOf(-100).compareTo(result.get(1).netBalance()));
        assertEquals(0, BigDecimal.valueOf(-100).compareTo(result.get(2).netBalance()));
    }

    @Test
    void tc38SoftDeleteExpenseAsPayer() throws Exception {
        GroupFixture fixture = groupFixture();
        createExpense(fixture.admin(), fixture.admin().userId());
        mockMvc.perform(delete("/api/v1/shared-expenses/1").header("Authorization", bearer(fixture.admin())))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/groups/1/shared-expenses").header("Authorization", bearer(fixture.admin())))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void tc39SoftDeleteExpenseAsNonPayerNonAdmin() throws Exception {
        GroupFixture fixture = groupFixture();
        createExpense(fixture.admin(), fixture.admin().userId());
        mockMvc.perform(delete("/api/v1/shared-expenses/1").header("Authorization", bearer(fixture.member())))
                .andExpect(status().isForbidden());
    }

    @Test
    void tc40SettleSplit() throws Exception {
        GroupFixture fixture = groupFixture();
        createExpense(fixture.admin(), fixture.admin().userId());
        mockMvc.perform(post("/api/v1/shared-expenses/splits/2/settle").header("Authorization", bearer(fixture.member())))
                .andExpect(status().isOk());
    }

    @Test
    void tc41SettleAnotherUsersSplit() throws Exception {
        GroupFixture fixture = groupFixture();
        createExpense(fixture.admin(), fixture.admin().userId());
        mockMvc.perform(post("/api/v1/shared-expenses/splits/2/settle").header("Authorization", bearer(fixture.other())))
                .andExpect(status().isForbidden());
    }

    private GroupFixture groupFixture() throws Exception {
        Session admin = register("shared-admin@mail.com");
        Session member = register("shared-member@mail.com");
        Session other = register("shared-other@mail.com");
        mockMvc.perform(post("/api/v1/groups").header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name":"Trip","description":"Cebu"}
                        """));
        addMember(admin, member.email());
        addMember(admin, other.email());
        return new GroupFixture(admin, member, other);
    }

    private void addMember(Session admin, String email) throws Exception {
        mockMvc.perform(post("/api/v1/groups/1/members").header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"%s"}
                        """.formatted(email)));
    }

    private void createExpense(Session requester, Long paidBy) throws Exception {
        mockMvc.perform(post("/api/v1/groups/1/shared-expenses").header("Authorization", bearer(requester))
                .contentType(MediaType.APPLICATION_JSON)
                .content(expenseJson(paidBy, 300, "Food")));
    }

    private String expenseJson(Long paidBy, int amount, String category) {
        return """
                {"amount":%d,"category":"%s","paidBy":%d}
                """.formatted(amount, category, paidBy);
    }

    private record GroupFixture(Session admin, Session member, Session other) {
    }
}
