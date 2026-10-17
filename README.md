# ShopWave

E-commerce backend built with Spring Boot, with JWT authentication, admin/user roles, and a PostgreSQL database managed by Flyway migrations.

> **Status:** work in progress. This project is being built step by step as a hands-on way to learn backend development with Spring Boot.

## Tech stack

| Area | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.x (Web, Data JPA, Validation, Actuator) |
| Database | PostgreSQL 16 (Docker) |
| Migrations | Flyway |
| Build tool | Maven (wrapper included) |
| Local infrastructure | Docker Compose |

Planned: Spring Security with JWT, Redis caching, Kafka order events, Razorpay payments, Testcontainers, GitHub Actions CI, and deployment.

## Prerequisites

- Java 21
- Docker Desktop (running)
- Git

## Getting started

### 1. Clone the repository

```bash
git clone <repository-url>
cd shopwave
```

### 2. Create your `.env` file

Create a file named `.env` in the project root. It is ignored by Git and must never be committed.

```
DB_PASSWORD=change_me
JWT_SECRET=change_me
```

- `DB_PASSWORD`: the password for the local Postgres user. Use letters and numbers only.
- `JWT_SECRET`: generate one with `openssl rand -base64 32`.

> Postgres stores the password the first time its container is created. If you change `DB_PASSWORD` later, reset the database with `docker compose down -v` and start it again.

### 3. Start the database

```bash
docker compose up -d
```

This starts PostgreSQL 16 in a container named `shopwave-db`. The database is available on **localhost:5433** (database `shopwave`, user `shopwave`).

### 4. Run the application

From the terminal:

```bash
set -a; source .env; set +a
./mvnw spring-boot:run
```

Or run `ShopwaveApplication` from IntelliJ IDEA with the EnvFile plugin enabled and `.env` added in the run configuration.

The API starts on http://localhost:8080. Flyway applies any pending database migrations automatically on startup.

## Database migrations

Schema changes are managed with Flyway. Migration files live in `src/main/resources/db/migration` and are named `V<version>__<description>.sql`.

- Never edit a migration after it has been applied. Add a new versioned file instead.
- Hibernate runs with `ddl-auto=validate`, so it only checks that the entities match the tables.

## Project structure

```
src/main/java/com/shopwave/shopwave/
├── config/        application-wide configuration (security, beans)
├── controller/    REST controllers
├── service/       business logic
├── repository/    Spring Data JPA repositories
├── model/         JPA entities and enums
├── dto/           request and response objects
├── security/      JWT filter and related classes
└── exception/     global exception handling

src/main/resources/
├── application.properties
└── db/migration/  Flyway migrations
```

## Roadmap

- [x] Project setup with Maven, Docker Compose, and PostgreSQL
- [x] Flyway migrations and the `users` table
- [x] `Users` entity, `Role` enum, and `UserRepository`
- [ ] Spring Security configuration
- [ ] Register and login with JWT, roles `USER` and `ADMIN`
- [ ] Categories and products (admin management, public browsing)
- [ ] Cart and checkout (transactional)
- [ ] Orders and order status lifecycle
- [ ] Payments with webhooks
- [ ] Redis caching
- [ ] Kafka order events
- [ ] Automated tests (unit and integration)
- [ ] CI pipeline and deployment

## Roles

| Role | Access |
|---|---|
| `USER` | Browse products, manage own cart, place and view own orders |
| `ADMIN` | Manage products, categories, stock, and all orders |

## License

This is a personal learning project.
