package com.campusskill.dto;

import com.campusskill.model.UserSkill;

public record UserSkillResponse(
        Long id,
        Long skillId,
        String name,
        String category
) {

    public static UserSkillResponse from(UserSkill userSkill) {
        return new UserSkillResponse(
                userSkill.getId(),
                userSkill.getSkill().getId(),
                userSkill.getSkill().getName(),
                userSkill.getSkill().getCategory()
        );
    }
}
