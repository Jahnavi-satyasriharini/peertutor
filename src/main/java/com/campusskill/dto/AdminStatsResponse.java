package com.campusskill.dto;

public record AdminStatsResponse(
        long totalUsers,
        long totalGroups,
        long totalSwapRequests
) {
}
