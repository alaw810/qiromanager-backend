package com.qiromanager.qiromanager_backend.api.mappers;

import com.qiromanager.qiromanager_backend.api.patients.TherapistSummary;
import com.qiromanager.qiromanager_backend.domain.user.User;

public class TherapistMapper {

    public static TherapistSummary toSummary(User user) {
        if (user == null) return null;

        return TherapistSummary.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .build();
    }
}
