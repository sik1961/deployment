package com.sik.jpa;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(DomainObject.class)
@SuppressWarnings("all")
public final class DomainObject_ {

	private DomainObject_() {}

	/**
	 * ID attribute.
	 */
	public static volatile SingularAttribute<DomainObject, String> id;

}
