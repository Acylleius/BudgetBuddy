package edu.casas.budgetbuddy.features.auth;

import edu.casas.budgetbuddy.features.auth.AuthDtos.AuthData;
import edu.casas.budgetbuddy.features.auth.AuthDtos.AuthUser;
import edu.casas.budgetbuddy.shared.store.BudgetBuddyStore;
import edu.casas.budgetbuddy.shared.store.BudgetBuddyStore.UserRecord;
import edu.casas.budgetbuddy.shared.utils.DomainException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final BudgetBuddyStore store;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Map<String, Long> tokens = new ConcurrentHashMap<>();

    public AuthService(BudgetBuddyStore store) {
        this.store = store;
    }

    public synchronized AuthData register(String email, String password, String firstname, String lastname) {
        String normalizedEmail = normalizeEmail(email);
        if (findByEmail(normalizedEmail).isPresent()) {
            throw new DomainException(HttpStatus.CONFLICT, "Email already exists");
        }
        UserRecord user = new UserRecord(store.userIds.getAndIncrement(), normalizedEmail,
                passwordEncoder.encode(password), firstname, lastname, "USER", "local", null,
                LocalDateTime.now());
        store.users.add(user);
        return issueToken(user);
    }

    public AuthData login(String email, String password) {
        UserRecord user = findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new DomainException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!"local".equals(user.authProvider()) || user.passwordHash() == null
                || !passwordEncoder.matches(password, user.passwordHash())) {
            throw new DomainException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return issueToken(user);
    }

    public synchronized AuthData loginWithGoogle(String email, String firstname, String lastname, String googleId) {
        String normalizedEmail = normalizeEmail(email);
        UserRecord user = findByEmail(normalizedEmail).orElseGet(() -> {
            UserRecord created = new UserRecord(store.userIds.getAndIncrement(), normalizedEmail, null,
                    blankToDefault(firstname, "Google"), blankToDefault(lastname, "User"),
                    "USER", "google", googleId, LocalDateTime.now());
            store.users.add(created);
            return created;
        });
        return issueToken(user);
    }

    public synchronized void changePassword(String authorization, String currentPassword, String newPassword) {
        UserRecord current = requireUser(authorization);
        if (!"local".equals(current.authProvider())) {
            throw new DomainException(HttpStatus.BAD_REQUEST, "Google users manage passwords with Google");
        }
        if (!passwordEncoder.matches(currentPassword, current.passwordHash())) {
            throw new DomainException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }
        replaceUser(new UserRecord(current.id(), current.email(), passwordEncoder.encode(newPassword),
                current.firstname(), current.lastname(), current.role(), current.authProvider(),
                current.googleId(), current.createdAt()));
    }

    public UserRecord requireUser(String authorization) {
        String token = parseToken(authorization);
        if (token == null) {
            throw new DomainException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        Long userId = tokens.get(token);
        if (userId == null) {
            throw new DomainException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return store.users.stream()
                .filter(user -> user.id().equals(userId))
                .findFirst()
                .orElseThrow(() -> new DomainException(HttpStatus.UNAUTHORIZED, "Authentication required"));
    }

    public AuthUser toAuthUser(UserRecord user) {
        return new AuthUser(user.id(), user.email(), user.firstname(), user.lastname(),
                user.role(), user.authProvider());
    }

    public void logout(String authorization) {
        String token = parseToken(authorization);
        if (token != null) {
            tokens.remove(token);
        }
    }

    public void clearSessions() {
        tokens.clear();
    }

    public Optional<UserRecord> findByEmail(String email) {
        String normalizedEmail = normalizeEmail(email);
        return store.users.stream()
                .filter(user -> user.email().equalsIgnoreCase(normalizedEmail))
                .findFirst();
    }

    private AuthData issueToken(UserRecord user) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, user.id());
        return new AuthData(token, toAuthUser(user));
    }

    private String parseToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return null;
        }
        return authorization.startsWith("Bearer ") ? authorization.substring(7) : authorization;
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private String blankToDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private void replaceUser(UserRecord replacement) {
        for (int index = 0; index < store.users.size(); index++) {
            if (store.users.get(index).id().equals(replacement.id())) {
                store.users.set(index, replacement);
                return;
            }
        }
    }
}
