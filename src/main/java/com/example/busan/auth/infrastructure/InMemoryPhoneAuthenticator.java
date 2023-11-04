package com.example.busan.auth.infrastructure;

import com.example.busan.auth.domain.AuthorizationCodeGenerator;
import com.example.busan.auth.domain.PhoneAuthentication;
import com.example.busan.auth.service.PhoneAuthenticator;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryPhoneAuthenticator implements PhoneAuthenticator {

    private static final Map<String, PhoneAuthentication> WAITING = new ConcurrentHashMap<>();
    private static final Map<String, LocalDateTime> AUTHENTICATED = new ConcurrentHashMap<>();
    private static final int DURATION_MINUTES = 5;

    private final DefaultMessageService messageService;
    private final AuthorizationCodeGenerator codeGenerator;
    private final String sender;

    public InMemoryPhoneAuthenticator(final DefaultMessageService messageService,
                                      final AuthorizationCodeGenerator codeGenerator,
                                      @Value("${phone.sender}") final String sender) {
        this.messageService = messageService;
        this.codeGenerator = codeGenerator;
        this.sender = sender;
    }

    @Override
    public void sendCode(final String phone) {
        final String code = sendAuthorizationCode(phone);
        WAITING.put(code, PhoneAuthentication.from(phone));
    }

    @Override
    public void authenticate(final String code) {
        if (!WAITING.containsKey(code)) {
            throw new IllegalArgumentException("잘못된 인증 코드입니다.");
        }

        final PhoneAuthentication phoneAuthentication = WAITING.get(code);
        if (phoneAuthentication.isExpired()) {
            WAITING.remove(code);
            throw new IllegalArgumentException("만료된 인증 코드입니다.");
        }

        final LocalDateTime expired = LocalDateTime.now().plusMinutes(DURATION_MINUTES);
        AUTHENTICATED.put(phoneAuthentication.phone(), expired);
    }

    private String sendAuthorizationCode(final String phone) {
        final String authorizationCode = codeGenerator.generate();

        Message message = new Message();
        message.setFrom(sender);
        message.setTo(phone);
        message.setText("인증번호: " + authorizationCode);

        messageService.sendOne(new SingleMessageSendingRequest(message));
        return authorizationCode;
    }

    @Override
    public void validateAuthenticated(final String phone) {
        if (!AUTHENTICATED.containsKey(phone)) {
            throw new IllegalArgumentException("휴대폰 인증이 필요합니다.");
        }

        final LocalDateTime expired = AUTHENTICATED.get(phone);
        if (expired.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("시간이 만료되어 재인증이 필요합니다.");
        }
    }
}
