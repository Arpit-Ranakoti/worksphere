# Changes: MySQL integration
 
This document lists every change made to move WorkSphere from the in-memory H2 demo database to a persistent MySQL database, while keeping H2 demo mode working.
 
## Summary
 
| # | File | Change | Why |
|---|------|--------|-----|
| 1 | `backend/src/main/resources/application-mysql.properties` | Added `spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver` | The base `application.properties` sets the H2 driver. The mysql profile did not override it, so Spring would try the H2 driver with a MySQL URL. |
| 2 | `backend/src/main/resources/application-mysql.properties` | Password now read from `${DB_PASSWORD:YOUR_PASSWORD}` | Keeps the real password out of GitHub. |
| 3 | `backend/src/main/java/com/worksphere/config/DataInitializer.java` | Skills are only inserted if they do not already exist | MySQL data persists, so re-seeding crashed with `Duplicate entry 'Data Entry' for key 'skills.uk_skills_name'`. |
| 4 | `README.md` | Rewrote the MySQL section with step-by-step Windows PowerShell instructions, troubleshooting table and security notes | The old section was a short outline. |
| 5 | `CHANGES.md` | New file (this document) | Records the integration. |
 
No changes were needed to `pom.xml`: the MySQL connector (`mysql-connector-j`) was already a dependency, and H2 stays available for demo mode.
 
## 1 and 2. `application-mysql.properties`
 
Final contents:
 
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/worksphere?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD:YOUR_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.sql.init.mode=never
```
 
Only the `driver-class-name` and `password` lines are new. The profile is activated with `--spring.profiles.active=mysql`, and Spring merges it over `application.properties`.
 
## 3. `DataInitializer.java`
 
Before:
 
```java
String[] names = {"Events", "Delivery", "Data Entry", "Customer Service", "Cleaning", "Marketing"};
for (String n : names) {
    skills.save(new Skill(n));
}
```
 
After:
 
```java
String[] names = {"Events", "Delivery", "Data Entry", "Customer Service", "Cleaning", "Marketing"};
for (String n : names) {
    if (skills.findByNameIgnoreCase(n).isEmpty()) {
        skills.save(new Skill(n));
    }
}
```
 
### Why this was needed
 
`DataInitializer` seeds demo data only when `users.count() == 0`. With H2 the database started empty on every run, so this never mattered. With MySQL, if `database/worksphere.sql` has already inserted the skills but no users, the initializer inserted the users and then crashed on the first duplicate skill. Checking before each insert makes the seeding safe to run on any starting state.
 
## Verification
 
After the changes, a fresh MySQL database was started with the `mysql` profile. The log showed the profile active, a MySQL connection from HikariCP, and `Started WorkSphereApplication`. Querying the database returned:
 
- 5 users: admin, employer1, worker1, worker2, worker3
- 6 skills: Cleaning, Customer Service, Data Entry, Delivery, Events, Marketing
- 1 job: Event Assistant
## Known leftovers (not changed)
 
- `spring.h2.console.enabled=true` stays in `application.properties`, which causes a harmless `H2 console available at '/h2-console'` log line in MySQL mode. To remove it, add `spring.h2.console.enabled=false` to `application-mysql.properties`.
- `worksphere.jwt.secret` is still the demo value. Change it before any real deployment.
 