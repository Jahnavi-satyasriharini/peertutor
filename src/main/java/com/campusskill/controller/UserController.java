package com.campusskill.controller;

import com.campusskill.dto.AddUserSkillRequest;
import com.campusskill.dto.ProfileResponse;
import com.campusskill.dto.UpdateProfileRequest;
import com.campusskill.service.AuthService;
import com.campusskill.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me")
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    public UserController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping
    public ProfileResponse getProfile(HttpSession session) {
        return userService.getProfile(authService.currentUserId(session));
    }

    @PutMapping
    public ProfileResponse updateProfile(@Valid @RequestBody UpdateProfileRequest body, HttpSession session) {
        return userService.updateProfile(authService.currentUserId(session), body);
    }

    @PostMapping("/skills")
    public ResponseEntity<ProfileResponse> addSkill(@Valid @RequestBody AddUserSkillRequest body, HttpSession session) {
        ProfileResponse profile = userService.addSkill(authService.currentUserId(session), body);
        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @DeleteMapping("/skills/{userSkillId}")
    public ProfileResponse removeSkill(@PathVariable Long userSkillId, HttpSession session) {
        return userService.removeSkill(authService.currentUserId(session), userSkillId);
    }
}
