package com.campusskill.repository;

import com.campusskill.model.RequestStatus;
import com.campusskill.model.SwapRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SwapRequestRepository extends JpaRepository<SwapRequest, Long> {

    @EntityGraph(attributePaths = {"requester", "receiver", "skill"})
    List<SwapRequest> findByReceiverIdOrderByIdDesc(Long receiverId);

    @EntityGraph(attributePaths = {"requester", "receiver", "skill"})
    List<SwapRequest> findByRequesterIdOrderByIdDesc(Long requesterId);

    @EntityGraph(attributePaths = {"requester", "receiver", "skill"})
    List<SwapRequest> findByRequesterIdOrReceiverIdOrderByIdDesc(Long requesterId, Long receiverId);

    boolean existsByRequesterIdAndReceiverIdAndSkillIdAndStatus(
            Long requesterId, Long receiverId, Long skillId, RequestStatus status);

    List<SwapRequest> findByRequesterIdOrReceiverId(Long requesterId, Long receiverId);

    @Query("select avg(s.rating) from SwapRequest s where s.receiver.id = :userId and s.rating is not null")
    Double averageRatingForUser(@Param("userId") Long userId);

    @Query("select count(s) from SwapRequest s where s.receiver.id = :userId and s.rating is not null")
    long countRatingsForUser(@Param("userId") Long userId);
}
