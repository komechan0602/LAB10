package com.example.lab10.controller;

import com.example.lab10.dto.CreateUserRequest;
import com.example.lab10.dto.LoginRequest;
import com.example.lab10.dto.UserResponse;
import com.example.lab10.model.User;
import com.example.lab10.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody CreateUserRequest request) {
        User created = userService.createUser(request);
        return new ResponseEntity<>(UserResponse.from(created), HttpStatus.CREATED);
    }

    @PostMapping(value = "/register-form", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<UserResponse> registerForm(@Valid @ModelAttribute CreateUserRequest request) {
        User created = userService.createUser(request);
        return new ResponseEntity<>(UserResponse.from(created), HttpStatus.CREATED);
    }

    @GetMapping("/register")
    public ResponseEntity<String> registerInfo() {
        return ResponseEntity.ok("Register endpoint. Please use POST to register a new user.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        if (userService.authenticate(request.getEmail(), request.getPassword())) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Login successful");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("/login")
    public ResponseEntity<String> loginInfo() {
        return ResponseEntity.ok("Login endpoint. Please use POST with email and password to login.");
    }

    @PostMapping(value = "/login-form", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> loginForm(@Valid @ModelAttribute LoginRequest request) {
        if (userService.authenticate(request.getEmail(), request.getPassword())) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Login successful (form)");
            return ResponseEntity.ok(response);
        }
        Map<String, String> response = new HashMap<>();
        response.put("error", "Invalid credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @GetMapping("/info")
    public ResponseEntity<List<UserResponse>> getUsersInfo() {
        List<UserResponse> users = userService.getAllUsers().stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id,
                                                    @RequestHeader(value = "X-Request-Id", required = false) String requestId) {
        User user = userService.getUserById(id);
        HttpHeaders headers = new HttpHeaders();
        if (requestId != null) {
            headers.add("X-Request-Id", requestId);
        }
        return new ResponseEntity<>(UserResponse.from(user), headers, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<UserResponse> findByEmail(@RequestParam("email") String email,
                                                    @RequestHeader(value = "X-Client-Version", required = false, defaultValue = "unknown") String clientVersion) {
        User user = userService.getUserByEmail(email);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Client-Version", clientVersion);
        return new ResponseEntity<>(UserResponse.from(user), headers, HttpStatus.OK);
    }

    @GetMapping("/headers")
    public ResponseEntity<Map<String, String>> echoHeaders(
            @RequestHeader("User-Agent") String userAgent,
            @RequestHeader(value = "Accept", required = false, defaultValue = "*") String accept,
            @RequestHeader(value = "X-Request-Id", required = false) String requestId) {
        Map<String, String> response = new HashMap<>();
        response.put("userAgent", userAgent);
        response.put("accept", accept);
        if (requestId != null) {
            response.put("requestId", requestId);
        }
        return ResponseEntity.ok(response);
    }
}
