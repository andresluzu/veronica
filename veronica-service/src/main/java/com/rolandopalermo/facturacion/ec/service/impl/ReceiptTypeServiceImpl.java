package com.rolandopalermo.facturacion.ec.service.impl;

import com.rolandopalermo.facturacion.ec.persistence.repository.ReceiptTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rolandopalermo.facturacion.ec.dto.TipoDocumentoDTO;
import com.rolandopalermo.facturacion.ec.persistence.entity.ReceiptType;

import java.util.Optional;

@Service("receiptTypeServiceImpl")
public class ReceiptTypeServiceImpl extends GenericCRUDServiceImpl<ReceiptType, TipoDocumentoDTO> {

	@Autowired
	private ReceiptTypeRepository domainRepository;

	@Override
	public ReceiptType mapTo(TipoDocumentoDTO tipoDocumentoRetenidoDTO) {
		ReceiptType receiptType = new ReceiptType();
		receiptType.setCode(tipoDocumentoRetenidoDTO.getCodigo());
		receiptType.setDescription(tipoDocumentoRetenidoDTO.getDescripcion());
		return receiptType;
	}

	@Override
	public Optional<ReceiptType> findExisting(TipoDocumentoDTO domainObject) {
		return domainRepository.findByCode(domainObject.getCodigo());
	}

	@Override
	public TipoDocumentoDTO build(ReceiptType domainObject) {
		return null;
	}
	
}