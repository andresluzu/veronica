package com.rolandopalermo.facturacion.ec.service.pubsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.rolandopalermo.facturacion.ec.common.util.DocumentType;
import com.rolandopalermo.facturacion.ec.dto.ComprobanteIdDTO;
import com.rolandopalermo.facturacion.ec.dto.v1_0.sri.RespuestaComprobanteDTO;
import com.rolandopalermo.facturacion.ec.dto.v1_0.sri.RespuestaSolicitudDTO;

import java.util.Optional;

public class MessageBuilder {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static final String DEFAULT_TIN = "9999999999999";
    static final String DEFAULT_NUMBER = "000-000-000000000";
    static final String EMPTY_DATA = "EMPTY";
    static final String DEFAULT_CONTENT_TYPE = String.class.getSimpleName();

    private ProcessingPhase phase;
    private DocumentType type;
    private String tin;
    private String number;
    private ProcessingState state;
    private String content;
    private String contentType;

    public MessageBuilder() {
        this.phase = ProcessingPhase.UNDEFINED;
        this.type = DocumentType.FACTURA;
        this.tin = DEFAULT_TIN;
        this.number = DEFAULT_NUMBER;
        this.state = ProcessingState.ERROR;
        this.content = EMPTY_DATA;
        this.contentType = DEFAULT_CONTENT_TYPE;
    }

    private ProcessedDocument create() {
        return new ProcessedDocument(phase.name(), type.name(), tin, number, state.name(), content, contentType);
    }

    public PubsubMessage build() throws JsonProcessingException {
        ProcessedDocument document = create();
        String content = MAPPER.writeValueAsString(document);
        ByteString data = ByteString.copyFromUtf8(content);
        return PubsubMessage.newBuilder().setData(data).build();
    }

    public MessageBuilder withComprobanteId(ComprobanteIdDTO dto) {
        try {
            this.content = MAPPER.writeValueAsString(dto);
            this.contentType = dto.getClass().getSimpleName();
        } catch (JsonProcessingException e) {
            this.state = ProcessingState.ERROR;
            this.content = e.getMessage();
        }
        return this;
    }

    public MessageBuilder withRespuestaSolicitud(RespuestaSolicitudDTO dto) {
        try {
            this.content = MAPPER.writeValueAsString(dto);
            this.contentType = dto.getClass().getSimpleName();
        } catch (JsonProcessingException e) {
            this.state = ProcessingState.ERROR;
            this.content = e.getMessage();
        }
        return this;
    }

    public MessageBuilder withRespuestaComprobante(RespuestaComprobanteDTO dto) {
        try {
            this.content = MAPPER.writeValueAsString(dto);
            this.contentType = dto.getClass().getSimpleName();
        } catch (JsonProcessingException e) {
            this.state = ProcessingState.ERROR;
            this.content = e.getMessage();
        }
        return this;
    }

    public MessageBuilder withException(Exception exception) {
        this.state = ProcessingState.ERROR;
        this.content = exception.getMessage();
        return this;
    }

    public MessageBuilder withContent(String content) {
        this.content = content;
        return this;
    }

    public MessageBuilder withPhase(ProcessingPhase phase) {
        this.phase = Optional.ofNullable(phase).orElse(ProcessingPhase.UNDEFINED);
        return this;
    }

    public MessageBuilder withDocumentType(DocumentType type) {
        this.type = Optional.ofNullable(type).orElse(DocumentType.FACTURA);
        return this;
    }

    public MessageBuilder withTin(String tin) {
        this.tin = Optional.ofNullable(tin).orElse(DEFAULT_TIN);
        return this;
    }

    public MessageBuilder withNumber(String number) {
        this.number = Optional.ofNullable(number).orElse(DEFAULT_NUMBER);
        return this;
    }

    public MessageBuilder withState(ProcessingState state) {
        this.state = Optional.ofNullable(state).orElse(ProcessingState.INVALID);
        return this;
    }
}
