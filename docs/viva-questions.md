# WorkSphere — Viva Questions and Answers

## 1. What is WorkSphere?

WorkSphere is a role-based job marketplace that connects workers/job seekers and employers through jobs, applications, commitments, ratings, notifications, and disputes.

## 2. Why use Spring Boot?

Spring Boot reduces boilerplate for REST APIs, dependency injection, configuration, validation, security, and database integration while keeping the backend modular.

## 3. Why JWT authentication?

JWT provides a stateless authentication mechanism. The frontend receives a signed token at login and sends it with later API requests.

## 4. What is the purpose of Spring Security?

Spring Security protects API endpoints, authenticates users from the JWT, and enforces role-based authorization such as admin-only endpoints.

## 5. Why use DTOs?

DTOs separate the HTTP API contract from persistence entities. They also allow request validation and safer response shaping.

## 6. Why use repositories?

Spring Data JPA repositories provide database access methods without requiring repetitive JDBC code.

## 7. What is the difference between an application and a commitment?

An application is a worker's request to work on a job. A commitment represents the selected/confirmed work relationship after the employer accepts an application.

## 8. What is the role of `JobStatus`?

It models the job lifecycle, including open, full, in-progress, completed, cancelled, and expired states.

## 9. Why are enums stored as strings?

String enum persistence is easier to read in a database and avoids coupling the stored value to enum ordinal positions.

## 10. How does the frontend connect to the backend?

`frontend/js/config.js` defines the API base URL, and `frontend/js/app.js` provides the shared request/authentication behavior. The backend listens on port 8080.

## 11. How does CORS help?

CORS allows a browser-served frontend on a different local origin/port to call the backend API when the backend explicitly permits that origin.

## 12. Why is H2 included?

H2 makes the project easy to demonstrate without installing MySQL. The default demo configuration creates an in-memory database at application startup.

## 13. How can MySQL be used?

Create the `worksphere` database, configure the MySQL connection/password, and start the backend with the MySQL configuration described in the project README.

## 14. What does `DataInitializer` do?

It creates safe demo accounts and starter data at startup, including BCrypt-encoded passwords, so the application can be demonstrated immediately.

## 15. How are duplicate applications prevented?

The `applications` table has a unique constraint on `job_id` and `worker_id`, and the service layer also validates the workflow.

## 16. What is a dispute?

A dispute is an issue raised against a commitment. It can be reviewed and resolved by the admin workflow.

## 17. What should be changed before production?

Change the JWT secret, use a managed MySQL database, configure production CORS, use HTTPS, move secrets to environment variables/secret storage, add migrations, and harden operational logging/monitoring.

## 18. What is the current demo limitation?

The package is assembled and statically validated in this environment, but a full Maven server run could not be performed here because Maven is not installed in the execution environment. The included `pom.xml`, application configuration, tests, and run instructions are ready for a normal Java/Maven environment.
