package com.rolandopalermo.facturacion.ec.service.pubsub;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.pubsub.v1.PubsubMessage;
import com.rolandopalermo.facturacion.ec.dto.v1_0.ComprobanteDTO;
import com.rolandopalermo.facturacion.ec.persistence.entity.Document;
import com.rolandopalermo.facturacion.ec.persistence.repository.DocumentRepository;
import com.rolandopalermo.facturacion.ec.service.v1_0.impl.GenericSRIServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DocumentMessageReceiver implements MessageReceiver {

    private static final Logger LOGGER = LogManager.getLogger(DocumentMessageReceiver.class);

    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private DomainConfiguration configuration;
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;
    @Autowired
    private SRIServiceFactory serviceFactory;
    @Autowired
    private MessagePublisher publisher;

    @Override
    public void receiveMessage(PubsubMessage pubsubMessage, AckReplyConsumer ackReplyConsumer) {
        LOGGER.info("Receiving Pub/Sub message {}", pubsubMessage.getMessageId());
        try {
            if (documentRepository.findByMessageId(pubsubMessage.getMessageId()).isPresent()) {
                LOGGER.info("Skipping message {} it is already processed", pubsubMessage.getMessageId());
                return;
            }
            Document document = createFrom(pubsubMessage);
            ComprobanteDTO dto = DocumentMapper.mapComprobante(document.getType(), document.getContent());
            GenericSRIServiceImpl sriService = serviceFactory.getService(document.getType());
            documentRepository.save(document);
            taskScheduler.schedule(new CreationTask(sriService, documentRepository, taskScheduler, document, dto, publisher),
                    SchedulerUtils.addDelay(document.getDateTime(), document.getRetries(), configuration.getTimeFactor()));
            LOGGER.info("Pub/Sub document stored with id: {}", document.getId());
        } catch (Exception e) {
            LOGGER.error("Cannot receive Pub/Sub message", e);
        } finally {
            ackReplyConsumer.ack();
        }
    }

    private Document createFrom(PubsubMessage message) {
        String type = message.getAttributesOrThrow(configuration.getTypeAttName());
        String tin = message.getAttributesOrThrow(configuration.getTinAttName());
        String number = message.getAttributesOrThrow(configuration.getNumberAttName());
        Date dateTime = new Date(message.getPublishTime().getSeconds() * 1000);
        String content = message.getData().toStringUtf8();

        Document document = new Document();
        document.setMessageId(message.getMessageId());
        document.setTopic(configuration.getSubCreatedTopicName());
        document.setType(type);
        document.setTin(tin);
        document.setNumber(number);
        document.setPhase(ProcessingPhase.CREATION.name());
        document.setState(ProcessingState.INCOMPLETE.name());
        document.setDateTime(dateTime);
        document.setRetries(0);
        document.setContent(content);

        return document;
    }
}
