package com.campusskill.controller;

import com.campusskill.dto.DashboardResponse;
import com.campusskill.service.AuthService;
import com.campusskill.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final AuthService authService;
    private final DashboardService dashboardService;

    public DashboardController(AuthService authService, DashboardService dashboardService) {
        this.authService = authService;
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public DashboardResponse getDashboard(HttpSession session) {
        return dashboardService.getDashboard(authService.currentUserId(session));
    }
}
