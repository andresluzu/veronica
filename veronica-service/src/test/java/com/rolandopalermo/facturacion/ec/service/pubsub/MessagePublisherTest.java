package com.rolandopalermo.facturacion.ec.service.pubsub;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.PubsubMessage;
import com.rolandopalermo.facturacion.ec.service.TestBaseSpring;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = MessagePublisher.class)
public class MessagePublisherTest extends TestBaseSpring {

    private static final String MESSAGE_ID = "21231232";

    @Autowired
    MessagePublisher messagePublisher;
    @MockBean
    Publisher publisher;
    @Mock
    ApiFuture<String> future;

    @Before
    public void setUp() throws Exception {
        when(future.get()).thenReturn(MESSAGE_ID);
        when(publisher.publish(any(PubsubMessage.class))).thenReturn(future);
    }

    @Test
    public void publishOne() throws ExecutionException, InterruptedException {
        assertNotNull(messagePublisher);
        PubsubMessage message = new MessageBuilder().build();
        assertNotNull(message);
        messagePublisher.publish(message);
        verify(future).get();
    }

    @Test
    public void publishTen() throws ExecutionException, InterruptedException {
        int threads = 10;
        ExecutorService service = Executors.newFixedThreadPool(threads);
        Collection<Future<?>> futures = new ArrayList<>(threads);
        PubsubMessage message = new MessageBuilder().build();
        for (int t = 0; t < threads; ++t) {
            futures.add(service.submit(() -> {
                try {
                    messagePublisher.publish(message);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }));
        }
        for (Future<?> f : futures) {
            f.get();
        }
        verify(future, times(threads)).get();
    }
}