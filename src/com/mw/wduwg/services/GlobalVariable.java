package com.mw.wduwg.services;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.FormatException;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout.Alignment;
import android.widget.Toast;

import com.example.wduwg.tiles.LoginFacebookActivity;
import com.example.wduwg.tiles.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.android.Facebook;
import com.google.gson.Gson;
import com.mw.wduwg.model.Business;
import com.mw.wduwg.model.BusinessFBPage;
import com.mw.wduwg.model.Customer;
import com.mw.wduwg.model.Event;
import com.mw.wduwg.model.Special;
import com.parse.entity.mime.HttpMultipartMode;
import com.parse.entity.mime.MultipartEntity;
import com.parse.entity.mime.content.ByteArrayBody;
import com.parse.entity.mime.content.StringBody;

public class GlobalVariable extends Application {

	// FIXME: shared preferences should be read from here ONLY

	SharedPreferences sharedPreferences;

	Gson gson;
	int menIn, menOut, womenIn, womenOut;
	int intervalMenIn, intervalWomenIn, intervalMenOut, intervalWomenOut;

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
		this.menIn = sharedPreferences.getInt("menIn", 0);
		this.menOut = sharedPreferences.getInt("menOut", 0);
		this.womenIn = sharedPreferences.getInt("womenIn", 0);
		this.womenOut = sharedPreferences.getInt("womenOut", 0);
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
		System.out.println(">>>>>>> while posting date:" + datestr);
		int hour = Integer.parseInt(datestr.split("T")[1].split(":")[0]) - 5;
		int minutes = Integer.parseInt(datestr.split("T")[1].split(":")[1]);
		String minutesStr;
		if(minutes < 10)
		{
			minutesStr = "0"+minutes;
		}else
			minutesStr = ""+minutes;
		int date = Integer.parseInt(datestr.split("T")[0].split("-")[2]);
		int month = Integer.parseInt(datestr.split("T")[0].split("-")[1]);
		int year = Integer.parseInt(datestr.split("T")[0].split("-")[0]);
		if (hour < 0) {
			date = date - 1;
			hour = 24 + hour;
		}
		if (date == 31) {
			month = month - 1;
		}
		if (month == 0) {
			month = 12;
			year = year - 1;
		}
		
		String formatedDate = "";
		switch (month) {
		case 1:
			if (hour < 12)
				formatedDate = date + " " + "Jan" + " " + year + ", "+"0" + hour
						+ ":" + minutesStr + " am";
			else
				formatedDate = date + " " + "Jan" + " " + year + ", "
						+ (hour - 12) + ":" + minutesStr + " pm";
			break;
		case 2:
			if (hour < 12)
				formatedDate = date + " " + "Feb" + " " + year + ", "+"0" + hour
						+ ":" + minutesStr + " am";
			else
				formatedDate = date + " " + "Feb" + " " + year + ", "
						+ (hour - 12) + ":" + minutesStr + " pm";
			break;
		case 3:
			if (hour < 12)
				formatedDate = date + " " + "Mar" + " " + year + ", "+"0" + hour
						+ ":" + minutesStr + " am";
			else
				formatedDate = date + " " + "Mar" + " " + year + ", "
						+ (hour - 12) + ":" + minutesStr + " pm";
			break;
		case 4:
			if (hour < 12)
				formatedDate = date + " " + "Apr" + " " + year + ", "+"0" + hour
						+ ":" + minutesStr + " am";
			else
				formatedDate = date + " " + "Apr" + " " + year + ", "
						+ (hour - 12) + ":" + minutesStr + " pm";
			break;
		case 5:
			if (hour < 12)
				formatedDate = date + " " + "May" + " " + year + ", "+"0" + hour
						+ ":" + minutesStr + " am";
			else
				formatedDate = date + " " + "May" + " " + year + ", "
						+ (hour - 12) + ":" + minutesStr + " pm";
			break;
		case 6:
			if (hour < 12)
				formatedDate = date + " " + "Jun" + " " + year + ", "+"0" + hour
						+ ":" + minutesStr + " am";
			else
				formatedDate = date + " " + "Jun" + " " + year + ", "
						+ (hour - 12) + ":" + minutesStr + " pm";
			break;
		case 7:
			if (hour < 12)
				formatedDate = date + " " + "Jul" + " " + year + ", "+"0" + hour
						+ ":" + minutesStr + " am";
			else
				formatedDate = date + " " + "Jul" + " " + year + ", "
						+ (hour - 12) + ":" + minutesStr + " pm";
			break;
		case 8:
			if (hour < 12)
				formatedDate = date + " " + "Aug" + " " + year + ", "+"0" + hour
						+ ":" + minutesStr + " am";
			else
				formatedDate = date + " " + "Aug" + " " + year + ", "
						+ (hour - 12) + ":" + minutesStr + " pm";
			break;
		case 9:
			if (hour < 12)
				formatedDate = date + " " + "Sep" + " " + year + ", "+"0" + hour
						+ ":" + minutesStr + " am";
			else
				formatedDate = date + " " + "Sep" + " " + year + ", "
						+ (hour - 12) + ":" + minutesStr + " pm";
			break;
		case 10:
			if (hour < 12)
				formatedDate = date + " " + "Oct" + " " + year + ", "+"0" + hour
						+ ":" + minutesStr + " am";
			else
				formatedDate = date + " " + "Oct" + " " + year + ", "
						+ (hour - 12) + ":" + minutesStr + " pm";
			break;
		case 11:
			if (hour < 12)
				formatedDate = date + " " + "Nov" + " " + year + ", "+"0" + hour
						+ ":" + minutesStr + " am";
			else
				formatedDate = date + " " + "Nov" + " " + year + ", "
						+ (hour - 12) + ":" + minutesStr + " pm";
			break;
		case 12:
			if (hour < 12)
				formatedDate = date + " " + "Dec" + " " + year + ", "+"0" + hour
						+ ":" + minutesStr + " am";
			else
				formatedDate = date + " " + "Dec" + " " + year + ", "
						+ (hour - 12) + ":" + minutesStr + " pm";
			break;

		}

		System.out.println(">>>> converted date" + formatedDate);
		return formatedDate;
	}

}
