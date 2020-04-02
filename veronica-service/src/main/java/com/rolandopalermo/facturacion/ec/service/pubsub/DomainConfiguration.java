package com.rolandopalermo.facturacion.ec.service.pubsub;

import lombok.Data;

@Data
public class DomainConfiguration {

    private static final String TYPE_ATT_NAME = "type";
    private static final String TIN_ATT_NAME = "tin";
    private static final String NUMBER_ATT_NAME = "number";

    private static final int POOL_SIZE = 5;
    private static final int TIME_FACTOR = 60000;  // in seconds

    /**
     * GCP Project ID
     */
    private final String projectId;

    /**
     * Publishing Document Processed Topic Name
     */
    private final String pubProcessedTopicName;

    /**
     * Subscribing Document Creation Topic Name
     */
    private final String subCreatedTopicName;

    public String getTypeAttName() {
        return TYPE_ATT_NAME;
    }

    public String getTinAttName() {
        return TIN_ATT_NAME;
    }

    public String getNumberAttName() {
        return NUMBER_ATT_NAME;
    }

    public int getPoolSize() {
        return POOL_SIZE;
    }

    public int getTimeFactor() {
        return TIME_FACTOR;
    }
}
