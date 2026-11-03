package com.campusskill.service;

import com.campusskill.dto.PublicPreviewResponse;
import com.campusskill.dto.PublicPreviewResponse.GroupPreview;
import com.campusskill.dto.PublicPreviewResponse.TutorPreview;
import com.campusskill.repository.GroupMemberRepository;
import com.campusskill.repository.StudyGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PreviewService {

    private static final int PREVIEW_SIZE = 3;

    private final TutorService tutorService;
    private final SkillService skillService;
    private final StudyGroupRepository studyGroupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public PreviewService(TutorService tutorService,
                          SkillService skillService,
                          StudyGroupRepository studyGroupRepository,
                          GroupMemberRepository groupMemberRepository) {
        this.tutorService = tutorService;
        this.skillService = skillService;
        this.studyGroupRepository = studyGroupRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    @Transactional(readOnly = true)
    public PublicPreviewResponse getPreview() {
        List<TutorPreview> tutors = tutorService.findTutors(null, null, null, null).stream()
                .limit(PREVIEW_SIZE)
                .map(tutor -> new TutorPreview(
                        shortName(tutor.name()),
                        tutor.branch(),
                        tutor.year(),
                        tutor.teachSkills(),
                        tutor.averageRating(),
                        tutor.ratingCount()))
                .toList();

        List<GroupPreview> groups = studyGroupRepository.findAllByOrderByNameAsc().stream()
                .limit(PREVIEW_SIZE)
                .map(group -> new GroupPreview(
                        group.getId(),
                        group.getName(),
                        group.getSubject(),
                        group.getMode(),
                        groupMemberRepository.countByGroupId(group.getId()),
                        group.getMaxMembers()))
                .toList();

        return new PublicPreviewResponse(tutors, groups, skillService.listSkills());
    }

    private static String shortName(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0];
        }
        return parts[0] + " " + parts[parts.length - 1].charAt(0) + ".";
    }
}
