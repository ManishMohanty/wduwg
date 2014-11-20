package com.mw.wduwg.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Customer implements Serializable {

	private static final long serialVersionUID = -5203463277930297116L;

	@SerializedName("_id")
	Id id;

	String name;
	String email;
	
	List<Business> businesses = new ArrayList<Business>();


	public Id getId() {
		return id;
	}

	public void setId(Id id) {
		this.id = id;
	}



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Business> getBusinesses() {
		return businesses;
	}

	public void setBusinesses(List<Business> business) {
		this.businesses = business;
	}

	public static class Id{
	    String $oid;

		public String get$oid() {
			return $oid;
		}

		public void set$oid(String $oid) {
			this.$oid = $oid;
		}

	}
	
}