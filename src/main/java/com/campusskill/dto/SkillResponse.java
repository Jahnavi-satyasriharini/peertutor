package com.campusskill.dto;

import com.campusskill.model.Skill;

public record SkillResponse(
        Long id,
        String name,
        String category
) {

    public static SkillResponse from(Skill skill) {
        return new SkillResponse(skill.getId(), skill.getName(), skill.getCategory());
    }
}
