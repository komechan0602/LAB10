# Lab 10: Implementation of HTTP with Java/Spring Boot

This project is a Spring Boot application implementing basic HTTP concepts using a layered architecture.

## Prerequisites

- Java 21 or higher
- Maven (included via `./mvnw`)

## Setup

1. Clone the repository
2. Create a `.env` file in the root directory based on `.env.example`:
   ```properties
   DB_URL=jdbc:sqlite:database.db
   DB_USERNAME=sa
   DB_PASSWORD=password
   ```
3. Build the project:
   ```bash
   ./mvnw clean compile
   ```
4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The application will start on `http://localhost:8080`

## Project Structure

The project follows a layered architecture:

- **Controller**: Handles HTTP requests and responses
- **Service**: Contains business logic
- **Repository**: Manages data persistence
- **Model**: Domain entities
- **DTO**: Data Transfer Objects for request validation
- **Config**: Configuration classes

## Endpoints

### Hello Endpoint
- **URL**: `/hello`
- **Method**: `GET`
- **Response**: Simple greeting message

### User Registration
- **URL**: `/api/users/register`
- **Method**: `POST`
- **Body**:
  ```json
  {
    "username": "johndoe",
    "email": "john@example.com",
    "password": "securepassword"
  }
  ```

### User Login
- **URL**: `/api/users/login`
- **Method**: `POST`
- **Body**:
  ```json
  {
    "email": "john@example.com",
    "password": "securepassword"
  }
  ```

## Technologies Used

- Spring Boot 4.0.1
- Spring Web
- Spring Security
- Spring Data JPA
- Flyway (Database Migration)
- SQLite Database
- Jakarta Validation
