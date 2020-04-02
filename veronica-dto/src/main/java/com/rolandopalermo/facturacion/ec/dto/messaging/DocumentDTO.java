package com.rolandopalermo.facturacion.ec.dto.messaging;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class DocumentDTO {

    @NotEmpty
    public String state;
    @NotNull
    public Long timestamp;
    @NotNull
    public Integer attempts;
    @NotEmpty
    public String data;
    @NotEmpty
    public String type;
}
