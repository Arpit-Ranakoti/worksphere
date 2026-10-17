# WorkSphere — Complete Full-Stack Project(anshika made some changes original is below my part)

WorkSphere is a full-stack job platform: an HTML/CSS/JavaScript frontend and a Java Spring Boot backend with JWT authentication. It runs in two database modes:

| Mode | Database | When to use |
|------|----------|-------------|
| Demo (default) | H2 in-memory | Quick test. Data is wiped on every restart. |
| MySQL | MySQL 8+ | Real usage. Data persists between restarts. |

## Project structure

```
worksphere/
├── README.md
├── CHANGES.md
├── database/
│   └── worksphere.sql
├── docs/
│   ├── project-overview.md
│   ├── database-design.md
│   ├── api-documentation.md
│   └── viva-questions.md
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/worksphere/
│       │   │   ├── WorkSphereApplication.java
│       │   │   ├── config/          (includes DataInitializer)
│       │   │   ├── security/
│       │   │   ├── entity/
│       │   │   ├── dto/
│       │   │   ├── repository/
│       │   │   ├── service/
│       │   │   ├── controller/
│       │   │   ├── exception/
│       │   │   └── util/
│       │   └── resources/
│       │       ├── application.properties         (H2 demo mode)
│       │       ├── application-mysql.properties   (MySQL profile)
│       │       ├── schema.sql
│       │       └── data.sql
│       └── test/java/com/worksphere/
└── frontend/
    ├── *.html
    ├── css/style.css
    └── js/*.js
```

## Tech stack

- Java 17+ and Spring Boot 3.5.5 (Spring Security, Spring Data JPA, Hibernate)
- JWT authentication
- MySQL 8+ (or H2 for demo mode)
- Maven 3.9+
- HTML, CSS and vanilla JavaScript frontend

## Requirements

- JDK 17 or newer
- Maven 3.9+
- MySQL 8+ (only for MySQL mode)
- A browser
- Python (only to serve the frontend with `python -m http.server`)

---

## Run with MySQL (recommended)

These steps are for Windows PowerShell. Commands for Command Prompt, macOS and Linux are noted where they differ.

### Step 1: Install and start MySQL

Install MySQL Server 8+ and make sure the MySQL service is running on port 3306. Check that you can log in:

```powershell
mysql -u root -p
```

If `mysql` is not recognized, add MySQL's `bin` folder to your PATH, or call it with the full path, for example:

```powershell
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p
```

### Step 2: Create the database

Easiest option: create an empty database. The backend builds all the tables itself on first start and seeds the demo data.

```powershell
mysql -u root -p -e "CREATE DATABASE worksphere;"
```

Optional: you can instead load the provided SQL script, which creates the schema and sample skills.

```powershell
# PowerShell (the "<" redirect does not work in PowerShell)
Get-Content database/worksphere.sql | mysql -u root -p
```

```cmd
:: Command Prompt, macOS, Linux
mysql -u root -p < database/worksphere.sql
```

Either way works. The backend checks for existing skills before inserting them, so loading the script does not cause errors.

### Step 3: Configure the connection

The MySQL settings live in `backend/src/main/resources/application-mysql.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/worksphere?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD:YOUR_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.sql.init.mode=never
```

Provide your MySQL password through the `DB_PASSWORD` environment variable so it never gets committed to GitHub:

```powershell
# PowerShell (applies to this terminal window only)
$env:DB_PASSWORD="your_mysql_password"
```

```bash
# macOS / Linux
export DB_PASSWORD="your_mysql_password"
```

If your MySQL user is not `root`, change `spring.datasource.username` too.

### Step 4: Start the backend with the MySQL profile

Run this from the `backend` folder (the folder that contains `pom.xml`):

```powershell
cd backend
mvn spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=mysql"
```

On Linux/macOS or Command Prompt you can drop the outer quotes:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=mysql"
```

The log should show `The following 1 profile is active: "mysql"` and end with `Started WorkSphereApplication`. The API is then available at `http://localhost:8080/api`.

Using an IDE (IntelliJ or Eclipse)? Run `WorkSphereApplication` with the environment variable `SPRING_PROFILES_ACTIVE=mysql` (plus `DB_PASSWORD`).

### Step 5: Verify the data in MySQL

```powershell
mysql -u root -p -e "USE worksphere; SHOW TABLES; SELECT email, role FROM users; SELECT name FROM skills; SELECT title FROM jobs;"
```

Expected on first start: 5 demo users, 6 skills and 1 sample job.

### Step 6: Start the frontend

Open a second terminal:

```powershell
cd frontend
python -m http.server 5500
```

Open `http://localhost:5500` in your browser. Do not open the HTML files directly with `file://`.

If the backend runs somewhere else, change the API address in `frontend/js/config.js` (default `http://localhost:8080/api`).

### Resetting to a clean state

```powershell
mysql -u root -p -e "DROP DATABASE worksphere; CREATE DATABASE worksphere;"
```

Restart the backend and it rebuilds the tables and demo data.

---

## Run in demo mode (H2, no MySQL needed)

```powershell
cd backend
mvn spring-boot:run
```

This uses an in-memory H2 database, so all data disappears when you stop the app. Start the frontend as in Step 6 above.

---

## Demo accounts

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@worksphere.com | Admin@123 |
| Worker | worker1@worksphere.com | Worker@123 |
| Worker | worker2@worksphere.com | Worker@123 |
| Worker | worker3@worksphere.com | Worker@123 |
| Employer | employer1@worksphere.com | Employer@123 |

## Browser test flow

**Employer:** log in as `employer1@worksphere.com`, open the dashboard, post a job, view applications, select or reject workers, manage commitments, complete the work and submit a rating.

**Worker:** log in as `worker1@worksphere.com`, browse and search jobs, apply, check applications and notifications, confirm/decline/cancel/complete commitments, raise a dispute if needed, and rate the other party after completion.

**Admin:** log in as `admin@worksphere.com`, review users, jobs, applications, commitments, ratings, strikes and disputes, and resolve a dispute.

## Troubleshooting

| Problem | Cause and fix |
|---------|---------------|
| `The '<' operator is reserved for future use` | You are in PowerShell. Use `Get-Content database/worksphere.sql \| mysql -u root -p` instead. |
| `No plugin found for prefix 'spring-boot'` | You ran Maven outside the `backend` folder. `cd backend` first. |
| `Access denied for user 'root'` | Wrong password. Check `DB_PASSWORD` is set in the same terminal you run Maven from. |
| `Communications link failure` | MySQL is not running, or not on port 3306. |
| `Unknown database 'worksphere'` | Run Step 2 to create the database. |
| `Driver org.h2.Driver claims to not accept jdbcUrl` | `application-mysql.properties` is missing the `spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver` line. |
| `Duplicate entry '...' for key 'skills.uk_skills_name'` | Old `DataInitializer` without the skills existence check. See `CHANGES.md`. |
| Demo users missing after a failed first start | A crashed first run can leave partial data. Reset the database (see above). |
| `H2 console available ...` appears in the log | Harmless. It is only a log line from the base properties; the app is using MySQL. |

## Security notes

- Never commit your real MySQL password. Use the `DB_PASSWORD` environment variable.
- Change `worksphere.jwt.secret` in `application.properties` before any real deployment. The current value is for demos only.

## Documentation

- `CHANGES.md` for the list of changes made for the MySQL integration
- `docs/project-overview.md`
- `docs/database-design.md`
- `docs/api-documentation.md`
- `docs/viva-questions.md`

## Validation performed

- Backend runs with the `mysql` profile against MySQL 8.0 (Hibernate connects, tables are created, demo data is seeded).
- Data verified in MySQL: 5 users, 6 skills, 1 job.
- Frontend JavaScript files were syntax-checked with Node.js.
#-------------------------------------------------------------------------------------------------------------
# WorkSphere — Complete Full-Stack Project

WorkSphere is a complete frontend + Java Spring Boot backend project organized exactly into the requested `worksphere/` structure.

## Final structure

```text
worksphere/
├── README.md
├── database/
│   └── worksphere.sql
├── docs/
│   ├── project-overview.md
│   ├── database-design.md
│   ├── api-documentation.md
│   └── viva-questions.md
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/worksphere/
│       │   │   ├── WorkSphereApplication.java
│       │   │   ├── config/
│       │   │   ├── security/
│       │   │   ├── entity/
│       │   │   ├── dto/
│       │   │   ├── repository/
│       │   │   ├── service/
│       │   │   ├── controller/
│       │   │   ├── exception/
│       │   │   └── util/
│       │   └── resources/
│       │       ├── application.properties
│       │       ├── application-mysql.properties
│       │       ├── schema.sql
│       │       └── data.sql
│       └── test/java/com/worksphere/
└── frontend/
    ├── *.html
    ├── css/style.css
    └── js/*.js
```

## What is included

- Complete supplied frontend pages, CSS, and JavaScript.
- Java 17+ Spring Boot 3.5.5 backend.
- JWT authentication and Spring Security.
- JPA entities, repositories, services, controllers, DTOs, exceptions, and utilities.
- H2 demo mode and MySQL configuration.
- Demo user/data initializer.
- SQL database design at `database/worksphere.sql`.
- Project overview, database design, API documentation, and viva preparation in `docs/`.
- Backend unit/integration test source files.

## Important source note

The Claude-provided material contained the frontend implementation and backend architecture/API expectations, but it did not contain actual Java backend source files. The Java backend in this package was therefore implemented to match the frontend's real API calls and the requested package/file structure rather than pretending nonexistent backend source had been copied.

## How to run — easiest demo mode

### 1. Requirements

Install:

- JDK 17 or newer
- Maven 3.9+
- A browser

### 2. Start the backend

Open a terminal in:

```text
worksphere/backend
```

Run:

```bash
mvn spring-boot:run
```

The API starts at:

```text
http://localhost:8080/api
```

The default configuration uses an in-memory H2 database, so MySQL is not required for the demo.

### 3. Start the frontend

Open another terminal in:

```text
worksphere/frontend
```

Run a simple static server, for example with Python:

```bash
python -m http.server 5500
```

Then open:

```text
http://localhost:5500
```

Do not open the HTML files directly with `file://`; serve the frontend through HTTP so browser requests work consistently.

### 4. Demo accounts

```text
Admin:
admin@worksphere.com / Admin@123

Worker:
worker1@worksphere.com / Worker@123

Worker:
worker2@worksphere.com / Worker@123

Worker:
worker3@worksphere.com / Worker@123

Employer:
employer1@worksphere.com / Employer@123
```

### 5. Browser test flow

#### Employer

1. Log in as `employer1@worksphere.com`.
2. Open employer dashboard.
3. Create/post a job.
4. View applications.
5. Select or reject workers.
6. Manage commitments.
7. Complete work and submit a rating.

#### Worker

1. Log in as `worker1@worksphere.com`.
2. Open jobs and search/review a job.
3. Apply to a job.
4. Check applications and notifications.
5. Confirm/decline/cancel/complete commitments as allowed by the workflow.
6. Raise a dispute if required.
7. Rate the other party after completion.

#### Admin

1. Log in as `admin@worksphere.com`.
2. Open admin dashboard.
3. Review users, jobs, applications, commitments, ratings, strikes, and disputes.
4. Resolve a dispute from the admin workflow.

## MySQL mode

1. Install MySQL 8+.
2. Run `database/worksphere.sql`.
3. Configure the database password in `backend/src/main/resources/application-mysql.properties`.
4. Start with the MySQL configuration, for example:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=mysql"
```

If your environment uses the provided `application-mysql.properties` as a named configuration instead of a profile, use the corresponding Spring configuration command from your environment. The important values are the MySQL URL, database username, and password.

## Frontend API configuration

Change only this value if the backend runs somewhere else:

```text
frontend/js/config.js
```

Default:

```text
http://localhost:8080/api
```

## Validation performed for this package

- Requested core backend file tree checked: no missing core files.
- Frontend HTML/JS/CSS files are included.
- Java source files were scanned for basic brace-balance errors.
- Frontend JavaScript files were syntax-checked with Node.js.
- Final archive contains the root `worksphere/` folder with frontend, backend, database, docs, and README.

A full Maven compile/server execution could not be completed in this environment because the `mvn` executable is not installed here. Therefore this package is not being falsely represented as having been live-run in this environment.

## Documentation

See:

- `docs/project-overview.md`
- `docs/database-design.md`
- `docs/api-documentation.md`
- `docs/viva-questions.md`
