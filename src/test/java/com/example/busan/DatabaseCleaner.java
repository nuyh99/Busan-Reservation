package com.example.busan;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DatabaseCleaner implements InitializingBean {

    private static final String TRUNCATE = "TRUNCATE ";
    private static final String FLYWAY = "flyway";

    @PersistenceContext
    private EntityManager entityManager;
    private List<String> truncateDMLs;

    @Override
    public void afterPropertiesSet() {
        final List<String> tableNames = entityManager.createNativeQuery("SHOW TABLES ").getResultList();

        truncateDMLs = tableNames.stream()
                .filter(tableName -> !tableName.contains(FLYWAY))
                .map(TRUNCATE::concat)
                .toList();
    }

    @Transactional
    public void truncate() {
        entityManager.createNativeQuery("SET foreign_key_checks=0").executeUpdate();
        truncateDMLs.stream()
                .map(entityManager::createNativeQuery)
                .forEach(Query::executeUpdate);
        entityManager.createNativeQuery("SET foreign_key_checks=1").executeUpdate();
    }

}
