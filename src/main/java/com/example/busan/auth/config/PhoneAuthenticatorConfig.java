package com.example.busan.auth.config;

import com.example.busan.auth.domain.AuthorizationCodeGenerator;
import com.example.busan.auth.infrastructure.InMemoryPhoneAuthenticator;
import com.example.busan.auth.service.PhoneAuthenticator;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class PhoneAuthenticatorConfig {

    private final AuthorizationCodeGenerator codeGenerator;
    private final String key;
    private final String secret;
    private final String sender;

    public PhoneAuthenticatorConfig(final AuthorizationCodeGenerator codeGenerator,
                                    @Value("${phone.key}") final String key,
                                    @Value("${phone.secret}") final String secret,
                                    @Value("${phone.sender}") final String sender) {
        this.codeGenerator = codeGenerator;
        this.key = key;
        this.secret = secret;
        this.sender = sender;
    }

    @Profile("prod")
    @Bean
    public PhoneAuthenticator phoneAuthenticator() {
        return new InMemoryPhoneAuthenticator(messageService(), codeGenerator, sender);
    }

    @Profile("default")
    @Bean
    public PhoneAuthenticator fakePhoneAuthenticator() {
        return new PhoneAuthenticator() {
            @Override
            public void sendCode(final String phone) {
            }

            @Override
            public void authenticate(final String code) {
            }

            @Override
            public void validateAuthenticated(final String phone) {
            }
        };
    }

    @Bean
    public DefaultMessageService messageService() {
        return new DefaultMessageService(key, secret, "https://api.coolsms.co.kr");
    }
}
