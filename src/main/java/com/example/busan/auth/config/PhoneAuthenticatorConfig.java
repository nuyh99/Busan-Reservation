package com.example.busan.auth.config;

import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PhoneAuthenticatorConfig {

    private final String key;
    private final String secret;

    public PhoneAuthenticatorConfig(@Value("${phone.key}") final String key,
                                    @Value("${phone.secret}") final String secret) {
        this.key = key;
        this.secret = secret;
    }

    @Bean
    public DefaultMessageService messageService() {
        return new DefaultMessageService(key, secret, "https://api.coolsms.co.kr");
    }
}
