package com.rolandopalermo.facturacion.ec.service.pubsub;

import com.google.api.core.ApiService;
import com.google.cloud.pubsub.v1.Subscriber;
import com.rolandopalermo.facturacion.ec.service.TestBaseSpring;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PubsubListenerTest extends TestBaseSpring {

    @MockBean
    SubscriberBuilder subscriberBuilder;
    @Mock
    Subscriber subscriber;
    @Mock
    ApiService apiService;

    PubsubListener pubsubListener;

    @Before
    public void setUp() throws Exception {
        when(subscriberBuilder.getSubscriber()).thenReturn(subscriber);
        when(subscriber.startAsync()).thenReturn(apiService);

        pubsubListener = new PubsubListener();
        pubsubListener.setSubscriberBuilder(subscriberBuilder);
        assertNotNull(pubsubListener);
    }

    @Test
    public void subscription() {
        pubsubListener.subscription();
        verify(subscriberBuilder).getSubscriber();
        verify(subscriber).startAsync();
        verify(apiService).awaitRunning();
        verify(subscriber).awaitTerminated();
    }

    @Test
    public void clean() {
        pubsubListener.subscription();
        pubsubListener.cleanSubscription();
        verify(subscriber).stopAsync();
    }
}