package com.campusskill.repository;

import com.campusskill.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findAllByOrderByCategoryAscNameAsc();
}
