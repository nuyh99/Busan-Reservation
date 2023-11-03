package com.example.busan;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Component
public class DatabaseCleaner implements InitializingBean {

    @PersistenceContext
    private EntityManager entityManager;
    private List<String> truncateDMLs;

    @Override
    public void afterPropertiesSet() {
        final Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();

        truncateDMLs = entities.stream()
                .map(entity -> "TRUNCATE " + entity.getName().toLowerCase())
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
