package com.example.demo.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author Herick Melo
 *
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(nullable = true, updatable = false)
	@CreatedDate
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCreation = new Date();

	@LastModifiedDate
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataModification;

	/**
	 * Atributo de controle para exclusão lógica TRUE = active FALSE = inactive
	 */
	@Column
	private Boolean active = true;

	public AbstractEntity() {
		super();
	}

	public abstract Long getId();

	public abstract void setId(Long id);

	@JsonIgnore
	public Date getDataCreation() {
		return dataCreation;
	}

	public void setDataCreation(Date dataCreation) {
		this.dataCreation = dataCreation;
	}

	@JsonIgnore
	public Date getDataModification() {
		return dataModification;
	}

	public void setDataModification(Date dataModification) {
		this.dataModification = dataModification;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AbstractEntity other = (AbstractEntity) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

	@PreUpdate
	public void preUpdate() {
		this.dataModification = new Date();
	}

	@JsonIgnore
	public Boolean getActive() {
		return active;
	}

	@JsonIgnore
	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

}
