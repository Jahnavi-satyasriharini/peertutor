package com.campusskill.service;

import com.campusskill.dto.DashboardResponse;
import com.campusskill.dto.UserSkillResponse;
import com.campusskill.model.RequestStatus;
import com.campusskill.model.SkillType;
import com.campusskill.model.SwapRequest;
import com.campusskill.model.User;
import com.campusskill.model.UserSkill;
import com.campusskill.repository.GroupMemberRepository;
import com.campusskill.repository.SwapRequestRepository;
import com.campusskill.repository.UserRepository;
import com.campusskill.repository.UserSkillRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DashboardService {

    private static final int RECENT_ACTIVITY_LIMIT = 5;

    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;
    private final SwapRequestRepository swapRequestRepository;
    private final GroupMemberRepository groupMemberRepository;

    public DashboardService(UserRepository userRepository,
                            UserSkillRepository userSkillRepository,
                            SwapRequestRepository swapRequestRepository,
                            GroupMemberRepository groupMemberRepository) {
        this.userRepository = userRepository;
        this.userSkillRepository = userSkillRepository;
        this.swapRequestRepository = swapRequestRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not found."));

        List<UserSkill> skills = userSkillRepository.findByUserIdWithSkill(userId);
        List<UserSkillResponse> teach = skills.stream()
                .filter(us -> us.getType() == SkillType.TEACH)
                .map(UserSkillResponse::from)
                .toList();
        List<UserSkillResponse> learn = skills.stream()
                .filter(us -> us.getType() == SkillType.LEARN)
                .map(UserSkillResponse::from)
                .toList();

        List<SwapRequest> requests = swapRequestRepository.findByRequesterIdOrReceiverIdOrderByIdDesc(userId, userId);
        long pending = countWithStatus(requests, RequestStatus.PENDING);
        long accepted = countWithStatus(requests, RequestStatus.ACCEPTED);
        long rejected = countWithStatus(requests, RequestStatus.REJECTED);
        long completed = countWithStatus(requests, RequestStatus.COMPLETED);

        List<DashboardResponse.ActivityItem> recentActivity = requests.stream()
                .limit(RECENT_ACTIVITY_LIMIT)
                .map(request -> toActivity(request, userId))
                .toList();

        return new DashboardResponse(
                user.getName(),
                teach,
                learn,
                pending,
                completed,
                groupMemberRepository.countByUserId(userId),
                new DashboardResponse.RequestStats(pending, accepted, rejected, completed),
                recentActivity
        );
    }

    private static long countWithStatus(List<SwapRequest> requests, RequestStatus status) {
        return requests.stream().filter(request -> request.getStatus() == status).count();
    }

    private static DashboardResponse.ActivityItem toActivity(SwapRequest request, Long userId) {
        String skillName = request.getSkill().getName();
        String message = request.getReceiver().getId().equals(userId)
                ? request.getRequester().getName() + " asked to learn " + skillName + " from you"
                : "You asked " + request.getReceiver().getName() + " to teach you " + skillName;
        return new DashboardResponse.ActivityItem(request.getId(), message, request.getStatus(), request.getDate());
    }
}
