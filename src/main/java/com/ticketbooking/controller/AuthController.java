package com.ticketbooking.controller;

import com.ticketbooking.dto.AuthResponse;
import com.ticketbooking.dto.LoginRequest;
import com.ticketbooking.dto.RegisterRequest;
import com.ticketbooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        userService.register(request.getEmail(), request.getPassword(), request.getRole());
        AuthResponse response = new AuthResponse("Registration successful", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        String token = userService.login(request.getEmail(), request.getPassword());
        AuthResponse response = new AuthResponse("Login successful", token);
        return ResponseEntity.ok(response);
    }
}
