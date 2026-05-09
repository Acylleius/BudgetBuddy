# Running BudgetBuddy

Start the Spring Boot backend from the backend project folder:

```powershell
cd backend\budgetbuddy
.\mvnw.cmd spring-boot:run
```

Then open:

```text
http://localhost:8080/
```

The backend serves the files in the root `web` folder and the API from the same server, so the login and register pages can call `/api/v1` without needing a separate web server.
