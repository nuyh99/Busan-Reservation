package com.example.busan.auth.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AutoLoginRepository extends JpaRepository<AutoLogin, String> {
}
