package com.campusskill.dto;

import java.util.List;

public record GroupDetailResponse(
        GroupSummaryResponse group,
        List<PersonSummary> members
) {
}
