package com.rolandopalermo.facturacion.ec.service.pubsub;

import com.rolandopalermo.facturacion.ec.service.TestBaseSpring;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = SubscriberBuilder.class)
public class SubscriberBuilderTest extends TestBaseSpring {

    private static final String PROJECT_ID = "test-project";
    private static final String SUB_CREATED_TOPIC_NAME = "sub.topic";

    @MockBean
    DocumentMessageReceiver receiver;
    @MockBean
    DomainConfiguration configuration;

    @Autowired
    SubscriberBuilder subscriberBuilder;

    @Before
    public void setUp() throws Exception {
        when(configuration.getProjectId()).thenReturn(PROJECT_ID);
        when(configuration.getSubCreatedTopicName()).thenReturn(SUB_CREATED_TOPIC_NAME);

        assertNotNull(subscriberBuilder);
    }

    @Test
    public void getSubscriber() {
        assertNotNull(subscriberBuilder.getSubscriber());
    }
}