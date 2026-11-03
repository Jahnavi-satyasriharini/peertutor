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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "user_skills", uniqueConstraints = @UniqueConstraint(
        name = "uk_user_skill_type", columnNames = {"user_id", "skill_id", "type"}))
public class UserSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SkillType type;

    protected UserSkill() {
    }

    public UserSkill(User user, Skill skill, SkillType type) {
        this.user = user;
        this.skill = skill;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Skill getSkill() {
        return skill;
    }

    public SkillType getType() {
        return type;
    }
}
