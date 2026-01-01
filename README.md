# Lab 10 - Implementation of HTTP

A Spring Boot project for learning secure request processing, input validation, and HTTP response handling.

## What I Learned

In this lab, I implemented:
- REST API endpoints with proper HTTP methods (GET, POST)
- Request header parsing using `@RequestHeader`
- JSON body parsing with DTOs and Jackson (`@RequestBody`)
- Form data handling with `@ModelAttribute`
- Input validation using Jakarta annotations (`@NotNull`, `@Email`, `@Size`)
- A custom validator for username rules (`@ValidUsername`)
- Proper HTTP status codes (200, 201, 400, 401, 404, 415, 500)
- Global exception handling with `@ControllerAdvice`

## How to Run

**Prerequisites:** Java 21+

```bash
# Build
./mvnw clean compile

# Run
./mvnw spring-boot:run -Dspring.devtools.restart.enabled=false
```

Server starts at http://localhost:8080

To stop: `lsof -ti:8080 | xargs kill -9`

## API Documentation

### Endpoints Overview

| Method | Path | Description | Status Codes |
|--------|------|-------------|--------------|
| GET | `/` | Welcome page | 200 |
| GET | `/hello` | Simple greeting | 200 |
| POST | `/api/users/register` | Register user (JSON body) | 201, 400, 415 |
| POST | `/api/users/register-form` | Register user (form data) | 201, 400, 415 |
| POST | `/api/users/login` | Login (JSON body) | 200, 400, 401, 415 |
| POST | `/api/users/login-form` | Login (form data) | 200, 400, 401, 415 |
| GET | `/api/users/info` | List all users | 200 |
| GET | `/api/users/{id}` | Get user by ID | 200, 404 |
| GET | `/api/users/search?email=` | Find user by email | 200, 404 |
| GET | `/api/users/headers` | Echo request headers | 200 |

### Request/Response Details

**POST /api/users/register** - JSON registration
- Headers: `Content-Type: application/json`
- Body: `{"username": "...", "email": "...", "password": "..."}`
- Returns: User object (without password) with 201 Created

**POST /api/users/register-form** - Form registration  
- Headers: `Content-Type: application/x-www-form-urlencoded`
- Body: `username=...&email=...&password=...`
- Returns: User object (without password) with 201 Created

**POST /api/users/login** - JSON login
- Headers: `Content-Type: application/json`  
- Body: `{"email": "...", "password": "..."}`
- Returns: `{"message": "Login successful"}` with 200 OK
- Or: `{"error": "Invalid credentials"}` with 401 Unauthorized

**GET /api/users/{id}** - Get user with header echo
- Optional Header: `X-Request-Id` (echoed back in response)
- Returns: User object with 200 OK or 404 Not Found

**GET /api/users/search?email=...** - Search by email
- Query param: `email` (required)
- Optional Header: `X-Client-Version` (echoed back in response)
- Returns: User object with 200 OK or 404 Not Found

**GET /api/users/headers** - Header parsing demo
- Required Header: `User-Agent`
- Optional Headers: `Accept`, `X-Request-Id`
- Returns: JSON with all parsed headers

### Input Validation

The DTOs use Jakarta validation annotations:

```java
// CreateUserRequest.java
@NotBlank
@Size(min = 3, max = 30)
@ValidUsername  // Custom: must start with letter, only letters/numbers/underscores
private String username;

@Email
@NotBlank
private String email;

@NotBlank
@Size(min = 8, max = 64)
private String password;
```

Validation errors return 400 with structured JSON:
```json
{
  "errors": {
    "username": "size must be between 3 and 30",
    "email": "must be a well-formed email address"
  }
}
```

### Error Responses

| Code | When |
|------|------|
| 400 | Validation failed, malformed JSON, duplicate email |
| 401 | Wrong password during login |
| 404 | User not found (by ID or email) |
| 415 | Wrong Content-Type header |
| 500 | Unexpected server error |

All errors return JSON: `{"error": "message here"}`

## Testing with curl

```bash
# Register a user
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","password":"password123"}'

# Register with form data
curl -X POST http://localhost:8080/api/users/register-form \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=jane&email=jane@example.com&password=password123"

# Login
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"password123"}'

# Get all users
curl http://localhost:8080/api/users/info

# Get user by ID (with header echo)
curl -H "X-Request-Id: test-123" http://localhost:8080/api/users/1 -i

# Search by email
curl "http://localhost:8080/api/users/search?email=john@example.com"

# Test header parsing
curl http://localhost:8080/api/users/headers \
  -H "User-Agent: MyCurl/1.0" \
  -H "X-Request-Id: req-456"

# Test validation error (invalid username)
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"123bad","email":"test@test.com","password":"password123"}'

# Test 415 error (wrong content type)
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: text/plain" \
  -d '{"username":"test","email":"t@t.com","password":"password123"}'
```

## Project Structure

```
src/main/java/com/example/lab10/
├── Lab10Application.java          # Main class
├── config/
│   ├── GlobalExceptionHandler.java  # Handles all errors (400, 404, 415, 500)
│   └── SecurityConfig.java          # BCrypt password encoder
├── controller/
│   ├── HelloController.java         # Basic GET endpoints
│   └── UserController.java          # User API with validation
├── dto/
│   ├── CreateUserRequest.java       # Registration DTO with validation
│   ├── LoginRequest.java            # Login DTO with validation
│   └── UserResponse.java            # Response DTO (no password)
├── exception/
│   └── ResourceNotFoundException.java  # Custom 404 exception
├── model/
│   └── User.java                    # JPA entity
├── repository/
│   └── UserRepository.java          # Spring Data JPA
├── service/
│   └── UserService.java             # Business logic
└── validation/
    ├── ValidUsername.java           # Custom annotation
    └── UsernameValidator.java       # Custom validator logic
```

## Technologies

- Spring Boot 4.0.1
- Spring Web (REST controllers)
- Spring Security (password hashing)
- Spring Data JPA + Hibernate
- Jakarta Validation
- SQLite Database
- Flyway (migrations)
