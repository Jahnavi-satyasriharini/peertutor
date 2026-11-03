package com.campusskill.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "group_members", uniqueConstraints = @UniqueConstraint(
        name = "uk_group_member", columnNames = {"group_id", "user_id"}))
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private StudyGroup group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    protected GroupMember() {
    }

    public GroupMember(StudyGroup group, User user) {
        this.group = group;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public StudyGroup getGroup() {
        return group;
    }

    public User getUser() {
        return user;
    }
}
