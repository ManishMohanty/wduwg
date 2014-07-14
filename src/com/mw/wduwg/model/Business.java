package com.mw.wduwg.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

public class Business implements Serializable {

	private static final long serialVersionUID = -8015437165470024712L;
	
	@SerializedName("id")
	Id id;
	
	@SerializedName("_id")
	Id _id;
	
	String name;

	@SerializedName("google_place_id")
	String googlePlaceID;
	
	String address;
	
	@SerializedName("google_api_result")
	String googleAPIResult;
	
	String imageUrl;
	String imageEncoded;
	Bitmap image;
	String face_book_page;
	
	public String getFace_book_page() {
		return face_book_page;
	}

	public void setFace_book_page(String face_book_page) {
		this.face_book_page = face_book_page;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public String getImageEncoded() {
		return imageEncoded;
	}

	public void setImageEncoded(String imageEncoded) {
		this.imageEncoded = imageEncoded;
	}

	List<Event> eventList = new ArrayList<Event>();
	List<Special>specials = new ArrayList<Special>();

	
	public List<Special> getSpecials() {
		return specials;
	}

	public void setSpecials(List<Special> specials) {
		this.specials = specials;
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
	
	public Id getId() {
		if (id != null)
			return id;
		else
			return _id;
	}

	public void setId(Id id) {
		this.id = id;
	}
	
	public Id get_id() {
		if (_id != null)
			return _id;
		else
			return id;
	}

	public void set_id(Id _id) {
		this._id = _id;
	}

	public String getGooglePlaceID() {
		return googlePlaceID;
	}

	public void setGooglePlaceID(String googlePlaceID) {
		this.googlePlaceID = googlePlaceID;
	}

	
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getGoogleAPIResult() {
		return googleAPIResult;
	}

	public void setGoogleAPIResult(String googleAPIResult) {
		this.googleAPIResult = googleAPIResult;
	}

	public List<Event> getEventList() {
		return eventList;
	}

	public void setEventList(List<Event> eventList) {
		this.eventList = eventList;
	}

}
