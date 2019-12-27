package com.rolandopalermo.facturacion.ec.service.impl;

import com.rolandopalermo.facturacion.ec.persistence.repository.TaxTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rolandopalermo.facturacion.ec.dto.TipoImpuestoDTO;
import com.rolandopalermo.facturacion.ec.persistence.entity.TaxType;

import java.util.Optional;

@Service("taxTypeServiceImpl")
public class TaxTypeServiceImpl extends GenericCRUDServiceImpl<TaxType, TipoImpuestoDTO> {

	@Autowired
	private TaxTypeRepository domainRepository;

	@Override
	public TaxType mapTo(TipoImpuestoDTO tipoImpuestoDTO) {
		TaxType taxType = new TaxType();
		taxType.setCode(tipoImpuestoDTO.getCodigo());
		taxType.setDescription(tipoImpuestoDTO.getDescripcion());
		return taxType;
	}

	@Override
	public Optional<TaxType> findExisting(TipoImpuestoDTO domainObject) {
		return domainRepository.findByCode(domainObject.getCodigo());
	}

	@Override
	public TipoImpuestoDTO build(TaxType domainObject) {
		return null;
	}

}