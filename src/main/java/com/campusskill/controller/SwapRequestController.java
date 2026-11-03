package com.campusskill.controller;

import com.campusskill.dto.CreateSwapRequest;
import com.campusskill.dto.RatingRequest;
import com.campusskill.dto.SwapRequestResponse;
import com.campusskill.dto.UpdateStatusRequest;
import com.campusskill.service.AuthService;
import com.campusskill.service.SwapRequestService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class SwapRequestController {

    private final AuthService authService;
    private final SwapRequestService swapRequestService;

    public SwapRequestController(AuthService authService, SwapRequestService swapRequestService) {
        this.authService = authService;
        this.swapRequestService = swapRequestService;
    }

    @PostMapping
    public ResponseEntity<SwapRequestResponse> create(@Valid @RequestBody CreateSwapRequest body, HttpSession session) {
        SwapRequestResponse created = swapRequestService.create(authService.currentUserId(session), body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/incoming")
    public List<SwapRequestResponse> incoming(HttpSession session) {
        return swapRequestService.listIncoming(authService.currentUserId(session));
    }

    @GetMapping("/outgoing")
    public List<SwapRequestResponse> outgoing(HttpSession session) {
        return swapRequestService.listOutgoing(authService.currentUserId(session));
    }

    @PostMapping("/{id}/status")
    public SwapRequestResponse updateStatus(@PathVariable Long id,
                                            @Valid @RequestBody UpdateStatusRequest body,
                                            HttpSession session) {
        return swapRequestService.updateStatus(authService.currentUserId(session), id, body.status());
    }

    @PostMapping("/{id}/rating")
    public SwapRequestResponse rate(@PathVariable Long id,
                                    @Valid @RequestBody RatingRequest body,
                                    HttpSession session) {
        return swapRequestService.rate(authService.currentUserId(session), id, body.rating());
    }
}
