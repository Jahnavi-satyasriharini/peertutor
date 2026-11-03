package com.campusskill.dto;

import com.campusskill.model.SkillType;
import jakarta.validation.constraints.NotNull;

public record AddUserSkillRequest(
        @NotNull Long skillId,
        @NotNull SkillType type
) {
}
