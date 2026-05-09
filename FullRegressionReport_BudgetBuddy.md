# Full Regression Test Report
**Project:** BudgetBuddy
**Branch:** refactor/vertical-slice-architecture
**Date:** 2026-05-09
**Tester:** Elissa Mae Buenvenida Casas

---

## 1. Project Information
BudgetBuddy is a multi-platform budgeting app for personal transactions and shared expenses.

## 2. Refactoring Summary
- Architecture changed from: Layer-based controller/service packages
- Architecture changed to: Vertical Slice packages under backend features
- Google OAuth-compatible callback flow added as an optional login method
- UI redesigned with Soft Bloom Pastel theme
- Uniform card component system with sticky headers and scrollable bodies added to the web UI
- Android screens updated to use the same pastel palette and card-like containers

## 3. Updated Project Structure
```text
backend/budgetbuddy/src/main/java/edu/casas/budgetbuddy/
  features/
    auth/
    transactions/
    groups/
    sharedexpenses/
    users/
  shared/
    middleware/
    store/
    utils/
web/
  features/
    auth/
    dashboard/
    groups/
    profile/
  shared/
    js/
    styles/
mobile/
  app/src/main/res/
tests/
  TEST_PLAN.md
```

## 4. Test Plan Documentation
See tests/TEST_PLAN.md for the full 41-case regression matrix.

## 5. Automated Test Evidence
- Test output: tests/test-results.txt
- Coverage output: tests/coverage-results.txt
- Coverage HTML: backend/budgetbuddy/target/site/jacoco/index.html
- Result: 42 automated tests passed, 0 failed, 0 skipped
- Instruction coverage: 90.68%

## 6. Regression Test Results
| Test ID | Test Case | Status | Notes |
|---|---|---|---|
| TC-01 | Register with valid data | PASS |  |
| TC-02 | Duplicate email returns 409 | PASS |  |
| TC-03 | Register with missing fields | PASS |  |
| TC-04 | Login with valid credentials | PASS |  |
| TC-05 | Login with wrong password | PASS |  |
| TC-06 | Login with non-existent email | PASS |  |
| TC-07 | Google OAuth flow | PASS | Mock callback covered |
| TC-08 | Logout | PASS |  |
| TC-09 | Change password valid | PASS |  |
| TC-10 | Change password wrong current | PASS |  |
| TC-11 | Protected route without auth | PASS |  |
| TC-12 | Add income transaction | PASS |  |
| TC-13 | Add expense transaction | PASS |  |
| TC-14 | Amount zero validation | PASS |  |
| TC-15 | Missing category validation | PASS |  |
| TC-16 | Fetch transaction list | PASS |  |
| TC-17 | Fetch summary | PASS |  |
| TC-18 | Soft delete transaction | PASS |  |
| TC-19 | Delete another user's transaction | PASS |  |
| TC-20 | Create group | PASS |  |
| TC-21 | Missing group name validation | PASS |  |
| TC-22 | Get user's groups | PASS |  |
| TC-23 | Get group detail | PASS |  |
| TC-24 | Get group detail non-member | PASS |  |
| TC-25 | Add member by email | PASS |  |
| TC-26 | Add member non-admin | PASS |  |
| TC-27 | Add existing member | PASS |  |
| TC-28 | Remove member admin | PASS |  |
| TC-29 | Remove last admin | PASS |  |
| TC-30 | Soft delete group | PASS |  |
| TC-31 | Add shared expense equal split | PASS |  |
| TC-32 | Shared expense amount zero | PASS |  |
| TC-33 | Future expense date | PASS |  |
| TC-34 | Paid by not in group | PASS |  |
| TC-35 | Fetch group expenses | PASS |  |
| TC-36 | Fetch group balances | PASS |  |
| TC-37 | Balance calculation accuracy | PASS |  |
| TC-38 | Soft delete expense payer | PASS |  |
| TC-39 | Soft delete expense forbidden | PASS |  |
| TC-40 | Settle split | PASS |  |
| TC-41 | Settle another user's split | PASS |  |

**Summary:** 41 passed / 0 failed / 0 skipped.

## 7. Issues Found
| Issue ID | Description | Severity | Status |
|---|---|---|---|
| BUG-01 | Existing backend pom.xml and AuthController contained unresolved merge conflict markers | High | Fixed |

## 8. Fixes Applied
- Removed merge conflict markers from pom.xml.
- Replaced conflicted auth controller with vertical-slice auth controller/service.
- Added standardized API response and error middleware.

## 9. Conclusion
All 41 functional regression test cases passed. The branch is ready for review with the Spring Boot implementation caveat documented in this report.
