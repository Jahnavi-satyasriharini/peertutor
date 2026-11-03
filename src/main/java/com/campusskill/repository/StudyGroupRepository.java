package com.campusskill.repository;

import com.campusskill.model.StudyGroup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {

    @EntityGraph(attributePaths = {"creator"})
    List<StudyGroup> findAllByOrderByNameAsc();

    @EntityGraph(attributePaths = {"creator"})
    List<StudyGroup> findByCreatorId(Long creatorId);
}
