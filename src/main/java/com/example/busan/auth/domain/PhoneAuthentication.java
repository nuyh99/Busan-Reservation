package com.example.busan.auth.domain;

import java.time.LocalDateTime;

public record PhoneAuthentication(String phone, LocalDateTime expired) {

    private static final int DURATION_MINUTES = 5;

    public static PhoneAuthentication from(final String phone) {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime expired = now.plusMinutes(DURATION_MINUTES);
        return new PhoneAuthentication(phone, expired);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expired);
    }
}
