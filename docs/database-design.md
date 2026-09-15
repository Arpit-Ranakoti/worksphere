# WorkSphere — Database Design

## 1. Database model

The database is centered on `users`, with role-specific profiles and marketplace workflow tables.

### Core tables

- `users` — account identity, role, status, and authentication data.
- `worker_profiles` — worker location, bio, rating and availability.
- `employer_profiles` — employer/company details and rating.
- `skills` — reusable skill catalogue.
- `worker_skills` — worker-to-skill mapping.
- `jobs` — employer-created job opportunities.
- `job_skills` — job-to-skill mapping.
- `applications` — worker applications to jobs.
- `commitments` — selected worker/employer work agreement lifecycle.
- `notifications` — user-facing workflow notifications.
- `ratings` — post-commitment feedback between parties.
- `strikes` — moderation records for users.
- `disputes` — issues raised against commitments and their resolutions.

## 2. Relationships

- One `User` can have one `WorkerProfile` or one `EmployerProfile` depending on role.
- One employer `User` can own many `Job` records.
- `WorkerProfile` and `Job` both reference many `Skill` records through `worker_skills` and `job_skills`.
- A `Job` can have many `Application` records.
- An `Application` belongs to one worker and one job.
- A selected application can be associated with a `Commitment`.
- A `Commitment` references one job, worker, employer, and optionally its source application.
- A commitment can have ratings and disputes.
- A user can have many notifications and strikes.

## 3. Enumerations

The Java enums are stored as strings for readability and compatibility:

- `Role`: `JOB_SEEKER`, `EMPLOYER`, `ADMIN`
- `JobStatus`: `OPEN`, `FULL`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`, `EXPIRED`
- `ApplicationStatus`: `PENDING`, `SELECTED`, `REJECTED`, `WITHDRAWN`
- `CommitmentStatus`: `PENDING_CONFIRMATION`, `PENDING`, `CONFIRMED`, `DECLINED`, `CANCELLED`, `COMPLETED`, `NO_SHOW`
- `DepositStatus`: `NOT_REQUIRED`, `PENDING`, `HELD`, `REFUNDED`, `FORFEITED`
- `NotificationType`: `APPLICATION`, `SELECTION`, `COMMITMENT`, `JOB`, `RATING`, `DISPUTE`, `SYSTEM`
- `StrikeStatus`: `ACTIVE`, `EXPIRED`, `REVOKED`
- `DisputeStatus`: `OPEN_DISPUTE`, `UNDER_REVIEW`, `RESOLVED`

## 4. Constraints

- User emails are unique.
- Skill names are unique.
- A worker cannot apply to the same job more than once (`job_id + worker_id`).
- Worker/job skill mappings are unique per pair.
- Ratings are unique per commitment/from-user/to-user combination.

## 5. SQL deployment

`database/worksphere.sql` is the explicit MySQL schema for the project. The demo Spring Boot configuration instead uses H2 and Hibernate `create-drop`, allowing the project to run without a MySQL server.
