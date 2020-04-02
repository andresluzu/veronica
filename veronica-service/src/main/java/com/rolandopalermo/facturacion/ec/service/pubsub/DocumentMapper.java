package com.rolandopalermo.facturacion.ec.service.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rolandopalermo.facturacion.ec.common.util.DocumentType;
import com.rolandopalermo.facturacion.ec.dto.v1_0.ComprobanteDTO;
import com.rolandopalermo.facturacion.ec.dto.v1_0.bol.GuiaRemisionDTO;
import com.rolandopalermo.facturacion.ec.dto.v1_0.cm.NotaCreditoDTO;
import com.rolandopalermo.facturacion.ec.dto.v1_0.dm.NotaDebitoDTO;
import com.rolandopalermo.facturacion.ec.dto.v1_0.invoice.FacturaDTO;
import com.rolandopalermo.facturacion.ec.dto.v1_0.withholding.RetencionDTO;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class DocumentMapper {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ComprobanteDTO mapComprobante(String type, String content) throws IOException {
        requireNonNull(type, "type cannot be null");
        requireNonNull(content, "content cannot be null");

        DocumentType documentType = DocumentType.valueOf(type);
        ComprobanteDTO result;
        switch (documentType) {
            case FACTURA:
                result = MAPPER.readValue(content, FacturaDTO.class);
                break;
            case NOTA_DEBITO:
                result = MAPPER.readValue(content, NotaDebitoDTO.class);
                break;
            case NOTA_CREDITO:
                result = MAPPER.readValue(content, NotaCreditoDTO.class);
                break;
            case GUITA_REMISION:
                result = MAPPER.readValue(content, GuiaRemisionDTO.class);
                break;
            case COMPROBANTE_RETENCION:
                result = MAPPER.readValue(content, RetencionDTO.class);
                break;
            default:
                throw new IllegalArgumentException(String.format("Cannot find DTO type for %s", type));
        }
        return result;
    }
}
