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
