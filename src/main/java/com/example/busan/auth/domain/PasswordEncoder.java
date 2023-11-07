package com.example.busan.auth.domain;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {

    public String encode(final String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public void validateEquals(final String password, final String encoded) {
        final boolean equals = BCrypt.checkpw(password, encoded);
        if (!equals) {
            throw new IllegalArgumentException("잘못된 계정 정보입니다.");
        }
    }
}
