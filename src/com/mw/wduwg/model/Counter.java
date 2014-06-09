package com.mw.wduwg.model;

import java.io.Serializable;

public class Counter implements Serializable {

	private static final long serialVersionUID = -7584600693613705845L;

	String objectId;
	String menIn;
	String womenIn;
	String menOut;
	String womenOut;
	String name;
	String type;

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getMenIn() {
		return menIn;
	}

	public void setMenIn(String menIn) {
		this.menIn = menIn;
	}

	public String getWomenIn() {
		return womenIn;
	}

	public void setWomenIn(String womenIn) {
		this.womenIn = womenIn;
	}

	public String getMenOut() {
		return menOut;
	}

	public void setMenOut(String menOut) {
		this.menOut = menOut;
	}

	public String getWomenOut() {
		return womenOut;
	}

	public void setWomenOut(String womenOut) {
		this.womenOut = womenOut;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
