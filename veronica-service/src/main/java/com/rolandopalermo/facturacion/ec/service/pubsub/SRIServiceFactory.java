package com.rolandopalermo.facturacion.ec.service.pubsub;

import com.rolandopalermo.facturacion.ec.common.util.DocumentType;
import com.rolandopalermo.facturacion.ec.service.v1_0.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

@Component
public class SRIServiceFactory {

    @Autowired
    private InvoiceServiceImpl invoiceService;

    @Autowired
    private BolServiceImpl bolService;

    @Autowired
    private WithHoldingServiceImpl withHoldingService;

    @Autowired
    private CreditMemoServiceImpl creditMemoService;

    @Autowired
    private DebitMemoServiceImpl debitMemoService;

    /**
     * Devuelve el servicio a usar dependiendo del tipo de documento
     *
     * @param type tipo de documento
     * @return Servicio que extiende de GenericSRIServicesImpl
     */
    public GenericSRIServiceImpl getService(String type) {
        requireNonNull(type, "documentType cannot be null");
        DocumentType documentType = DocumentType.valueOf(type);

        GenericSRIServiceImpl service;
        switch (documentType) {
            case FACTURA:
                service = invoiceService;
                break;
            case GUITA_REMISION:
                service = bolService;
                break;
            case NOTA_CREDITO:
                service = creditMemoService;
                break;
            case NOTA_DEBITO:
                service = debitMemoService;
                break;
            case COMPROBANTE_RETENCION:
                service = withHoldingService;
                break;
            default:
                throw new IllegalArgumentException(String.format("Cannot find SRI service for type %s", documentType));
        }
        return service;
    }
}
