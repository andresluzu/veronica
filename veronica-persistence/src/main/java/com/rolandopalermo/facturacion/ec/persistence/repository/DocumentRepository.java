package com.rolandopalermo.facturacion.ec.persistence.repository;

import com.rolandopalermo.facturacion.ec.persistence.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    Optional<Document> findByAccessKey(String accessKey);

    Collection<Document> findByPhaseInAndStateInAndRetriesLessThanEqual(String[] phases, String[] states, int maxRetries);
}