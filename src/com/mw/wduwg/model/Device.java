package com.mw.wduwg.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Device implements Serializable {
	
	String name;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@SerializedName("id")
	Id id;
	
	@SerializedName("_id")
	Id _id;
	
	public static class Id implements Serializable{
	    String $oid;

		public String get$oid() {
			return $oid;
		}

		public void set$oid(String $oid) {
			this.$oid = $oid;
		}

	}
	
	public Id getId() {
		if (id != null)
			return id;
		else
			return _id;
	}

	public void setId(Id id) {
		this.id = id;
	}

}
