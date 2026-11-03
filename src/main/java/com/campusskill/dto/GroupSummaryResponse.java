package com.campusskill.dto;

import com.campusskill.model.StudyMode;

public record GroupSummaryResponse(
        Long id,
        String name,
        String subject,
        String description,
        Integer maxMembers,
        long memberCount,
        StudyMode mode,
        PersonSummary creator,
        boolean joined,
        boolean owner,
        boolean full
) {
}
