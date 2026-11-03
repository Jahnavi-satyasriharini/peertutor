package com.campusskill.dto;

import com.campusskill.model.Role;

import java.util.List;

public record ProfileResponse(
        Long id,
        String name,
        String email,
        String branch,
        Integer year,
        String bio,
        Role role,
        List<UserSkillResponse> teachSkills,
        List<UserSkillResponse> learnSkills,
        Double averageRating,
        long ratingCount
) {
}
