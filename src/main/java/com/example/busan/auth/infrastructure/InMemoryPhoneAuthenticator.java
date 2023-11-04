package com.example.busan.auth.infrastructure;

import com.example.busan.auth.service.PhoneAuthenticator;
import org.springframework.stereotype.Component;

@Component
public class InMemoryPhoneAuthenticator implements PhoneAuthenticator {
    @Override
    public void authenticate(final String phone) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void isAuthenticated(final String phone) {
        throw new UnsupportedOperationException();
    }
}
