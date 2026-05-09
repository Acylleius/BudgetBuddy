# Running BudgetBuddy

From the repository root, run:

```powershell
.\mvnw.cmd spring-boot:run
```

Then open:

```text
http://localhost:8080/
```

You can also run backend tests from the repository root:

```powershell
.\mvnw.cmd test
```

Alternative backend folder command:

```powershell
cd backend\budgetbuddy
.\mvnw.cmd spring-boot:run
```

The backend serves the files in the root `web` folder and the API from the same server, so the login and register pages can call `/api/v1` without needing a separate web server.
