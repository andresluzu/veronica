package com.rolandopalermo.facturacion.ec.service.impl;

import com.rolandopalermo.facturacion.ec.common.exception.VeronicaException;
import com.rolandopalermo.facturacion.ec.dto.CertificadoDigitalDTO;
import com.rolandopalermo.facturacion.ec.persistence.entity.DigitalCert;
import com.rolandopalermo.facturacion.ec.persistence.repository.DigitalCertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service("digitalCertServiceImpl")
public class DigitalCertServiceImpl extends GenericCRUDServiceImpl<DigitalCert, CertificadoDigitalDTO> {

    @Autowired
    private DigitalCertRepository domainRepository;

    @Override
    public DigitalCert mapTo(CertificadoDigitalDTO certificadoDigital) {
        DigitalCert cert = new DigitalCert();
        cert.setActive(true);
        cert.setInsertDate(new Date());
        cert.setDigitalCert(certificadoDigital.getArchivo());
        cert.setOwner(certificadoDigital.getRucPropietario());
        cert.setPassword(certificadoDigital.getPassword());
        return cert;
    }

    @Override
    public Optional<DigitalCert> findExisting(CertificadoDigitalDTO domainObject) {
        return domainRepository.findByOwnerAndActive(domainObject.getRucPropietario(), true);
    }

    public Optional<DigitalCert> findExistingByRUC(String RUC) {
        return domainRepository.findByOwnerAndActive(RUC, true);
    }

    @Override
    public void deleteExistingByRUC(String ownerRUC){
        Optional<DigitalCert> digitalCert = findExistingByRUC(ownerRUC);
        try {
            domainRepository.delete(digitalCert.get());
        }
        catch (Exception ex){
            throw new VeronicaException(String.format("No existe certificado con RUC: %s", ownerRUC));
        }
    }

    @Override
    public CertificadoDigitalDTO build(DigitalCert domainObject) {
        return null;
    }



}