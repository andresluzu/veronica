package com.rolandopalermo.facturacion.ec.service.pubsub;

import com.google.pubsub.v1.PubsubMessage;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class MessageBuilderTest {

    MessageBuilder builder = new MessageBuilder();

    @Test
    public void build() {
        PubsubMessage message = builder.build();
        assertNotNull(message);
    }
}