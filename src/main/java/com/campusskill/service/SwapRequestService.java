package com.campusskill.service;

import com.campusskill.dto.CreateSwapRequest;
import com.campusskill.dto.PersonSummary;
import com.campusskill.dto.SkillResponse;
import com.campusskill.dto.SwapRequestResponse;
import com.campusskill.model.RequestStatus;
import com.campusskill.model.Skill;
import com.campusskill.model.SkillType;
import com.campusskill.model.SwapRequest;
import com.campusskill.model.User;
import com.campusskill.repository.SkillRepository;
import com.campusskill.repository.SwapRequestRepository;
import com.campusskill.repository.UserRepository;
import com.campusskill.repository.UserSkillRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class SwapRequestService {

    private final SwapRequestRepository swapRequestRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;

    public SwapRequestService(SwapRequestRepository swapRequestRepository,
                              UserRepository userRepository,
                              SkillRepository skillRepository,
                              UserSkillRepository userSkillRepository) {
        this.swapRequestRepository = swapRequestRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.userSkillRepository = userSkillRepository;
    }

    @Transactional
    public SwapRequestResponse create(Long requesterId, CreateSwapRequest request) {
        if (requesterId.equals(request.receiverId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "You cannot send a request to yourself.");
        }
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not found."));
        User receiver = userRepository.findById(request.receiverId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "That student does not exist."));
        Skill skill = skillRepository.findById(request.skillId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "That skill does not exist."));

        if (!userSkillRepository.existsByUserIdAndSkillIdAndType(receiver.getId(), skill.getId(), SkillType.TEACH)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "This student does not teach that skill.");
        }
        if (swapRequestRepository.existsByRequesterIdAndReceiverIdAndSkillIdAndStatus(
                requesterId, receiver.getId(), skill.getId(), RequestStatus.PENDING)) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "You already have a pending request for this skill with this student.");
        }

        SwapRequest saved = swapRequestRepository.save(
                new SwapRequest(requester, receiver, skill, RequestStatus.PENDING, LocalDate.now()));
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<SwapRequestResponse> listIncoming(Long userId) {
        return swapRequestRepository.findByReceiverIdOrderByIdDesc(userId).stream()
                .map(SwapRequestService::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SwapRequestResponse> listOutgoing(Long userId) {
        return swapRequestRepository.findByRequesterIdOrderByIdDesc(userId).stream()
                .map(SwapRequestService::toResponse)
                .toList();
    }

    @Transactional
    public SwapRequestResponse updateStatus(Long userId, Long requestId, RequestStatus target) {
        SwapRequest request = findRequest(requestId);
        boolean isReceiver = request.getReceiver().getId().equals(userId);
        boolean isRequester = request.getRequester().getId().equals(userId);

        switch (target) {
            case ACCEPTED, REJECTED -> {
                if (!isReceiver) {
                    throw new ApiException(HttpStatus.FORBIDDEN, "Only the receiver can accept or reject this request.");
                }
                requireStatus(request, RequestStatus.PENDING, "Only pending requests can be accepted or rejected.");
            }
            case COMPLETED -> {
                if (!isReceiver && !isRequester) {
                    throw new ApiException(HttpStatus.FORBIDDEN,
                            "Only the people in this exchange can mark it as completed.");
                }
                requireStatus(request, RequestStatus.ACCEPTED, "Only accepted exchanges can be marked as completed.");
            }
            default -> throw new ApiException(HttpStatus.BAD_REQUEST,
                    "A request can only be accepted, rejected or completed.");
        }

        request.setStatus(target);
        return toResponse(request);
    }

    @Transactional
    public SwapRequestResponse rate(Long userId, Long requestId, int rating) {
        SwapRequest request = findRequest(requestId);
        if (!request.getRequester().getId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Only the student who made this request can rate it.");
        }
        if (request.getStatus() != RequestStatus.COMPLETED) {
            throw new ApiException(HttpStatus.CONFLICT, "Only completed exchanges can be rated.");
        }
        if (request.getRating() != null) {
            throw new ApiException(HttpStatus.CONFLICT, "This exchange has already been rated.");
        }
        request.setRating(rating);
        return toResponse(request);
    }

    private SwapRequest findRequest(Long requestId) {
        return swapRequestRepository.findById(requestId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Request not found."));
    }

    private static void requireStatus(SwapRequest request, RequestStatus expected, String message) {
        if (request.getStatus() != expected) {
            throw new ApiException(HttpStatus.CONFLICT, message);
        }
    }

    private static SwapRequestResponse toResponse(SwapRequest request) {
        return new SwapRequestResponse(
                request.getId(),
                PersonSummary.from(request.getRequester()),
                PersonSummary.from(request.getReceiver()),
                SkillResponse.from(request.getSkill()),
                request.getStatus(),
                request.getDate(),
                request.getRating()
        );
    }
}
