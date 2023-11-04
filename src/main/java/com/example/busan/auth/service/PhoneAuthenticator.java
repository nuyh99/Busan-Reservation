package com.example.busan.auth.service;

public interface PhoneAuthenticator {
    void authenticate(String phone);

    void isAuthenticated(String phone);
}
