package com.rolandopalermo.facturacion.ec.persistence.entity;

import com.rolandopalermo.facturacion.ec.persistence.converters.AttributeSecurer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "digital_cert")
public class DigitalCert {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "digital_cert_generator")
    @SequenceGenerator(name = "digital_cert_generator", sequenceName = "digital_cert_seq", allocationSize = 50)
    @Column(name = "digital_cert_id", updatable = false, nullable = false)
    private long id;

    @Column
    @Convert(converter = AttributeSecurer.class)
    private String password;

    @Column
    private byte[] digitalCert;

    @Column
    private String owner;

    @Column
    private boolean active;

    @Column
    private Date insertDate;

}