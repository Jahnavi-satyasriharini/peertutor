package com.campusskill.controller;

import com.campusskill.dto.LoginRequest;
import com.campusskill.dto.SignupRequest;
import com.campusskill.dto.UserResponse;
import com.campusskill.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest body, HttpServletRequest request) {
        UserResponse user = authService.signup(body);
        authService.startSession(request, user.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody LoginRequest body, HttpServletRequest request) {
        UserResponse user = authService.login(body);
        authService.startSession(request, user.id());
        return user;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        authService.endSession(session);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public UserResponse me(HttpSession session) {
        return authService.currentUser(session);
    }
}
