// SchedulerCountActivity.java

package com.mw.wduwg.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.UUID;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Looper;

public class SchedulerCount extends TimerTask {

	Looper looper = Looper.getMainLooper();
	private Handler mHandler = new Handler(looper);

	Editor editor;
	Context context;
	JSONParser jParser;
	JSONObject jsonFromServer;
	GlobalVariable globalVariable;
	String imeiNo;
	boolean isprocessing = false;
	
	SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");

	public SchedulerCount(Context context,String imeiNo) {
		super();
		this.context = context;
		this.imeiNo = imeiNo;
		globalVariable = (GlobalVariable) context.getApplicationContext();
	}

	public void run() {

		mHandler.post(new Runnable() {
			public void run() {
				if(globalVariable.isInternet() == true && isprocessing == false) {
					try {
						isprocessing = true;

						sdf.setTimeZone(TimeZone.getTimeZone("gmt"));

						String uuid = imeiNo + UUID.randomUUID().toString() + sdf.format(new Date());
						String url = ServerURLs.URL + ServerURLs.COUNTER;

						JSONObject jsonObject = new JSONObject();
						jsonObject.put("uuid", uuid);
						jsonObject.put("women_in",globalVariable.getIntervalWomenIn());
						jsonObject.put("women_out",globalVariable.getIntervalWomenOut());
						jsonObject.put("men_in",globalVariable.getIntervalMenIn());
						jsonObject.put("men_out",globalVariable.getIntervalMenOut());
						jsonObject.put("time", sdf.format(new Date()));
						jsonObject.put("business_id",globalVariable.getSelectedBusiness().getId().get$oid());

						JSONObject jsonResult = new JSONParser().getJSONFromUrlAfterHttpPost(url, new JSONObject().put("counter", jsonObject));

						if (jsonResult.getString("status").equals("ok")) {											
							globalVariable.setIntervalMenIn(0);
							globalVariable.setIntervalMenOut(0);
							globalVariable.setIntervalWomenIn(0);
							globalVariable.setIntervalWomenOut(0);
							globalVariable.saveSharedPreferences();
							globalVariable.setTotalInDB(jsonResult.getInt("total"));
						}
						
						isprocessing = false;
					} catch (Exception e) {
						//Don't do anything -- Skip any error stuff
						
						isprocessing = false;
					}
				}
			}
		});
	}
}
