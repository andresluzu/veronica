package com.rolandopalermo.facturacion.ec.service.pubsub;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import com.google.pubsub.v1.PubsubMessage;
import com.rolandopalermo.facturacion.ec.persistence.entity.Document;
import com.rolandopalermo.facturacion.ec.persistence.repository.DocumentRepository;
import com.rolandopalermo.facturacion.ec.service.TestBaseSpring;
import com.rolandopalermo.facturacion.ec.service.v1_0.impl.GenericSRIServiceImpl;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = DocumentMessageReceiver.class)
public class DocumentMessageReceiverTest extends TestBaseSpring {

    private static final String SUB_CREATED_TOPIC_NAME = "sub.topic";
    private static final String MESSAGE_ID = "123123232";
    private static final String TYPE_ATT_NAME = "type";
    private static final String TIN_ATT_NAME = "tin";
    private static final String NUMBER_ATT_NAME = "number";
    private static final String TYPE = "FACTURA";
    private static final String TIN = "2390007488001";
    private static final String NUMBER = "001-001-000000123";
    private static final Timestamp TIMESTAMP = Timestamp.newBuilder().build();
//    private static final ByteString DATA = ByteString.copyFromUtf8("SOME CONTENT");

    @MockBean
    DomainConfiguration domainConfiguration;
    @MockBean
    DocumentRepository documentRepository;
    @MockBean
    ThreadPoolTaskScheduler taskScheduler;
    @MockBean
    SRIServiceFactory serviceFactory;
    @MockBean
    GenericSRIServiceImpl sriService;
    @MockBean
    MessagePublisher publisher;

    @Autowired
    DocumentMessageReceiver messageReceiver;

    @Value("classpath:json/factura.json")
    Resource invoiceResource;

    @Mock
    AckReplyConsumer ackReplyConsumer;
    PubsubMessage pubsubMessage;
    Document document;

    @Before
    public void setUp() throws Exception {
        assertNotNull(messageReceiver);

        when(domainConfiguration.getSubCreatedTopicName()).thenReturn(SUB_CREATED_TOPIC_NAME);
        when(domainConfiguration.getTypeAttName()).thenReturn(TYPE_ATT_NAME);
        when(domainConfiguration.getTinAttName()).thenReturn(TIN_ATT_NAME);
        when(domainConfiguration.getNumberAttName()).thenReturn(NUMBER_ATT_NAME);

        when(serviceFactory.getService(any(String.class))).thenReturn(sriService);
        when(documentRepository.findByMessageId(MESSAGE_ID)).thenReturn(Optional.ofNullable(null));
        String invoiceJson = FileUtils.readFileToString(invoiceResource.getFile(), "UTF-8");
        ByteString invoiceData = ByteString.copyFromUtf8(invoiceJson);

        pubsubMessage = PubsubMessage.newBuilder()
                .putAttributes(TYPE_ATT_NAME, TYPE)
                .putAttributes(TIN_ATT_NAME, TIN)
                .putAttributes(NUMBER_ATT_NAME, NUMBER)
                .setMessageId(MESSAGE_ID)
                .setPublishTime(TIMESTAMP)
                .setData(invoiceData)
                .build();

        document = new Document();
        document.setMessageId(MESSAGE_ID);
        document.setTopic(SUB_CREATED_TOPIC_NAME);
        document.setType(TYPE);
        document.setTin(TIN);
        document.setNumber(NUMBER);
        document.setPhase(ProcessingPhase.CREATION.name());
        document.setState(ProcessingState.INCOMPLETE.name());
        document.setDateTime(new Date(TIMESTAMP.getSeconds()));
        document.setRetries(0);
        document.setContent(invoiceData.toStringUtf8());
    }

    @Test
    public void receiveMessage() {
        messageReceiver.receiveMessage(pubsubMessage, ackReplyConsumer);
        verify(documentRepository).save(document);
        verify(ackReplyConsumer).ack();
        verify(ackReplyConsumer, times(0)).nack();
        verify(taskScheduler).schedule(any(CreationTask.class), any(Date.class));
    }

    @Test
    public void receiveDuplicatedMessage() {
        when(documentRepository.findByMessageId(MESSAGE_ID)).thenReturn(Optional.ofNullable(document));
        messageReceiver.receiveMessage(pubsubMessage, ackReplyConsumer);
        verify(documentRepository, times(1)).findByMessageId(MESSAGE_ID);
        verifyNoMoreInteractions(documentRepository);
        verifyNoMoreInteractions(taskScheduler);
        verify(ackReplyConsumer).ack();
    }

    @Test
    public void receiveMessageRepoError() {
        when(documentRepository.save(any(Document.class))).thenThrow(new RuntimeException());
        messageReceiver.receiveMessage(pubsubMessage, ackReplyConsumer);
        verify(ackReplyConsumer).ack();
        verify(ackReplyConsumer, times(0)).nack();
    }

    @Test
    public void receiveMessageNoType() {
        pubsubMessage = PubsubMessage.newBuilder()
                .putAttributes(TIN_ATT_NAME, TIN)
                .putAttributes(NUMBER_ATT_NAME, NUMBER)
                .build();
        messageReceiver.receiveMessage(pubsubMessage, ackReplyConsumer);
        verify(documentRepository, times(0)).save(document);
        verify(ackReplyConsumer).ack();
        verify(ackReplyConsumer, times(0)).nack();
    }

    @Test
    public void receiveMessageNoTin() {
        pubsubMessage = PubsubMessage.newBuilder()
                .putAttributes(TYPE_ATT_NAME, TYPE)
                .putAttributes(NUMBER_ATT_NAME, NUMBER)
                .build();
        messageReceiver.receiveMessage(pubsubMessage, ackReplyConsumer);
        verify(documentRepository, times(0)).save(document);
        verify(ackReplyConsumer).ack();
        verify(ackReplyConsumer, times(0)).nack();
    }

    @Test
    public void receiveMessageNoNumber() {
        pubsubMessage = PubsubMessage.newBuilder()
                .putAttributes(TYPE_ATT_NAME, TYPE)
                .putAttributes(TIN_ATT_NAME, TIN)
                .build();
        messageReceiver.receiveMessage(pubsubMessage, ackReplyConsumer);
        verify(documentRepository, times(0)).save(document);
        verify(ackReplyConsumer).ack();
        verify(ackReplyConsumer, times(0)).nack();
    }
}