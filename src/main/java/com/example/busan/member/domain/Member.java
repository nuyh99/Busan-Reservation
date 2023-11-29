package com.example.busan.member.domain;

import com.example.busan.auth.domain.PasswordEncoder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

import static io.micrometer.common.util.StringUtils.isBlank;
import static java.lang.String.format;
import static org.springframework.util.StringUtils.containsWhitespace;

@EntityListeners(AuditingEntityListener.class)
@Entity
public class Member {

    public static final int PASSWORD_MINIMUM_LENGTH = 8;
    public static final int PASSWORD_MAXIMUM_LENGTH = 15;
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final String PASSWORD_REGEX =
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$";

    @Id
    private String email;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Region region;
    @Column(nullable = false)
    private String company;
    @Column(nullable = false)
    private String phone;
    @Enumerated(EnumType.STRING)
    private Role role;
    @CreatedDate
    private LocalDateTime createdAt;

    protected Member() {
    }

    public Member(final String email,
                  final String name,
                  final String password,
                  final Region region,
                  final String phone,
                  final String company,
                  final PasswordEncoder passwordEncoder) {
        validate(email, name, password, phone, region, company);
        this.email = email;
        this.password = passwordEncoder.encode(password);
        this.region = region;
        this.company = company;
        this.role = Role.USER;
        this.phone = phone;
        this.name = name;
    }

    private void validate(final String email,
                          final String name,
                          final String password,
                          final String phone,
                          final Region region,
                          final String company) {
        validateEmail(email);
        validateName(name);
        validateCompany(company);
        validatePassword(password);
        validatePhone(phone);
        validateRegion(region);
    }

    private void validateRegion(final Region region) {
        Assert.notNull(region, "지역이 필요합니다.");
    }

    private void validatePassword(final String password) {
        if (isBlank(password) || containsWhitespace(password) || !password.matches(PASSWORD_REGEX) ||
                password.length() < PASSWORD_MINIMUM_LENGTH ||
                password.length() > PASSWORD_MAXIMUM_LENGTH) {
            throw new IllegalArgumentException(
                    format("비밀번호는 특수문자 1개 이상 포함하고 영문, 숫자 조합의 %d ~ %d 글자입니다.",
                            PASSWORD_MINIMUM_LENGTH,
                            PASSWORD_MAXIMUM_LENGTH));
        }
    }

    private void validatePhone(final String phone) {
        Assert.notNull(phone, "휴대폰 번호가 필요합니다.");
    }

    private void validateEmail(final String email) {
        if (isBlank(email) || containsWhitespace(email) || !email.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("이메일 형식이 필요합니다.");
        }
    }

    private void validateName(final String name) {
        Assert.notNull(name, "이름이 필요합니다.");
    }

    private void validateCompany(final String company) {
        Assert.notNull(company, "회사가 필요합니다.");
    }

    public void checkPassword(final String password, final PasswordEncoder passwordEncoder) {
        passwordEncoder.validateEquals(password, this.password);
    }

    public void updateProfile(final String company, final String name, final Region region) {
        validateCompany(company);
        validateName(name);
        validateRegion(region);
        this.company = company;
        this.name = name;
        this.region = region;
    }

    public void updatePhone(final String phone) {
        validatePhone(phone);
        this.phone = phone;
    }

    public void updatePassword(final String password, final PasswordEncoder passwordEncoder) {
        validatePassword(password);
        this.password = passwordEncoder.encode(password);
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Region getRegion() {
        return region;
    }

    public String getCompany() {
        return company;
    }

    public Role getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getPhone() {
        return phone;
    }
}
