package com.campusskill.controller;

import com.campusskill.dto.AdminStatsResponse;
import com.campusskill.dto.UserResponse;
import com.campusskill.service.AdminService;
import com.campusskill.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AuthService authService;
    private final AdminService adminService;

    public AdminController(AuthService authService, AdminService adminService) {
        this.authService = authService;
        this.adminService = adminService;
    }

    @GetMapping("/stats")
    public AdminStatsResponse stats(HttpSession session) {
        return adminService.getStats(authService.currentUserId(session));
    }

    @GetMapping("/users")
    public List<UserResponse> listUsers(@RequestParam(required = false) String q, HttpSession session) {
        return adminService.listUsers(authService.currentUserId(session), q);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, HttpSession session) {
        adminService.deleteUser(authService.currentUserId(session), id);
        return ResponseEntity.noContent().build();
    }
}
