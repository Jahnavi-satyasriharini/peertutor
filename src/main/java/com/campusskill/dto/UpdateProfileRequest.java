package com.campusskill.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 100) String branch,
        @NotNull @Min(1) @Max(5) Integer year,
        @Size(max = 500) String bio
) {
}
