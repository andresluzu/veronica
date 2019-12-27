package com.rolandopalermo.facturacion.ec.service.impl;

import com.rolandopalermo.facturacion.ec.persistence.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rolandopalermo.facturacion.ec.dto.EmpresaDTO;
import com.rolandopalermo.facturacion.ec.persistence.entity.Supplier;

import java.util.Optional;

@Service("supplierServiceImpl")
public class SupplierServiceImpl extends GenericCRUDServiceImpl<Supplier, EmpresaDTO> {

	@Autowired
	private SupplierRepository domainRepository;

	@Override
	public Supplier mapTo(EmpresaDTO empresaDTO) {
		Supplier supplier = new Supplier();
		supplier.setBusinessName(empresaDTO.getRazonSocial());
		supplier.setIdNumber(empresaDTO.getNumeroIdentificacion());
		supplier.setIdType(empresaDTO.getTipoIdentificacion());
		supplier.setLogo(empresaDTO.getLogo());
		supplier.setDeleted(false);
		return supplier;
	}

	@Override
	public Optional<Supplier> findExisting(EmpresaDTO domainObject) {
		return domainRepository.findByIdNumber(domainObject.getNumeroIdentificacion());
	}

	@Override
	public EmpresaDTO build(Supplier domainObject) {
		EmpresaDTO dto = new EmpresaDTO();
		dto.setLogo(domainObject.getLogo());
		dto.setNumeroIdentificacion(domainObject.getIdNumber());
		dto.setRazonSocial(domainObject.getBusinessName());
		dto.setTipoIdentificacion(domainObject.getIdType());
		return dto;
	}
	
}