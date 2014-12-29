package com.mw.wduwg.services;

import java.util.Date;
import java.util.Timer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;
import com.mw.wduwg.model.Business;
import com.mw.wduwg.model.Customer;

public class GlobalVariable extends Application {

	SharedPreferences sharedPreferences;

	Gson gson;
	int menIn, menOut, womenIn, womenOut;
	int totalInDB;
	Date lastUpdatedDate;
	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	Date resetDate;
	boolean isReset;
	Timer timer;
	String sessionId;
	
	public Date getResetDate() {
		return resetDate;
	}

	public void setResetDate(Date resetDate) {
		this.resetDate = resetDate;
	}

	public boolean isReset() {
		return isReset;
	}

	public void setReset(boolean isReset) {
		this.isReset = isReset;
	}

	public int getTotalInDB() {
		return totalInDB;
	}

	public void setTotalInDB(int totalInDB) {
		this.totalInDB = totalInDB;
	}
	
	public String getIMEINo(){
		String imeiNo = null;
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		if(null != telephonyManager){
			imeiNo = telephonyManager.getDeviceId();			
		}
		return imeiNo;
	}

	public boolean isInternet() {
		ConnectivityManager connection = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connection != null) {
			NetworkInfo info = connection
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			return (info != null && info.getState() == NetworkInfo.State.CONNECTED);
		}
		return false;
	}

	public int getMenIn() {
		return menIn;
	}

	public void setMenIn(int menIn) {
		this.menIn = menIn;
	}

	public int getMenOut() {
		return menOut;
	}

	public void setMenOut(int menOut) {
		this.menOut = menOut;
	}

	public int getWomenIn() {
		return womenIn;
	}

	public void setWomenIn(int womenIn) {
		this.womenIn = womenIn;
	}

	public int getWomenOut() {
		return womenOut;
	}

	public void setWomenOut(int womenOut) {
		this.womenOut = womenOut;
	}
	
	public String getSessionId(){
		return sessionId;
	}
	
	public void setSessionId(String sessionId){
		this.sessionId = sessionId;
	}

	Customer customer;
	Business selectedBusiness;

	@Override
	public void onCreate() {
		super.onCreate();
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		gson = new Gson();
		if (sharedPreferences.contains("customer")) {
			String customerFromSP = sharedPreferences.getString("customer",
					null);
			this.customer = gson.fromJson(customerFromSP, Customer.class);
		}
		if (sharedPreferences.contains("business")) {
			String businessFromSP = sharedPreferences.getString("business",
					null);
			this.selectedBusiness = gson.fromJson(businessFromSP,
					Business.class);
		}
		this.menIn = sharedPreferences.getInt("menIn", 0);
		this.menOut = sharedPreferences.getInt("menOut", 0);
		this.womenIn = sharedPreferences.getInt("womenIn", 0);
		this.womenOut = sharedPreferences.getInt("womenOut", 0);
		this.isReset = sharedPreferences.getBoolean("isreset", false);
		this.totalInDB = sharedPreferences.getInt("totalInDB", 0);
	}

	public void saveSharedPreferences() {
		Editor editor = sharedPreferences.edit();
		String customergsonToJSON = gson.toJson(this.customer);
		editor.putString("customer", customergsonToJSON);
		if (this.selectedBusiness != null) {
			String businessgsonToJSON = gson.toJson(this.selectedBusiness);
			editor.putString("business", businessgsonToJSON);
		} else if (sharedPreferences.contains("business")) {
			editor.remove("business");
			editor.remove("isDeviceRegistered");
			editor.remove("event");
			editor.remove("isEventThere");
		} else if (sharedPreferences.contains("isDeviceRegistered")) {
			editor.remove("isDeviceRegistered");
		} else if (sharedPreferences.contains("event")) {
			editor.remove("event");
		} else if (sharedPreferences.contains("isEventThere")) {
			editor.remove("isEventThere");
		}
		if (this.selectedBusiness != null) {
			editor.putBoolean("isDeviceRegistered", true);
		}
		editor.putInt("menIn", menIn);
		editor.putInt("womenIn", womenIn);
		editor.putInt("menOut", menOut);
		editor.putInt("womenOut", womenOut);
		editor.putInt("totalInDB", totalInDB);
		editor.putBoolean("isreset", this.isReset);
		editor.commit();
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer selectedCustomer) {
		this.customer = selectedCustomer;
	}

	public Business getSelectedBusiness() {
		return selectedBusiness;
	}

	public void setSelectedBusiness(Business selectedBusiness) {
		this.selectedBusiness = selectedBusiness;
	}
	
	public Timer getTimer(){
		return timer;
	}
	
	public void setTimer(Timer createdTimer){
		timer = createdTimer;
	}	
}