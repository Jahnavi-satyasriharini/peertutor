package com.campusskill.controller;

import com.campusskill.dto.GroupDetailResponse;
import com.campusskill.dto.GroupSummaryResponse;
import com.campusskill.dto.StudyGroupRequest;
import com.campusskill.model.StudyMode;
import com.campusskill.service.AuthService;
import com.campusskill.service.StudyGroupService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class StudyGroupController {

    private final AuthService authService;
    private final StudyGroupService studyGroupService;

    public StudyGroupController(AuthService authService, StudyGroupService studyGroupService) {
        this.authService = authService;
        this.studyGroupService = studyGroupService;
    }

    @GetMapping
    public List<GroupSummaryResponse> listGroups(@RequestParam(required = false) String q,
                                                 @RequestParam(required = false) StudyMode mode,
                                                 HttpSession session) {
        return studyGroupService.listGroups(authService.currentUserId(session), q, mode);
    }

    @PostMapping
    public ResponseEntity<GroupSummaryResponse> create(@Valid @RequestBody StudyGroupRequest body, HttpSession session) {
        GroupSummaryResponse created = studyGroupService.create(authService.currentUserId(session), body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public GroupDetailResponse getGroup(@PathVariable Long id, HttpSession session) {
        return studyGroupService.getDetail(authService.currentUserId(session), id);
    }

    @PostMapping("/{id}/join")
    public GroupDetailResponse join(@PathVariable Long id, HttpSession session) {
        return studyGroupService.join(authService.currentUserId(session), id);
    }

    @PostMapping("/{id}/leave")
    public GroupDetailResponse leave(@PathVariable Long id, HttpSession session) {
        return studyGroupService.leave(authService.currentUserId(session), id);
    }
}
