# Asset Management System — Spring Boot conversion

This is your original NetBeans/Tomcat project (Servlets + JSP + raw
Hibernate/JDBC) rewritten as a self-contained **Spring Boot** application.
Everything — the web server (embedded Tomcat), the MVC layer, the
database access, the login/role filter, the default-account seeding —
runs inside this one Spring Boot process. No NetBeans, no external
Tomcat, no Ant build required.

## How each original piece maps to the new project

| Original file | Replaced by |
|---|---|
| `Login_servlet.java` | `controller/AuthController.java` (`/Login_servlet`) |
| `RegisterServlet.java` | `controller/AuthController.java` (`/RegisterServlet`) |
| `AssetRegistrationServlet.java` | `controller/AssetController.java` |
| `AssetDeleteServlet.java` | `controller/AssetController.java` |
| `AuthFilter.java` | `config/AuthFilter.java` (same session/role rules, now a Spring bean) |
| `AppInitListener.java` | `config/DataSeeder.java` (table creation now handled by `ddl-auto=update`) |
| `HibernateUtil.java` / raw `Session` code | Spring Data JPA repositories (`repository/*`) |
| `DBUtil.java` / manual JDBC in the JSPs | Spring Data JPA + `application.properties` datasource |
| `Asset.java` (Hibernate entity) | `entity/Asset.java` (JPA entity, same `assets` table) |
| `asset_reg.jsp`, `asset_assignment.jsp`, `maintenance_request.jsp`, `login.html`, `register.html`, `employee_dashboard.html`, `profile.html` (JSP scriptlets) | Thymeleaf templates in `src/main/resources/templates/` |
| `admin_dashboard.html`, `asset_registration.html`, `asset_list.html` (static) | Same content, served as Thymeleaf templates |

All URL paths were kept **identical** to the original
(`/login.html`, `/Login_servlet`, `/asset_reg.jsp`,
`/AssetRegistrationServlet`, `/AssetDeleteServlet`,
`/asset_assignment.jsp`, `/maintenance_request.jsp`, etc.), so the
page behaviour and navigation are the same as before.

Database tables also keep their original names (`Login_info`,
`asset_assigned`, `asset_maintenance`, `assets`), so if you point this
at your existing `asset_management` database it will keep using the
same data.

## Before you run it

1. Make sure MySQL is running and reachable. By default this project
   points at the same URL/credentials the original project used:
   `jdbc:mysql://localhost:3301/asset_management`, user `root`.
   Change them either directly in `src/main/resources/application.properties`,
   or via environment variables when you start the app:
   ```
   DB_URL=jdbc:mysql://localhost:3306/asset_management?useSSL=false&serverTimezone=UTC
   DB_USER=root
   DB_PASSWORD=yourpassword
   ```
2. Java 17+ and Maven must be installed.

## Run it

```
mvn spring-boot:run
```

or build a runnable jar:

```
mvn clean package
java -jar target/asset-management-1.0.0.jar
```

The app starts on **http://localhost:8080/** and redirects straight to
the login page. On first startup it seeds the same two accounts the
original `AppInitListener` did:

| username | password | role |
|---|---|---|
| admin | admin123 | admin |
| student001 | student123 | student |

## Things worth knowing

- **Passwords are stored in plain text**, exactly like the original
  project. This was carried over as-is to keep behaviour identical;
  for anything beyond a demo, swap in Spring Security + `BCryptPasswordEncoder`.
- `depreciation.jsp` and `reports.html`, linked from the admin
  dashboard, weren't present in the original project either — those
  links are kept as dead links, matching the original's behaviour.
- I could not run `mvn` in the sandbox that built this (no network
  access to download dependencies), so I wasn't able to do a live
  compile/run here. The code is standard Spring Boot 3 / Jakarta EE —
  if `mvn spring-boot:run` throws anything on your machine, paste the
  error back to me and I'll fix it immediately.
