package com.campusskill.dto;

import com.campusskill.model.RequestStatus;

import java.time.LocalDate;

public record SwapRequestResponse(
        Long id,
        PersonSummary requester,
        PersonSummary receiver,
        SkillResponse skill,
        RequestStatus status,
        LocalDate date,
        Integer rating
) {
}
