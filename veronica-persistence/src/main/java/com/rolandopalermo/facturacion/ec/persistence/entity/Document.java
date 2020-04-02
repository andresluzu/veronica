package com.rolandopalermo.facturacion.ec.persistence.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "document")
@Data
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_generator")
    @SequenceGenerator(name = "document_generator", sequenceName = "document_seq", allocationSize = 50)
    @Column(name = "document_id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "message_id", nullable = false, length = 50)
    private String messageId;

    @Column(nullable = false, length = 50)
    private String topic;

    @Column(nullable = false, length = 15)
    private String phase;

    @Column(nullable = false, length = 15)
    private String type;

    @Column(nullable = false, length = 15)
    private String tin;

    @Column(nullable = false, length = 20)
    private String number;

    @Column(name = "access_key", length = 50)
    private String accessKey;

    @Column(nullable = false, length = 15)
    private String state;

    @Column(nullable = false)
    private Integer retries;

    @Column(name = "date_time", nullable = false)
    private Date dateTime;

    @Column(nullable = false)
    private String content;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "published_message_id", nullable = false, length = 50)
    private String publishedMessageId;

}