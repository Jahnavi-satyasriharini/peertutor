package com.campusskill.dto;

import java.util.List;

public record TutorResponse(
        Long id,
        String name,
        String branch,
        Integer year,
        String bio,
        List<SkillResponse> teachSkills,
        Double averageRating,
        long ratingCount
) {
}
