package com.rolandopalermo.facturacion.ec.service.pubsub;

import com.google.cloud.pubsub.v1.Subscriber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Service
@EnableAsync
public class PubsubListener {

    private static final Logger LOGGER = LogManager.getLogger(PubsubListener.class);

    @Autowired
    private SubscriberBuilder subscriberBuilder;

    private Subscriber subscriber;

    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void subscription() {
        LOGGER.info("Creating Pub/Sub subscription");
        subscriber = subscriberBuilder.getSubscriber();
        subscriber.startAsync().awaitRunning();
        LOGGER.info("Pub/Sub subscriber created {}", subscriber.getSubscriptionNameString());
        subscriber.awaitTerminated();
    }

    @PreDestroy
    public void cleanSubscription() {
        LOGGER.info("Stopping Pub/Sub subscription");
        if (subscriber != null) {
            subscriber.stopAsync();
            LOGGER.info("Pub/Sub subscription stopped");
        }
    }

    public void setSubscriberBuilder(SubscriberBuilder subscriberBuilder) {
        this.subscriberBuilder = subscriberBuilder;
    }
}
