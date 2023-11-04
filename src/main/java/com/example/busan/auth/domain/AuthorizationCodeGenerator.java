package com.example.busan.auth.domain;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class AuthorizationCodeGenerator {

    private static final int CODE_LENGTH = 6;
    private static final int MINIMUM_NUMBER = 0;
    private static final int MAXIMUM_NUMBER = 10;

    private static final Random random = new SecureRandom();

    public String generate() {
        return random.ints(CODE_LENGTH, MINIMUM_NUMBER, MAXIMUM_NUMBER)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }
}
