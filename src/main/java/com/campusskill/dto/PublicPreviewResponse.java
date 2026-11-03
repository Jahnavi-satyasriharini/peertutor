package com.campusskill.dto;

import com.campusskill.model.StudyMode;

import java.util.List;

public record PublicPreviewResponse(
        List<TutorPreview> tutors,
        List<GroupPreview> groups,
        List<SkillResponse> skills
) {

    public record TutorPreview(
            String name,
            String branch,
            Integer year,
            List<SkillResponse> teachSkills,
            Double averageRating,
            long ratingCount
    ) {
    }

    public record GroupPreview(
            Long id,
            String name,
            String subject,
            StudyMode mode,
            long memberCount,
            Integer maxMembers
    ) {
    }
}
