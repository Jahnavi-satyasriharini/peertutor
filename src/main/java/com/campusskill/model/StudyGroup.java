package com.campusskill.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "study_groups")
public class StudyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String subject;

    @Column(length = 1000)
    private String description;

    @Column(name = "max_members", nullable = false)
    private Integer maxMembers;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private StudyMode mode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    protected StudyGroup() {
    }

    public StudyGroup(String name, String subject, String description, Integer maxMembers, StudyMode mode, User creator) {
        this.name = name;
        this.subject = subject;
        this.description = description;
        this.maxMembers = maxMembers;
        this.mode = mode;
        this.creator = creator;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public Integer getMaxMembers() {
        return maxMembers;
    }

    public StudyMode getMode() {
        return mode;
    }

    public User getCreator() {
        return creator;
    }
}
