package com.example.busan.auth.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizationCodeGeneratorTest {

    private final AuthorizationCodeGenerator generator = new AuthorizationCodeGenerator();

    @Test
    @DisplayName("6자리 랜덤한 수를 생성할 수 있다")
    void generate() {
        final String generated1 = generator.generate();
        final String generated2 = generator.generate();
        final String generated3 = generator.generate();
        final String generated4 = generator.generate();
        final String generated5 = generator.generate();

        assertThat(List.of(generated1, generated2, generated3, generated4, generated5))
                .doesNotHaveDuplicates();
    }
}
