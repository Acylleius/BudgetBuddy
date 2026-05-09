# BudgetBuddy - Software Test Plan

## 1. Project Information
- Project Name: BudgetBuddy
- Version: Phase 4 (Shared Expenses + Google Auth)
- Test Plan Date: 2026-05-09
- Tester: Elissa Mae Buenvenida Casas

## 2. Test Objectives
- Verify all functional requirements work after vertical slice refactoring
- Detect regressions introduced by refactoring
- Validate Google OAuth integration
- Validate shared expense logic (equal split, balance calculation, settlement)

## 3. Scope
### In Scope:
- Authentication (register, login, logout, Google OAuth)
- Personal transaction CRUD (soft delete)
- Group management (create, update, soft delete, add/remove members)
- Shared expense management (add, list, soft delete)
- Balance calculation and settlement
- Input validation on all endpoints
- Password change

### Out of Scope:
- Push/email notifications
- Custom (non-equal) expense splits
- Payment processing

## 4. Test Types
- Unit Tests (JUnit) - service layer functions
- Integration Tests (Spring MockMvc) - API endpoints
- Manual / UI Tests - frontend flows

## 5. Test Environment
- Spring Boot backend running locally
- In-memory test store for regression tests
- Browser: Chrome latest

## 6. Test Cases

### Feature: Authentication
| ID | Test Case | Input | Expected Result | Type |
|---|---|---|---|---|
| TC-01 | Register with valid data | Valid email, name, password | 201, user created, password hashed | Integration |
| TC-02 | Register with duplicate email | Existing email | 409, error message | Integration |
| TC-03 | Register with missing fields | Empty email or password | 400, validation error | Integration |
| TC-04 | Login with valid credentials | Correct email + password | 200, session/token returned | Integration |
| TC-05 | Login with wrong password | Correct email, wrong password | 401, error message | Integration |
| TC-06 | Login with non-existent email | Unknown email | 401, error message | Integration |
| TC-07 | Google OAuth flow | Valid Google account | User created or found, redirected to dashboard | Manual/Integration |
| TC-08 | Logout | Authenticated session | Session destroyed, redirected to login | Integration |
| TC-09 | Change password (valid) | Correct current password, new password | 200, password updated | Integration |
| TC-10 | Change password (wrong current) | Wrong current password | 400, error | Integration |
| TC-11 | Access protected route without auth | No session/token | 401, redirect to login | Integration |

### Feature: Personal Transactions
| ID | Test Case | Input | Expected Result | Type |
|---|---|---|---|---|
| TC-12 | Add income transaction | Valid type=INCOME, amount, category | 201, transaction saved | Integration |
| TC-13 | Add expense transaction | Valid type=EXPENSE, amount, category | 201, transaction saved | Integration |
| TC-14 | Add transaction with amount = 0 | amount = 0 | 400, validation error | Integration |
| TC-15 | Add transaction with no category | Missing category | 400, validation error | Integration |
| TC-16 | Fetch transaction list | Valid user token | 200, array of transactions (not deleted) | Integration |
| TC-17 | Fetch summary | Valid user token | 200, total income/expense/balance/count | Integration |
| TC-18 | Soft delete transaction | Valid transactionId, user token | 200, is_deleted=true, excluded from list | Integration |
| TC-19 | Delete another user's transaction | Wrong user token | 403, forbidden | Integration |

### Feature: Groups
| ID | Test Case | Input | Expected Result | Type |
|---|---|---|---|---|
| TC-20 | Create group | Valid name, description | 201, group created, creator is ADMIN | Integration |
| TC-21 | Create group with no name | Missing name | 400, validation error | Integration |
| TC-22 | Get user's groups | Valid user token | 200, list of groups user belongs to | Integration |
| TC-23 | Get group detail | Valid groupId (member) | 200, group + members | Integration |
| TC-24 | Get group detail (non-member) | groupId, unrelated user token | 403, forbidden | Integration |
| TC-25 | Add member by email (ADMIN) | Valid email of existing user | 200, member added | Integration |
| TC-26 | Add member (non-ADMIN) | Request from MEMBER role | 403, forbidden | Integration |
| TC-27 | Add already-existing member | Email already in group | 409, already a member | Integration |
| TC-28 | Remove member (ADMIN) | Valid memberId | 200, is_deleted=true | Integration |
| TC-29 | Remove last ADMIN | Only ADMIN in group | 400, cannot remove last admin | Integration |
| TC-30 | Soft delete group (ADMIN) | Valid groupId | 200, is_deleted=true | Integration |

### Feature: Shared Expenses
| ID | Test Case | Input | Expected Result | Type |
|---|---|---|---|---|
| TC-31 | Add shared expense (equal split) | amount, category, paid_by, members | 201, expense + equal splits created | Integration |
| TC-32 | Add expense with amount = 0 | amount = 0 | 400, validation error | Integration |
| TC-33 | Add expense with future date | expense_date > today | 400, validation error | Integration |
| TC-34 | Add expense - paid_by not in group | paid_by = user not in group | 400/403, validation error | Integration |
| TC-35 | Fetch group expenses | Valid groupId | 200, list of non-deleted expenses | Integration |
| TC-36 | Fetch group balances | Valid groupId (3 members, 1 expense) | 200, correct per-member paid/owed/net | Integration |
| TC-37 | Balance calculation accuracy | PHP 300 expense, 3 equal members | Each member owes PHP 100; payer net = +PHP 200 | Unit |
| TC-38 | Soft delete expense (payer) | Valid expenseId, correct user token | 200, is_deleted=true, excluded from list | Integration |
| TC-39 | Soft delete expense (non-payer, non-admin) | Wrong user token | 403, forbidden | Integration |
| TC-40 | Settle a split | Valid splitId, correct user token | 200, is_settled=true, settled_at set | Integration |
| TC-41 | Settle another user's split | Wrong user token | 403, forbidden | Integration |

## 7. Test Scripts (Automated - JUnit + MockMvc)
See backend/budgetbuddy/src/test/java/edu/casas/budgetbuddy/auth/AuthIntegrationTest.java, backend/budgetbuddy/src/test/java/edu/casas/budgetbuddy/transactions/TransactionsIntegrationTest.java, backend/budgetbuddy/src/test/java/edu/casas/budgetbuddy/groups/GroupsIntegrationTest.java, backend/budgetbuddy/src/test/java/edu/casas/budgetbuddy/sharedexpenses/SharedExpensesIntegrationTest.java.

## 8. Pass/Fail Criteria
- PASS: Response code and body match expected result
- FAIL: Any deviation from expected result
- All TC-01 through TC-41 must PASS before branch can be merged

## 9. Regression Scope
After refactoring, re-run ALL 41 test cases to confirm no regressions.
