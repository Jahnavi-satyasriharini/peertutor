package com.campusskill.dto;

import jakarta.validation.constraints.NotNull;

public record CreateSwapRequest(
        @NotNull Long receiverId,
        @NotNull Long skillId
) {
}
