package com.campusskill.service;

import com.campusskill.dto.AddUserSkillRequest;
import com.campusskill.dto.ProfileResponse;
import com.campusskill.dto.UpdateProfileRequest;
import com.campusskill.dto.UserSkillResponse;
import com.campusskill.model.Skill;
import com.campusskill.model.SkillType;
import com.campusskill.model.User;
import com.campusskill.model.UserSkill;
import com.campusskill.repository.SkillRepository;
import com.campusskill.repository.SwapRequestRepository;
import com.campusskill.repository.UserRepository;
import com.campusskill.repository.UserSkillRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;
    private final SwapRequestRepository swapRequestRepository;

    public UserService(UserRepository userRepository,
                       SkillRepository skillRepository,
                       UserSkillRepository userSkillRepository,
                       SwapRequestRepository swapRequestRepository) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.userSkillRepository = userSkillRepository;
        this.swapRequestRepository = swapRequestRepository;
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long userId) {
        return buildProfile(findUser(userId));
    }

    @Transactional
    public ProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = findUser(userId);
        user.setName(request.name().trim());
        user.setBranch(request.branch().trim());
        user.setYear(request.year());
        user.setBio(TextUtils.blankToNull(request.bio()));
        return buildProfile(user);
    }

    @Transactional
    public ProfileResponse addSkill(Long userId, AddUserSkillRequest request) {
        User user = findUser(userId);
        Skill skill = skillRepository.findById(request.skillId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "That skill does not exist."));
        if (userSkillRepository.existsByUserIdAndSkillIdAndType(userId, skill.getId(), request.type())) {
            String list = request.type() == SkillType.TEACH ? "teaching" : "learning";
            throw new ApiException(HttpStatus.CONFLICT, "This skill is already in your " + list + " list.");
        }
        userSkillRepository.save(new UserSkill(user, skill, request.type()));
        return buildProfile(user);
    }

    @Transactional
    public ProfileResponse removeSkill(Long userId, Long userSkillId) {
        UserSkill userSkill = userSkillRepository.findByIdAndUserId(userSkillId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Skill entry not found."));
        userSkillRepository.delete(userSkill);
        return buildProfile(findUser(userId));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not found."));
    }

    private ProfileResponse buildProfile(User user) {
        List<UserSkill> skills = userSkillRepository.findByUserIdWithSkill(user.getId());
        List<UserSkillResponse> teach = skills.stream()
                .filter(us -> us.getType() == SkillType.TEACH)
                .map(UserSkillResponse::from)
                .toList();
        List<UserSkillResponse> learn = skills.stream()
                .filter(us -> us.getType() == SkillType.LEARN)
                .map(UserSkillResponse::from)
                .toList();
        Double averageRating = swapRequestRepository.averageRatingForUser(user.getId());
        long ratingCount = swapRequestRepository.countRatingsForUser(user.getId());
        return new ProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getBranch(),
                user.getYear(),
                user.getBio(),
                user.getRole(),
                teach,
                learn,
                averageRating,
                ratingCount
        );
    }
}
