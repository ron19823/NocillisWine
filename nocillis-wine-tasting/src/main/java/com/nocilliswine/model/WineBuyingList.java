package com.nocilliswine.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author Rohan Sharma
 *
 */
@Entity
@Table(name = "wine_buying_list", indexes = { @Index(name = "person_id_idx", columnList = "person_id") }, uniqueConstraints = @UniqueConstraint(columnNames = { "wine_id", "person_id" }))
public class WineBuyingList implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2197280293452133862L;

	@Id
	@Column(name = "wine_id", length=150)
	String wineId;

	@Column(name = "person_id", length=150)
	String personId;

	public WineBuyingList() {
	}

	public WineBuyingList(String wineId, String personId) {
		super();
		this.wineId = wineId;
		this.personId = personId;
	}

	public String getWineId() {
		return wineId;
	}

	public void setWineId(String wineId) {
		this.wineId = wineId;
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	@Override
	public String toString() {
		return "WineBuyingList [wineId=" + wineId + ", personId=" + personId + "]";
	}

}
