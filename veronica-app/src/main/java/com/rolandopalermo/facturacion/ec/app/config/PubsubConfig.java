package com.rolandopalermo.facturacion.ec.app.config;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectTopicName;
import com.rolandopalermo.facturacion.ec.service.pubsub.DomainConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.io.IOException;

@Configuration
public class PubsubConfig {

    @Value("${pubsub.project}")
    private String projectId;

    @Value("${pubsub.sub-created-topic}")
    private String subCreatedTopicName;

    @Value("${pubsub.pub-processed-topic}")
    private String pubProcessedTopicName;

    @Bean
    public DomainConfiguration getConfiguration() {
        return new DomainConfiguration(projectId, pubProcessedTopicName, subCreatedTopicName);
    }

    @Bean
    public Publisher getPublisher() {
        ProjectTopicName topicName = ProjectTopicName.of(projectId, pubProcessedTopicName);
        try {
            return Publisher.newBuilder(topicName).build();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot initialize Pub/Sub publisher", e);
        }
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(){
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolPubsubScheduler");
        return threadPoolTaskScheduler;
    }
}
