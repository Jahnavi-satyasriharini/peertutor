package com.campusskill.dto;

import com.campusskill.model.Role;
import com.campusskill.model.User;

public record UserResponse(
        Long id,
        String name,
        String email,
        String branch,
        Integer year,
        String bio,
        Role role
) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getBranch(),
                user.getYear(),
                user.getBio(),
                user.getRole()
        );
    }
}
