package com.mw.wduwg.services;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.mw.wduwg.model.Business;
import com.mw.wduwg.model.BusinessFBPage;
import com.mw.wduwg.model.Customer;
import com.mw.wduwg.model.Event;

public class GlobalVariable extends Application {

	// FIXME: shared preferences should be read from here ONLY

	SharedPreferences sharedPreferences;
	
	boolean isMenWomen;

	public boolean isMenWomen() {
		return isMenWomen;
	}

	public void setMenWomen(boolean isMenWomen) {
		this.isMenWomen = isMenWomen;
	}

	Gson gson;
	int menIn, menOut, womenIn, womenOut;
	int intervalMenIn, intervalWomenIn, intervalMenOut, intervalWomenOut;
	SchedulerFBPosts scheduleTask;
	int message_frequency;
	
	public int getMessage_frequency() {
		return message_frequency;
	}

	public void setMessage_frequency(int message_frequency) {
		this.message_frequency = message_frequency;
	}

	Timer timer;

	public boolean isInternet() {
		ConnectivityManager connection = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connection != null) {
			NetworkInfo[] info = connection.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}

			}
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

	Customer customer;
	Business selectedBusiness;

	public int getIntervalMenIn() {
		return intervalMenIn;
	}

	public void setIntervalMenIn(int intervalMenIn) {
		this.intervalMenIn = intervalMenIn;
	}

	public int getIntervalWomenIn() {
		return intervalWomenIn;
	}

	public void setIntervalWomenIn(int intervalWomenIn) {
		this.intervalWomenIn = intervalWomenIn;
	}

	public int getIntervalMenOut() {
		return intervalMenOut;
	}

	public void setIntervalMenOut(int intervalMenOut) {
		this.intervalMenOut = intervalMenOut;
	}

	public int getIntervalWomenOut() {
		return intervalWomenOut;
	}

	public void setIntervalWomenOut(int intervalWomenOut) {
		this.intervalWomenOut = intervalWomenOut;
	}

	BusinessFBPage selectedFBPage;

	public BusinessFBPage getSelectedFBPage() {
		return selectedFBPage;
	}

	public void setSelectedFBPage(BusinessFBPage selectedFBPage) {
		this.selectedFBPage = selectedFBPage;
	}

	Event selectedEvent;
	// CHECK: what is this used for?
	Event selectedEventReports;

	String fb_access_token;
	long fb_access_expire;

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
		if (sharedPreferences.contains("event")) {
			String eventFromSP = sharedPreferences.getString("event", null);
			this.selectedEvent = gson.fromJson(eventFromSP, Event.class);
		}
		if (sharedPreferences.contains("fb_access_token")) {
			this.fb_access_token = sharedPreferences.getString(
					"fb_access_token", null);
		}
		if (sharedPreferences.contains("selectedFBPage")) {
			this.selectedFBPage = gson.fromJson(
					sharedPreferences.getString("selectedFBPage", null),
					BusinessFBPage.class);
		}
		if(sharedPreferences.contains("message_frequency"))
		{
			this.message_frequency = sharedPreferences.getInt("message_frequency", 0);
		}
		this.menIn = sharedPreferences.getInt("menIn", 0);
		this.menOut = sharedPreferences.getInt("menOut", 0);
		this.womenIn = sharedPreferences.getInt("womenIn", 0);
		this.womenOut = sharedPreferences.getInt("womenOut", 0);
		this.isMenWomen = sharedPreferences.getBoolean("isMenWomen",false);
	}

	public void saveSharedPreferences() {
		// FIXME:
		Editor editor = sharedPreferences.edit();
		if (this.fb_access_token != null)
			editor.putString("fb_access_token", this.fb_access_token);
		else if (sharedPreferences.contains("fb_access_token")) {
			editor.remove("fb_access_token");
		}
		if (this.fb_access_expire != 0) {
			editor.putLong("fb_access_expire", this.fb_access_expire);
		} else if (sharedPreferences.contains("fb_access_expire")) {
			editor.remove("fb_access_expire");
		}
		if (selectedFBPage != null) {
			editor.putString("selectedFBPage", gson.toJson(this.selectedFBPage));
		} else if (sharedPreferences.contains("selectedFBPage")) {
			editor.remove("selectedFBPage");
		}

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
		if (this.selectedEvent != null) {
			editor.putBoolean("isEventThere", true);
			String eventgsonToJSON = gson.toJson(this.selectedEvent);
			editor.putString("event", eventgsonToJSON);
		}

		editor.putInt("menIn", menIn);
		editor.putInt("womenIn", womenIn);
		editor.putInt("menOut", menOut);
		editor.putInt("womenOut", womenOut);
		editor.putInt("message_frequency", this.message_frequency);
		editor.putBoolean("isMenWomen", this.isMenWomen);
		
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

	public Event getSelectedEvent() {
		return selectedEvent;
	}

	public void setSelectedEvent(Event selectedEvent) {
		this.selectedEvent = selectedEvent;
	}

	public Event getSelectedReportsEvent() {
		return selectedEventReports;
	}

	public void setSelectedReportsEvent(Event selectedReportsEvent) {
		this.selectedEventReports = selectedReportsEvent;
	}

	public static Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
		int targetWidth = 150;
		int targetHeight = 150;
		Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
				Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(targetBitmap);
		Path path = new Path();
		path.addCircle(((float) targetWidth - 1) / 2,
				((float) targetHeight - 1) / 2,
				(Math.min(((float) targetWidth), ((float) targetHeight)) / 2),
				Path.Direction.CCW);

		canvas.clipPath(path);
		Bitmap sourceBitmap = scaleBitmapImage;
		canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(),
				sourceBitmap.getHeight()), new Rect(0, 0, targetWidth,
				targetHeight), null);
		return targetBitmap;
	}

	public boolean isfacebookOn() {
		return sharedPreferences.getBoolean("facebookSwitch", false);
	}

	public String facebookFrequency() {
		return sharedPreferences.getString("prefFb_frequency", "0");
	}

	public boolean isNotificationOn() {
		return sharedPreferences.getBoolean("prefMessageSwitch", false);
	}

	public int messageFrequency() {
		return Integer.parseInt(sharedPreferences.getString(
				"prefNotificationFrequency", "0"));
	}

	public long getFb_access_expire() {
		return fb_access_expire;
	}

	public void setFb_access_expire(long fb_access_expire) {
		this.fb_access_expire = fb_access_expire;
	}

	public String getFb_access_token() {
		return fb_access_token;
	}

	public void setFb_access_token(String fb_access_token) {
		this.fb_access_token = fb_access_token;
	}

	public String convertDate(String datestr) {
		String formatedDate = "";
		try{
			SimpleDateFormat utcFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
			utcFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		    Date convdate = utcFormatter.parse(datestr);
		    SimpleDateFormat cstformat = new SimpleDateFormat("EEE, d MMM, HH:mm a");
		    cstformat.setTimeZone(TimeZone.getDefault());
		     formatedDate  = cstformat.format(convdate);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return formatedDate;
	}
	
	
	public void fbPostOn(boolean isMenWomen)
	{
		scheduleTask = new SchedulerFBPosts(getApplicationContext());
		timer = new Timer();
		scheduleTask.setMenwomen(isMenWomen);
		timer.scheduleAtFixedRate(scheduleTask, 1000, 10*60*1000);
	}
	public void fbPostOff()
	{
		if(timer != null)
		{
		timer.cancel();
		timer.purge();
		sharedPreferences.edit().putBoolean("facebookSwitch", false).commit();
		}
	}

}
