package com.campusskill.service;

import com.campusskill.dto.SkillResponse;
import com.campusskill.dto.TutorResponse;
import com.campusskill.model.SkillType;
import com.campusskill.model.User;
import com.campusskill.model.UserSkill;
import com.campusskill.repository.SwapRequestRepository;
import com.campusskill.repository.UserSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TutorService {

    private static final Comparator<TutorResponse> BEST_RATED_FIRST = Comparator
            .comparing(TutorResponse::averageRating, Comparator.nullsLast(Comparator.<Double>reverseOrder()))
            .thenComparing(TutorResponse::name);

    private final UserSkillRepository userSkillRepository;
    private final SwapRequestRepository swapRequestRepository;

    public TutorService(UserSkillRepository userSkillRepository, SwapRequestRepository swapRequestRepository) {
        this.userSkillRepository = userSkillRepository;
        this.swapRequestRepository = swapRequestRepository;
    }

    @Transactional(readOnly = true)
    public List<TutorResponse> findTutors(Long currentUserId, String skill, String branch, Integer year) {
        String skillQuery = lowerOrNull(skill);
        String branchQuery = lowerOrNull(branch);

        Map<Long, List<UserSkill>> skillsByTutor = userSkillRepository.findAllByTypeWithDetails(SkillType.TEACH)
                .stream()
                .collect(Collectors.groupingBy(us -> us.getUser().getId(), LinkedHashMap::new, Collectors.toList()));

        return skillsByTutor.values().stream()
                .filter(skills -> !skills.get(0).getUser().getId().equals(currentUserId))
                .filter(skills -> matchesSkill(skills, skillQuery))
                .filter(skills -> matchesBranch(skills.get(0).getUser(), branchQuery))
                .filter(skills -> year == null || Objects.equals(year, skills.get(0).getUser().getYear()))
                .map(this::toTutorResponse)
                .sorted(BEST_RATED_FIRST)
                .toList();
    }

    private TutorResponse toTutorResponse(List<UserSkill> teachingSkills) {
        User tutor = teachingSkills.get(0).getUser();
        List<SkillResponse> skills = teachingSkills.stream()
                .map(us -> SkillResponse.from(us.getSkill()))
                .toList();
        Double averageRating = swapRequestRepository.averageRatingForUser(tutor.getId());
        long ratingCount = swapRequestRepository.countRatingsForUser(tutor.getId());
        return new TutorResponse(
                tutor.getId(),
                tutor.getName(),
                tutor.getBranch(),
                tutor.getYear(),
                tutor.getBio(),
                skills,
                averageRating,
                ratingCount
        );
    }

    private static boolean matchesSkill(List<UserSkill> teachingSkills, String skillQuery) {
        if (skillQuery == null) {
            return true;
        }
        return teachingSkills.stream()
                .anyMatch(us -> us.getSkill().getName().toLowerCase(Locale.ROOT).contains(skillQuery));
    }

    private static boolean matchesBranch(User tutor, String branchQuery) {
        return branchQuery == null || tutor.getBranch().toLowerCase(Locale.ROOT).contains(branchQuery);
    }

    private static String lowerOrNull(String value) {
        String cleaned = TextUtils.blankToNull(value);
        return cleaned == null ? null : cleaned.toLowerCase(Locale.ROOT);
    }
}
