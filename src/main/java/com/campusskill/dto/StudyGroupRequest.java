package com.campusskill.dto;

import com.campusskill.model.StudyMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StudyGroupRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 100) String subject,
        @Size(max = 1000) String description,
        @NotNull @Min(2) @Max(50) Integer maxMembers,
        @NotNull StudyMode mode
) {
}
