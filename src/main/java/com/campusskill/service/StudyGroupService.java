package com.campusskill.service;

import com.campusskill.dto.GroupDetailResponse;
import com.campusskill.dto.GroupSummaryResponse;
import com.campusskill.dto.PersonSummary;
import com.campusskill.dto.StudyGroupRequest;
import com.campusskill.model.GroupMember;
import com.campusskill.model.StudyGroup;
import com.campusskill.model.StudyMode;
import com.campusskill.model.User;
import com.campusskill.repository.GroupMemberRepository;
import com.campusskill.repository.StudyGroupRepository;
import com.campusskill.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    public StudyGroupService(StudyGroupRepository studyGroupRepository,
                             GroupMemberRepository groupMemberRepository,
                             UserRepository userRepository) {
        this.studyGroupRepository = studyGroupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<GroupSummaryResponse> listGroups(Long userId, String query, StudyMode mode) {
        String search = TextUtils.blankToNull(query);
        String lowerSearch = search == null ? null : search.toLowerCase(Locale.ROOT);
        Set<Long> joinedGroupIds = groupMemberRepository.findByUserId(userId).stream()
                .map(membership -> membership.getGroup().getId())
                .collect(Collectors.toSet());

        return studyGroupRepository.findAllByOrderByNameAsc().stream()
                .filter(group -> mode == null || group.getMode() == mode)
                .filter(group -> lowerSearch == null || matchesSearch(group, lowerSearch))
                .map(group -> toSummary(group, userId, joinedGroupIds.contains(group.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public GroupDetailResponse getDetail(Long userId, Long groupId) {
        StudyGroup group = findGroup(groupId);
        boolean joined = groupMemberRepository.existsByGroupIdAndUserId(groupId, userId);
        List<PersonSummary> members = groupMemberRepository.findByGroupIdOrderByIdAsc(groupId).stream()
                .map(membership -> PersonSummary.from(membership.getUser()))
                .toList();
        return new GroupDetailResponse(toSummary(group, userId, joined), members);
    }

    @Transactional
    public GroupSummaryResponse create(Long userId, StudyGroupRequest request) {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not found."));
        StudyGroup group = studyGroupRepository.save(new StudyGroup(
                request.name().trim(),
                request.subject().trim(),
                TextUtils.blankToNull(request.description()),
                request.maxMembers(),
                request.mode(),
                creator
        ));
        groupMemberRepository.save(new GroupMember(group, creator));
        return toSummary(group, userId, true);
    }

    @Transactional
    public GroupDetailResponse join(Long userId, Long groupId) {
        StudyGroup group = findGroup(groupId);
        if (groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new ApiException(HttpStatus.CONFLICT, "You are already a member of this group.");
        }
        if (groupMemberRepository.countByGroupId(groupId) >= group.getMaxMembers()) {
            throw new ApiException(HttpStatus.CONFLICT, "This group is full.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not found."));
        groupMemberRepository.save(new GroupMember(group, user));
        return getDetail(userId, groupId);
    }

    @Transactional
    public GroupDetailResponse leave(Long userId, Long groupId) {
        StudyGroup group = findGroup(groupId);
        if (group.getCreator().getId().equals(userId)) {
            throw new ApiException(HttpStatus.CONFLICT, "As the creator, you cannot leave this group.");
        }
        GroupMember membership = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "You are not a member of this group."));
        groupMemberRepository.delete(membership);
        return getDetail(userId, groupId);
    }

    private StudyGroup findGroup(Long groupId) {
        return studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Study group not found."));
    }

    private GroupSummaryResponse toSummary(StudyGroup group, Long userId, boolean joined) {
        long memberCount = groupMemberRepository.countByGroupId(group.getId());
        return new GroupSummaryResponse(
                group.getId(),
                group.getName(),
                group.getSubject(),
                group.getDescription(),
                group.getMaxMembers(),
                memberCount,
                group.getMode(),
                PersonSummary.from(group.getCreator()),
                joined,
                group.getCreator().getId().equals(userId),
                memberCount >= group.getMaxMembers()
        );
    }

    private static boolean matchesSearch(StudyGroup group, String lowerSearch) {
        return group.getName().toLowerCase(Locale.ROOT).contains(lowerSearch)
                || group.getSubject().toLowerCase(Locale.ROOT).contains(lowerSearch);
    }
}
