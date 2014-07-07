package com.mw.wduwg.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class Event implements Serializable {

	private static final long serialVersionUID = 6832549226046300557L;


	String name,description,imageUrl;
	
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@SerializedName("start_date_time")
	String startDate;
	
	@SerializedName("end_date_time")
	String endDate;
	
	public Event() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

}
