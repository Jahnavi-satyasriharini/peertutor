package com.campusskill.service;

public final class TextUtils {

    private TextUtils() {
    }

    public static String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
