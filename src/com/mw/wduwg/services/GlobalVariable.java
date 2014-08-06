package com.mw.wduwg.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

	Gson gson;
	int menIn,menOut,womenIn,womenOut;
	int intervalMenIn,intervalWomenIn,intervalMenOut,intervalWomenOut;
	int totalInDB;
	Date resetDate;
	boolean isReset;
	
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

	public boolean isInternet()
	{
		ConnectivityManager connection =  (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connection != null)
		{
		   NetworkInfo[] info = connection.getAllNetworkInfo();
		   if(info != null)
		   {
			   for(int i=0;i<info.length;i++)
			   {
				   if(info[i].getState() == NetworkInfo.State.CONNECTED)
				   {
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
	public void onCreate(){
		super.onCreate();
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		gson = new Gson();
		if (sharedPreferences.contains("customer")){
			String customerFromSP = sharedPreferences.getString("customer", null);
			this.customer = gson.fromJson(customerFromSP, Customer.class);
		}
		if(sharedPreferences.contains("business"))
		{
			String businessFromSP = sharedPreferences.getString("business", null);
			this.selectedBusiness = gson.fromJson(businessFromSP, Business.class);
		}
		if(sharedPreferences.contains("event"))
		{
			String eventFromSP = sharedPreferences.getString("event", null);
			this.selectedEvent = gson.fromJson(eventFromSP, Event.class);
		}
		if(sharedPreferences.contains("fb_access_token"))
		{
			this.fb_access_token = sharedPreferences.getString("fb_access_token", null);
		}
		if(sharedPreferences.contains("selectedFBPage"))
		{
			this.selectedFBPage = gson.fromJson(sharedPreferences.getString("selectedFBPage", null), BusinessFBPage.class);
		}
		this.menIn=sharedPreferences.getInt("menIn", 0);
		this.menOut=sharedPreferences.getInt("menOut", 0);
		this.womenIn=sharedPreferences.getInt("womenIn", 0);
		this.womenOut=sharedPreferences.getInt("womenOut", 0);
		this.intervalMenIn = sharedPreferences.getInt("intervalMenIn", 0);
		this.intervalMenOut = sharedPreferences.getInt("intervalMenOut", 0);
		this.intervalWomenIn = sharedPreferences.getInt("intervalWomenIn", 0);
		this.intervalWomenOut = sharedPreferences.getInt("intervalWomenOut", 0);
		this.isReset = sharedPreferences.getBoolean("isreset", false);
//		this.resetDate = new Date(sharedPreferences.getString("resetdate", null));
	}	
	
	public void saveSharedPreferences(){
		// FIXME:
		Editor editor = sharedPreferences.edit();
		if(this.fb_access_token !=null)
		editor.putString("fb_access_token", this.fb_access_token);
		else if(sharedPreferences.contains("fb_access_token"))
		{
			editor.remove("fb_access_token");
		}
		if(this.fb_access_expire != 0)
		{
			editor.putLong("fb_access_expire", this.fb_access_expire);
		}else if(sharedPreferences.contains("fb_access_expire"))
		{
			editor.remove("fb_access_expire");
		}
		if(selectedFBPage!=null)
		{
		  editor.putString("selectedFBPage", gson.toJson(this.selectedFBPage))	;
		}else if(sharedPreferences.contains("selectedFBPage"))
		{
			editor.remove("selectedFBPage");
		}
		
		
		String customergsonToJSON = gson.toJson(this.customer);
		editor.putString("customer", customergsonToJSON);
		if(this.selectedBusiness != null){
		String businessgsonToJSON = gson.toJson(this.selectedBusiness);
		editor.putString("business", businessgsonToJSON);
		}else if(sharedPreferences.contains("business"))
		{
			editor.remove("business");
			editor.remove("isDeviceRegistered");
			editor.remove("event");
			editor.remove("isEventThere");
		}else if(sharedPreferences.contains("isDeviceRegistered"))
		{
			editor.remove("isDeviceRegistered");
		}else if(sharedPreferences.contains("event"))
		{
			editor.remove("event");
		}else if(sharedPreferences.contains("isEventThere"))
		{
			editor.remove("isEventThere");
		}
		
		
		if(this.selectedBusiness != null)
		{
			editor.putBoolean("isDeviceRegistered", true);
		}
		if(this.selectedEvent!= null)
		{
			editor.putBoolean("isEventThere", true);
			String eventgsonToJSON = gson.toJson(this.selectedEvent);
			editor.putString("event", eventgsonToJSON);
		}
		
			editor.putInt("menIn", menIn);
			editor.putInt("womenIn", womenIn);
			editor.putInt("menOut", menOut);
			editor.putInt("womenOut", womenOut);
			editor.putInt("intervalMenIn", intervalMenIn);
			editor.putInt("intervalMenOut",intervalMenOut);
			editor.putInt("intervalWomenIn", intervalWomenIn);
			editor.putInt("intervalWomenOut", intervalWomenOut);
			editor.putBoolean("isreset", this.isReset);
//			editor.putString("resetdate", this.resetDate.toString());
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
	
	public boolean isfacebookOn()
	{
		return sharedPreferences.getBoolean("facebookSwitch", false);
	}
	public int facebookFrequency()
	{
		return Integer.parseInt(sharedPreferences.getString("prefFb_frequency", "0"));
	}
	
	public boolean isNotificationOn()
	{
		return sharedPreferences.getBoolean("prefMessageSwitch", false);
	}
	public int messageFrequency()
	{
		return Integer.parseInt(sharedPreferences.getString("prefNotificationFrequency", "0"));
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
	
	public String timeFormat(String datetime)
	{
		System.out.println(">>>>>>>current time:"+datetime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat df = new SimpleDateFormat("MMMM dd, yyyy");
		String date = datetime.split(",")[0];
		int day =Integer.parseInt( date.split("-")[2]);
		String time = datetime.split(",")[1];
		int hh = Integer.parseInt(time.split(":")[0]);
		hh = hh -5;
		if(hh < 0)
		{
			
			hh = hh + 24;
			day = day -1;
		}
		if((hh) > 11)
		{
			if(hh != 12)
			time = (hh-12) + ":"+time.split(":")[1]+" PM  ";
			else
				time = (hh) + ":"+time.split(":")[1]+" PM  ";
		}else
		{
			time = (hh) + ":"+time.split(":")[1]+" AM  ";
		}
		try{
			date = date.substring(0, date.length()-2)+  day;
		 return time+ df.format(sdf.parse(date));
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	
}
