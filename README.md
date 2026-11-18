# Controle Financeiro API

REST API for personal finance management: accounts, categories and transactions.
Study project in Java with Spring Boot.

The plain Java version, with the business rules, is in
[controle-financeiro-java](https://github.com/Babino0/controle-financeiro-java).

## Technologies

- Java 21
- Spring Boot (Spring Web)
- Spring Data JPA with Hibernate
- H2 (in-memory database)
- Maven

## How to run

Requirement: JDK 21.

```
.\mvnw.cmd spring-boot:run
```

On Linux and macOS, use `./mvnw spring-boot:run`. The API starts at `http://localhost:8080`.

## Persistence

Entities are mapped with JPA. Tables are created automatically in H2 on startup,
and the data is erased when the application stops. PostgreSQL will be adopted in
the next stages.

## Endpoints: categories

| Method | Path | Description | Response |
| --- | --- | --- | --- |
| POST | `/categories` | Creates a category | 201 |
| GET | `/categories` | Lists the categories | 200 |
| GET | `/categories/{id}` | Finds by id | 200 or 404 |
| DELETE | `/categories/{id}` | Deletes by id | 204 |

## Project status

- [x] Category CRUD
- [x] Accounts and transactions with balance rules
- [x] Transaction listing, period filter and expenses by category report
- [ ] Account-to-account transfers
- [ ] PostgreSQL
- [ ] JWT authentication
- [ ] Automated tests
- [ ] Docker and deploy