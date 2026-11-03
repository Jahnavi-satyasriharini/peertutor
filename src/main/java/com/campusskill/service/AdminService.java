package com.campusskill.service;

import com.campusskill.dto.AdminStatsResponse;
import com.campusskill.dto.UserResponse;
import com.campusskill.model.Role;
import com.campusskill.model.StudyGroup;
import com.campusskill.model.User;
import com.campusskill.repository.GroupMemberRepository;
import com.campusskill.repository.StudyGroupRepository;
import com.campusskill.repository.SwapRequestRepository;
import com.campusskill.repository.UserRepository;
import com.campusskill.repository.UserSkillRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final SwapRequestRepository swapRequestRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserSkillRepository userSkillRepository;

    public AdminService(UserRepository userRepository,
                        SwapRequestRepository swapRequestRepository,
                        StudyGroupRepository studyGroupRepository,
                        GroupMemberRepository groupMemberRepository,
                        UserSkillRepository userSkillRepository) {
        this.userRepository = userRepository;
        this.swapRequestRepository = swapRequestRepository;
        this.studyGroupRepository = studyGroupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userSkillRepository = userSkillRepository;
    }

    @Transactional(readOnly = true)
    public AdminStatsResponse getStats(Long adminId) {
        requireAdmin(adminId);
        return new AdminStatsResponse(
                userRepository.count(),
                studyGroupRepository.count(),
                swapRequestRepository.count()
        );
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listUsers(Long adminId, String query) {
        requireAdmin(adminId);
        String search = TextUtils.blankToNull(query);
        List<User> users = search == null
                ? userRepository.findAllByOrderByNameAsc()
                : userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrderByNameAsc(search, search);
        return users.stream().map(UserResponse::from).toList();
    }

    @Transactional
    public void deleteUser(Long adminId, Long targetId) {
        requireAdmin(adminId);
        if (adminId.equals(targetId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "You cannot delete your own admin account.");
        }
        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found."));

        List<StudyGroup> createdGroups = studyGroupRepository.findByCreatorId(targetId);
        for (StudyGroup group : createdGroups) {
            groupMemberRepository.deleteByGroupId(group.getId());
        }
        studyGroupRepository.deleteAll(createdGroups);

        groupMemberRepository.deleteByUserId(targetId);
        swapRequestRepository.deleteAll(swapRequestRepository.findByRequesterIdOrReceiverId(targetId, targetId));
        userSkillRepository.deleteByUserId(targetId);
        userRepository.delete(target);
    }

    private void requireAdmin(Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Please log in to continue."));
        if (admin.getRole() != Role.ADMIN) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Admin access is required.");
        }
    }
}
