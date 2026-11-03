package com.campusskill.controller;

import com.campusskill.dto.TutorResponse;
import com.campusskill.service.AuthService;
import com.campusskill.service.TutorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tutors")
public class TutorController {

    private final AuthService authService;
    private final TutorService tutorService;

    public TutorController(AuthService authService, TutorService tutorService) {
        this.authService = authService;
        this.tutorService = tutorService;
    }

    @GetMapping
    public List<TutorResponse> findTutors(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) Integer year,
            HttpSession session) {
        return tutorService.findTutors(authService.currentUserId(session), skill, branch, year);
    }
}
