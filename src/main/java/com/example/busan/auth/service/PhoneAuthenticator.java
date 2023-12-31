package com.example.busan.auth.service;

public interface PhoneAuthenticator {
    void sendCode(String phone);

    void authenticate(String code);

    void validateAuthenticated(String phone);
}
