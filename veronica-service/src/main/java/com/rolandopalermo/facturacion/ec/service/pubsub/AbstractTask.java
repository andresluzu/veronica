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

    protected <T> void publishMessage(T content) {
        try {
            String messageId = publisher.publish(createMessage(content));
            document.setPublishedMessageId(messageId);
        } catch (JsonProcessingException e) {
            LOGGER.error("Cannot create Pub/Sub message", e);
            throw new IllegalStateException(e);
        } catch (InterruptedException e) {
            LOGGER.error("Pub/Sub publish was interrupted", e);
            throw new IllegalStateException(e);
        } catch (ExecutionException e) {
            LOGGER.error("Cannot send Pub/Sub message", e);
            throw new IllegalStateException(e);
        }

        repository.save(document);
    }

    protected PubsubMessage createMessage(Object obj) throws JsonProcessingException {
        return createBuilder().withContent(obj.toString()).build();
    }

    protected PubsubMessage createMessage(Exception exception) throws JsonProcessingException {
        return createBuilder().withException(exception).build();
    }

    protected PubsubMessage createMessage(ComprobanteIdDTO dto) throws JsonProcessingException {
        return createBuilder().withComprobanteId(dto).build();
    }

    protected PubsubMessage createMessage(RespuestaSolicitudDTO dto) throws JsonProcessingException {
        return createBuilder().withRespuestaSolicitud(dto).build();
    }

    protected PubsubMessage createMessage(RespuestaComprobanteDTO dto) throws JsonProcessingException {
        return createBuilder().withRespuestaComprobante(dto).build();
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
