package com.campusskill.repository;

import com.campusskill.model.SkillType;
import com.campusskill.model.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

    @Query("select us from UserSkill us join fetch us.skill where us.user.id = :userId order by us.skill.name")
    List<UserSkill> findByUserIdWithSkill(@Param("userId") Long userId);

    @Query("select us from UserSkill us join fetch us.skill join fetch us.user where us.type = :type")
    List<UserSkill> findAllByTypeWithDetails(@Param("type") SkillType type);

    boolean existsByUserIdAndSkillIdAndType(Long userId, Long skillId, SkillType type);

    Optional<UserSkill> findByIdAndUserId(Long id, Long userId);

    void deleteByUserId(Long userId);
}
