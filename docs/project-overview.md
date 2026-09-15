# WorkSphere — Project Overview

## 1. Purpose

WorkSphere is a role-based job marketplace application connecting workers/job seekers with employers. The supplied frontend provides browser pages for authentication, job discovery, profiles, applications, commitments, notifications, disputes, and administration. The Java backend in this project implements the REST API expected by that frontend.

## 2. Technology stack

- Frontend: HTML5, CSS3, vanilla JavaScript
- Backend: Java 17+, Spring Boot 3.5.5
- Persistence: Spring Data JPA / Hibernate
- Demo database: H2 in-memory database
- Production-style database option: MySQL 8+
- Authentication: JWT + Spring Security
- Build: Maven

## 3. Roles

- `JOB_SEEKER`: worker who maintains a worker profile, searches jobs, applies, confirms commitments, and rates completed work.
- `EMPLOYER`: posts jobs, reviews applications, selects/rejects workers, manages commitments, and rates completed work.
- `ADMIN`: views platform dashboards and manages/resolve administrative records such as disputes.

## 4. Main workflow

1. Register or log in.
2. Receive a JWT access token.
3. Worker completes a worker profile; employer completes an employer profile.
4. Employer posts a job.
5. Worker searches/reviews jobs and submits an application.
6. Employer reviews applications and selects/rejects a worker.
7. A commitment is created and can be confirmed, declined, cancelled, or completed.
8. Either party can create a dispute when needed.
9. After completion, parties can submit ratings.
10. Notifications communicate important workflow events.
11. Admin users can inspect dashboard counts and platform records.

## 5. Frontend/backend integration

The single frontend API configuration is `frontend/js/config.js` and defaults to:

`http://localhost:8080/api`

The Spring Boot application listens on port `8080` and CORS is configured for common local frontend ports (`5500`, `5501`). The frontend's fetch wrapper injects the JWT token after login.

## 6. Demo accounts

- Admin: `admin@worksphere.com` / `Admin@123`
- Worker: `worker1@worksphere.com` / `Worker@123`
- Worker: `worker2@worksphere.com` / `Worker@123`
- Worker: `worker3@worksphere.com` / `Worker@123`
- Employer: `employer1@worksphere.com` / `Employer@123`

## 7. Important implementation note

The Claude-provided material supplied the frontend and its expected architecture/API surface; it did not contain actual Java backend source files. The backend included in this final package was therefore generated to match the frontend's actual API calls and the requested backend structure.
