package com.rolandopalermo.facturacion.ec.service;

import java.util.List;
import java.util.Optional;

public interface GenericCRUDService<DOMAIN, DTO> {

	public void saveOrUpdate(DTO dtoObject);

	public List<DTO> findAll(DTO dtoObject);

	/**
	 * Allows to map a serializable object to a domain object
	 * @param dtoObject
	 * @return
	 */
	public DOMAIN mapTo(DTO dtoObject);

	/**
	 * Allows to serialize a domain object
	 * @param domainObject
	 * @return
	 */
	public DTO build(DOMAIN domainObject);

	/**
	 * Allows to findExisting an existing object with the same key primary
	 * @param domainObject
	 * @return
	 */
	public abstract Optional<DOMAIN> findExisting(DTO domainObject);

	/**
	 * Allows to delete an existing object identified by a RUC number
	 * @param domainObject
	 * @return
	 */
	public abstract void deleteExisting(DTO domainObject);

}