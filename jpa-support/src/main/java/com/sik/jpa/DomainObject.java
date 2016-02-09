package com.sik.jpa;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public abstract class DomainObject implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID")
	private String id;

	@Version
	@Column(name="VERSION")
	private Long version;

	/**
	 * Construct a domain object preconfiguring a new unique ID.
	 */
	public DomainObject() {
		this.id = UUID.randomUUID().toString();
	}

	/**
	 * @return ID of this instance. Remains the same before and after saving to the persistence layer.
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Used where a specific ID must be used, for example when used with an embedded database that must recreate a specific entity.
	 * @param id
	 */
	protected void setId(final String id) {
		this.id = id;
	}

	/**
	 * @param version for internal Hibernate use
	 */
	protected void setVersion(final Long version) {
		this.version = version;
	}

	/**
	 * @returns version for internal Hibernate use
	 */
	protected Long getVersion() {
		return this.version;
	}

	@Override
    @SuppressWarnings("all")
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings("all")
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DomainObject other = (DomainObject) obj;
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!this.id.equals(other.id)) {
            return false;
        }
        return true;
    }
}