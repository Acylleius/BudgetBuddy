package edu.casas.budgetbuddy.shared.store;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

@Component
public class BudgetBuddyStore {
    public final AtomicLong userIds = new AtomicLong(1);
    public final AtomicLong transactionIds = new AtomicLong(1);
    public final AtomicLong groupIds = new AtomicLong(1);
    public final AtomicLong expenseIds = new AtomicLong(1);
    public final AtomicLong splitIds = new AtomicLong(1);
    public final List<UserRecord> users = new ArrayList<>();
    public final List<TransactionRecord> transactions = new ArrayList<>();
    public final List<GroupRecord> groups = new ArrayList<>();
    public final List<GroupMemberRecord> members = new ArrayList<>();
    public final List<SharedExpenseRecord> expenses = new ArrayList<>();
    public final List<ExpenseSplitRecord> splits = new ArrayList<>();

    public synchronized void reset() {
        userIds.set(1);
        transactionIds.set(1);
        groupIds.set(1);
        expenseIds.set(1);
        splitIds.set(1);
        users.clear();
        transactions.clear();
        groups.clear();
        members.clear();
        expenses.clear();
        splits.clear();
    }

    public record UserRecord(Long id, String email, String passwordHash, String firstname,
                             String lastname, String role, String authProvider, String googleId,
                             LocalDateTime createdAt) {
    }

    public record TransactionRecord(Long id, Long userId, String type, BigDecimal amount,
                                    String category, String description, LocalDate transactionDate,
                                    boolean deleted) {
    }

    public record GroupRecord(Long id, String name, String description, Long createdBy,
                              boolean deleted) {
    }

    public record GroupMemberRecord(Long groupId, Long userId, String role, boolean deleted) {
    }

    public record SharedExpenseRecord(Long id, Long groupId, Long paidBy, BigDecimal amount,
                                      String category, String description, LocalDate expenseDate,
                                      boolean deleted) {
    }

    public record ExpenseSplitRecord(Long id, Long expenseId, Long userId, BigDecimal amount,
                                     boolean settled, LocalDateTime settledAt, boolean deleted) {
    }
}
