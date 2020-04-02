package com.rolandopalermo.facturacion.ec.service.pubsub;

import lombok.Data;

@Data
public class ProcessedDocument {
    private final String phase;
    private final String type;
    private final String tin;
    private final String number;
    private final String state;
    private final String content;

}
