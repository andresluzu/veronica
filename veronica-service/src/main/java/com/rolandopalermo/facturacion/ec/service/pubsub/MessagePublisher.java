package com.rolandopalermo.facturacion.ec.service.pubsub;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class MessagePublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessagePublisher.class);

    @Autowired
    private Publisher publisher;

    public String publish(PubsubMessage message) throws ExecutionException, InterruptedException {
        // Once published, returns a server-assigned message id (unique within the topic)
        ApiFuture<String> future = publisher.publish(message);
        return future.get();
    }

    @PreDestroy
    public void finalize() {
        if (publisher != null) {
            // When finished with the publisher, shutdown to free up resources.
            publisher.shutdown();
            try {
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                LOGGER.error("Cannot shutdown Pub/Sub publisher", e);
            }
        }
    }
}
