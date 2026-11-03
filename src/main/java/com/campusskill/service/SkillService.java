package com.campusskill.service;

import com.campusskill.dto.SkillResponse;
import com.campusskill.repository.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Transactional(readOnly = true)
    public List<SkillResponse> listSkills() {
        return skillRepository.findAllByOrderByCategoryAscNameAsc().stream()
                .map(SkillResponse::from)
                .toList();
    }
}
