package com.rolandopalermo.facturacion.ec.service.pubsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.pubsub.v1.PubsubMessage;
import com.rolandopalermo.facturacion.ec.common.util.DocumentType;
import com.rolandopalermo.facturacion.ec.dto.ComprobanteIdDTO;
import com.rolandopalermo.facturacion.ec.dto.v1_0.ComprobanteDTO;
import com.rolandopalermo.facturacion.ec.dto.v1_0.sri.RespuestaComprobanteDTO;
import com.rolandopalermo.facturacion.ec.dto.v1_0.sri.RespuestaSolicitudDTO;
import com.rolandopalermo.facturacion.ec.persistence.entity.Document;
import com.rolandopalermo.facturacion.ec.persistence.repository.DocumentRepository;
import com.rolandopalermo.facturacion.ec.service.v1_0.impl.GenericSRIServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public abstract class AbstractTask implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(AbstractTask.class);

    protected final GenericSRIServiceImpl sriService;
    protected final DocumentRepository repository;
    protected final ThreadPoolTaskScheduler taskScheduler;
    protected final Document document;
    protected final ComprobanteDTO dto;
    protected final MessagePublisher publisher;

    protected AbstractTask(GenericSRIServiceImpl sriService, DocumentRepository repository,
                           ThreadPoolTaskScheduler taskScheduler, Document document, ComprobanteDTO dto, MessagePublisher publisher) {
        this.sriService = requireNonNull(sriService, "sriService cannot be null");
        this.repository = requireNonNull(repository, "repository cannot be null");
        this.taskScheduler = requireNonNull(taskScheduler, "taskScheduler cannot be null");
        this.document = requireNonNull(document, "document cannot be null");
        this.dto = requireNonNull(dto, "dto cannot be null");
        this.publisher = requireNonNull(publisher, "publisher cannot be null");
    }

    protected AbstractTask(GenericSRIServiceImpl sriService, DocumentRepository repository,
                           ThreadPoolTaskScheduler taskScheduler, Document document, MessagePublisher publisher) {
        this.sriService = requireNonNull(sriService, "sriService cannot be null");
        this.repository = requireNonNull(repository, "repository cannot be null");
        this.taskScheduler = requireNonNull(taskScheduler, "taskScheduler cannot be null");
        this.document = requireNonNull(document, "document cannot be null");
        this.dto = null;
        this.publisher = requireNonNull(publisher, "publisher cannot be null");
    }

    public void publish(Supplier<PubsubMessage> messageSupplier) {
        PubsubMessage message = messageSupplier.get();
        try {
            String messageId = publisher.publish(message);
            document.setPublishedMessageId(messageId);
        } catch (InterruptedException e) {
            LOGGER.error("Pub/Sub publish was interrupted", e);
            throw new IllegalStateException(e);
        } catch (ExecutionException e) {
            LOGGER.error("Cannot send Pub/Sub message", e);
            throw new IllegalStateException(e);
        }
        repository.save(document);
    }

    protected PubsubMessage createMessage(Object obj) {
        try {
            return createBuilder().withContent(obj.toString()).build();
        } catch (JsonProcessingException e) {
            LOGGER.error("Cannot parse {}", obj.getClass().getSimpleName(), e);
            throw new RuntimeException(e);
        }
    }

    protected PubsubMessage createMessage(Exception exception) {
        try {
            return createBuilder().withException(exception).build();
        } catch (JsonProcessingException e) {
            LOGGER.error("Cannot parse Exception", e);
            throw new RuntimeException(e);
        }
    }

    protected PubsubMessage createMessage(ComprobanteIdDTO dto) {
        try {
            return createBuilder().withComprobanteId(dto).build();
        } catch (JsonProcessingException e) {
            LOGGER.error("Cannot parse ComprobanteIdDTO", e);
            throw new RuntimeException(e);
        }
    }

    protected PubsubMessage createMessage(RespuestaSolicitudDTO dto) {
        try {
            return createBuilder().withRespuestaSolicitud(dto).build();
        } catch (JsonProcessingException e) {
            LOGGER.error("Cannot parse RespuestaSolicitudDTO", e);
            throw new RuntimeException(e);
        }
    }

    protected PubsubMessage createMessage(RespuestaComprobanteDTO dto) {
        try {
            return createBuilder().withRespuestaComprobante(dto).build();
        } catch (JsonProcessingException e) {
            LOGGER.error("Cannot parse RespuestaComprobanteDTO", e);
            throw new RuntimeException(e);
        }
    }

    private MessageBuilder createBuilder() {
        return new MessageBuilder()
                .withDocumentType(DocumentType.valueOf(document.getType()))
                .withTin(document.getTin())
                .withNumber(document.getNumber())
                .withPhase(ProcessingPhase.valueOf(document.getPhase()))
                .withState(ProcessingState.valueOf(document.getState()));
    }
}
