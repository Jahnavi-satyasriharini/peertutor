package com.campusskill.dto;

import com.campusskill.model.RequestStatus;

import java.time.LocalDate;
import java.util.List;

public record DashboardResponse(
        String name,
        List<UserSkillResponse> teachSkills,
        List<UserSkillResponse> learnSkills,
        long pendingRequests,
        long completedExchanges,
        long activeGroups,
        RequestStats requestStats,
        List<ActivityItem> recentActivity
) {

    public record RequestStats(long pending, long accepted, long rejected, long completed) {
    }

    public record ActivityItem(Long requestId, String message, RequestStatus status, LocalDate date) {
    }
}
