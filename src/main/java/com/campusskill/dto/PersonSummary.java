package com.campusskill.dto;

import com.campusskill.model.User;

public record PersonSummary(
        Long id,
        String name,
        String branch,
        Integer year
) {

    public static PersonSummary from(User user) {
        return new PersonSummary(user.getId(), user.getName(), user.getBranch(), user.getYear());
    }
}
