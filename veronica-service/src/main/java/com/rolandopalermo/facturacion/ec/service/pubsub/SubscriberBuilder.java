package com.rolandopalermo.facturacion.ec.service.pubsub;

import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriberBuilder {

    @Autowired
    private DomainConfiguration configuration;

    @Autowired
    private DocumentMessageReceiver messageReceiver;

    public Subscriber getSubscriber() {
        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(
                configuration.getProjectId(), configuration.getSubCreatedTopicName()
        );
        return Subscriber.newBuilder(subscriptionName, messageReceiver).build();
    }
}
