package edu.casas.budgetbuddy.features.transactions;

import edu.casas.budgetbuddy.features.transactions.TransactionsDtos.SummaryDto;
import edu.casas.budgetbuddy.features.transactions.TransactionsDtos.TransactionDto;
import edu.casas.budgetbuddy.shared.store.BudgetBuddyStore;
import edu.casas.budgetbuddy.shared.store.BudgetBuddyStore.TransactionRecord;
import edu.casas.budgetbuddy.shared.utils.DomainException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class TransactionsService {
    private final BudgetBuddyStore store;
    private final NumberFormat pesoFormat;

    public TransactionsService(BudgetBuddyStore store) {
        this.store = store;
        this.pesoFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-PH"));
    }

    public synchronized TransactionDto create(Long userId, String type, BigDecimal amount,
                                              String category, String description, LocalDate date) {
        String normalizedType = normalizeType(type);
        TransactionRecord record = new TransactionRecord(store.transactionIds.getAndIncrement(), userId,
                normalizedType, amount, category, description, date == null ? LocalDate.now() : date, false);
        store.transactions.add(record);
        return toDto(record);
    }

    public List<TransactionDto> list(Long userId) {
        return store.transactions.stream()
                .filter(transaction -> transaction.userId().equals(userId) && !transaction.deleted())
                .map(this::toDto)
                .toList();
    }

    public SummaryDto summary(Long userId) {
        List<TransactionRecord> records = store.transactions.stream()
                .filter(transaction -> transaction.userId().equals(userId) && !transaction.deleted())
                .toList();
        BigDecimal income = sum(records, "INCOME");
        BigDecimal expense = sum(records, "EXPENSE");
        return new SummaryDto(income, expense, income.subtract(expense), records.size());
    }

    public synchronized void softDelete(Long userId, Long transactionId) {
        for (int index = 0; index < store.transactions.size(); index++) {
            TransactionRecord transaction = store.transactions.get(index);
            if (transaction.id().equals(transactionId)) {
                if (!transaction.userId().equals(userId)) {
                    throw new DomainException(HttpStatus.FORBIDDEN, "Cannot delete another user's transaction");
                }
                store.transactions.set(index, new TransactionRecord(transaction.id(), transaction.userId(),
                        transaction.type(), transaction.amount(), transaction.category(), transaction.description(),
                        transaction.transactionDate(), true));
                return;
            }
        }
        throw new DomainException(HttpStatus.NOT_FOUND, "Transaction not found");
    }

    private BigDecimal sum(List<TransactionRecord> records, String type) {
        return records.stream()
                .filter(transaction -> transaction.type().equals(type))
                .map(TransactionRecord::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String normalizeType(String type) {
        String normalized = type == null ? "" : type.trim().toUpperCase();
        if (!normalized.equals("INCOME") && !normalized.equals("EXPENSE")) {
            throw new DomainException(HttpStatus.BAD_REQUEST, "type must be INCOME or EXPENSE");
        }
        return normalized;
    }

    private TransactionDto toDto(TransactionRecord record) {
        String prefix = record.type().equals("INCOME") ? "up " : "down ";
        return new TransactionDto(record.id(), record.userId(), record.type(), record.amount(),
                prefix + pesoFormat.format(record.amount()), record.category(), record.description(),
                record.transactionDate());
    }
}
