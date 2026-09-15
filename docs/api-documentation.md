# WorkSphere — API Documentation

Base URL: `http://localhost:8080/api`

Authentication: JWT Bearer token. After login, the frontend stores the token and sends it in the `Authorization: Bearer <token>` header.

## Authentication

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/auth/register` | Register a new user |
| POST | `/auth/login` | Authenticate and receive JWT |

## Users and profiles

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/users/me` | Current user |
| GET | `/users/{id}/ratings` | Ratings for a user |
| GET | `/users/me/worker-profile` | Get current worker profile |
| PUT | `/users/me/worker-profile` | Create/update worker profile |
| GET | `/users/me/employer-profile` | Get current employer profile |
| PUT | `/users/me/employer-profile` | Create/update employer profile |
| GET | `/skills` | List skills |

## Jobs

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/jobs` | Search/filter jobs |
| GET | `/jobs/{id}` | Get job details |
| GET | `/jobs/my` | Employer's jobs |
| GET | `/jobs/{jobId}/applications` | Applications for an employer job |
| POST | `/jobs` | Create a job |
| PUT | `/jobs/{id}` | Update a job |
| DELETE | `/jobs/{id}` | Delete a job |

Supported search query parameters include `keyword`, `location`, `category`, `date`, `minPay`, `maxPay`, `recommended`, and `size` when supported by the frontend/request layer.

## Applications

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/applications` | Apply for a job |
| GET | `/applications/my` | Current user's applications |
| PUT | `/applications/{id}/withdraw` | Withdraw an application |
| PUT | `/applications/{id}/select` | Employer selects an application |
| PUT | `/applications/{id}/reject` | Employer rejects an application |

## Commitments

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/commitments/my` | Current user's commitments |
| GET | `/commitments/my?upcoming=true` | Upcoming commitments |
| POST | `/commitments/{id}/confirm` | Confirm commitment |
| POST | `/commitments/{id}/decline` | Decline commitment |
| POST | `/commitments/{id}/cancel` | Cancel commitment; body may contain `reason` |
| POST | `/commitments/{id}/complete` | Mark commitment completed |

## Notifications

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/notifications` | Current user's notifications |
| PUT | `/notifications/{id}/read` | Mark notification read |

## Ratings

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/ratings` | Submit a rating/review |

## Disputes

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/disputes` | Create dispute |
| GET | `/disputes/my` | Current user's disputes |

## Admin

Admin endpoints require the `ADMIN` role.

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/admin/dashboard` | Dashboard counts |
| GET | `/admin/users` | User administration list |
| GET | `/admin/jobs` | Job administration list |
| GET | `/admin/applications` | Application administration list |
| GET | `/admin/commitments` | Commitment administration list |
| GET | `/admin/ratings` | Rating administration list |
| GET | `/admin/strikes` | Strike administration list |
| GET | `/admin/disputes` | Dispute administration list |
| PUT | `/admin/disputes/{id}/resolve` | Resolve a dispute |

## Security behavior

- `/api/auth/**` is public.
- Public job browsing is allowed for GET job endpoints.
- Other API operations require authentication.
- `/api/admin/**` requires the admin role.
- Password hashes are not serialized in normal user JSON responses.
