# OneMinute Authentication Service

This repository contains a Spring Boot service that provides user registration and authentication using JWT tokens.

## Prerequisites

- Java 21
- Maven
- MySQL database

## Configuration

The service reads sensitive information from environment variables. The following variables can be set:

- `DB_URL` - JDBC connection string (default `jdbc:mysql://localhost:3306/authdb`)
- `DB_USERNAME` - database username (default `root`)
- `DB_PASSWORD` - database password (default `root`)
- `JWT_SECRET` - secret key used to sign tokens (required)
- `JWT_EXPIRATION` - token lifetime in milliseconds (default `86400000`)

You can define these in an `.env` file or export them in your shell before running the service.

## Building and Running

```bash
cd services/auth-service
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`.

## Running Tests

```bash
./mvnw test
```
