package com.campusskill.repository;

import com.campusskill.model.GroupMember;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    @EntityGraph(attributePaths = {"user"})
    List<GroupMember> findByGroupIdOrderByIdAsc(Long groupId);

    List<GroupMember> findByUserId(Long userId);

    boolean existsByGroupIdAndUserId(Long groupId, Long userId);

    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);

    long countByGroupId(Long groupId);

    long countByUserId(Long userId);

    void deleteByGroupId(Long groupId);

    void deleteByUserId(Long userId);
}
