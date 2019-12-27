package com.rolandopalermo.facturacion.ec.service.impl;

import com.rolandopalermo.facturacion.ec.dto.MetodoPagoDTO;
import com.rolandopalermo.facturacion.ec.persistence.entity.PaymentMethod;
import com.rolandopalermo.facturacion.ec.persistence.repository.PaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("paymentMethodServiceImpl")
public class PaymentMethodServiceImpl extends GenericCRUDServiceImpl<PaymentMethod, MetodoPagoDTO> {

    @Autowired
    private PaymentMethodRepository domainRepository;

    @Override
    public PaymentMethod mapTo(MetodoPagoDTO metodoPagoDTO) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setCode(metodoPagoDTO.getCodigo());
        paymentMethod.setDescription(metodoPagoDTO.getDescripcion());
        return paymentMethod;
    }

    @Override
    public Optional<PaymentMethod> findExisting(MetodoPagoDTO domainObject) {
        return domainRepository.findByCode(domainObject.getCodigo());
    }

    @Override
    public MetodoPagoDTO build(PaymentMethod domainObject) {
        return null;
    }

}