package edu.casas.budgetbuddy.features.transactions;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class TransactionsDtos {
    private TransactionsDtos() {
    }

    public record TransactionRequest(@NotBlank String type,
                                     @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
                                     @NotBlank String category,
                                     String description,
                                     LocalDate transactionDate) {
    }

    public record TransactionDto(Long id, Long userId, String type, BigDecimal amount,
                                 String formattedAmount, String category, String description,
                                 LocalDate transactionDate) {
    }

    public record SummaryDto(BigDecimal totalIncome, BigDecimal totalExpense,
                             BigDecimal balance, long count) {
    }
}
