package com.rolandopalermo.facturacion.ec.service;

import com.rolandopalermo.facturacion.ec.service.pubsub.DomainConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class UnitTestConfig {
    private static final String PROJECT_ID = "lab-web-13872";
    private static final String TOPIC_NAME = "demo.topic";

    @Bean
    public DomainConfiguration getConfiguration() {
        return new DomainConfiguration(PROJECT_ID, TOPIC_NAME, TOPIC_NAME);
    }

    /*
    @Bean
    public Publisher getPublisher() {
        ProjectTopicName topicName = ProjectTopicName.of(PROJECT_ID, TOPIC_NAME);
        try {
            return Publisher.newBuilder(topicName).build();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot initialize publisher", e);
        }
    }*/
}
