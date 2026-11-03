package com.campusskill.dto;

import com.campusskill.model.RequestStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(
        @NotNull RequestStatus status
) {
}
