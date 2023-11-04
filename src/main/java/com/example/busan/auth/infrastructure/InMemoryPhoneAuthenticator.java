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
    public String getPhone(final String id) {
        throw new UnsupportedOperationException();
    }
}
