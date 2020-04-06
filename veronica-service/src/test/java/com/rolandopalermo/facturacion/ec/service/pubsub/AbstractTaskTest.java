package com.rolandopalermo.facturacion.ec.service.pubsub;

import com.google.pubsub.v1.PubsubMessage;
import com.rolandopalermo.facturacion.ec.common.util.DocumentType;
import com.rolandopalermo.facturacion.ec.dto.ComprobanteIdDTO;
import com.rolandopalermo.facturacion.ec.dto.v1_0.ComprobanteDTO;
import com.rolandopalermo.facturacion.ec.dto.v1_0.sri.RespuestaComprobanteDTO;
import com.rolandopalermo.facturacion.ec.dto.v1_0.sri.RespuestaSolicitudDTO;
import com.rolandopalermo.facturacion.ec.persistence.entity.Document;
import com.rolandopalermo.facturacion.ec.persistence.repository.DocumentRepository;
import com.rolandopalermo.facturacion.ec.service.v1_0.impl.GenericSRIServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractTaskTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTaskTest.class);
    private static final String PHASE = "Test content";
    private static final String EXCEPTION_RESULT =
            "{\"phase\":\"AUTHORIZATION\",\"type\":\"FACTURA\",\"tin\":\"9999999999999\",\"number\":\"000-000-000000000\",\"state\":\"ERROR\",\"content\":\"Test exception\"}";

    @Mock
    GenericSRIServiceImpl sriService;
    @Mock
    DocumentRepository repository;
    @Mock
    ThreadPoolTaskScheduler taskScheduler;
    @Mock
    Document document;
    @Mock
    ComprobanteDTO dto;
    @Mock
    MessagePublisher publisher;

    AbstractTask task;

    @Before
    public void setUp() throws Exception {
        when(document.getType()).thenReturn(DocumentType.FACTURA.name());
        when(document.getPhase()).thenReturn(ProcessingPhase.AUTHORIZATION.name());
        when(document.getState()).thenReturn(ProcessingState.COMPLETE.name());

        this.task = new AbstractTask(sriService, repository, taskScheduler, document, dto, publisher) {
            @Override
            public void run() {
                LOGGER.info("AbstractTask executing mock run method");
            }
        };
    }

    @Test
    public void publishExceptionVerifyPolymorphism() throws ExecutionException, InterruptedException {
        Exception exception = new Exception("Test exception");
        PubsubMessage message = task.createMessage(exception);
        Supplier<PubsubMessage> messageSupplier = () -> message;
        task.publish(messageSupplier);
        verify(publisher).publish(message);
    }

    @Test
    public void publishStringVerifyPolymorphism() throws ExecutionException, InterruptedException {
        PubsubMessage message = task.createMessage("Some content");
        Supplier<PubsubMessage> messageSupplier = () -> message;
        task.publish(messageSupplier);
        verify(publisher).publish(message);
    }

    @Test
    public void publishComprobanteIdDTOVerifyPolymorphism() throws ExecutionException, InterruptedException {
        ComprobanteIdDTO dto = new ComprobanteIdDTO();
        dto.setClaveAcceso("some-access-key");
        PubsubMessage message = task.createMessage(dto);
        Supplier<PubsubMessage> messageSupplier = () -> message;
        task.publish(messageSupplier);
        verify(publisher).publish(message);
    }

    @Test
    public void publishRespuestaSolicitudDTOVerifyPolymorphism() throws ExecutionException, InterruptedException {
        RespuestaSolicitudDTO dto = new RespuestaSolicitudDTO();
        dto.setEstado("some-state");
        PubsubMessage message = task.createMessage(dto);
        Supplier<PubsubMessage> messageSupplier = () -> message;
        task.publish(messageSupplier);
        verify(publisher).publish(message);
    }

    @Test
    public void publishRespuestaComprobanteDTOVerifyPolymorphism() throws ExecutionException, InterruptedException {
        RespuestaComprobanteDTO dto = new RespuestaComprobanteDTO();
        dto.setClaveAccesoConsultada("some-access-key");
        PubsubMessage message = task.createMessage(dto);
        Supplier<PubsubMessage> messageSupplier = () -> message;
        task.publish(messageSupplier);
        verify(publisher).publish(message);
    }
}