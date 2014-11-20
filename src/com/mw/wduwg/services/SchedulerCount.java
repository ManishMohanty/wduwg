// SchedulerCountActivity.java

package com.mw.wduwg.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Looper;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

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
	JsonObjectRequest jsonObjRequest = null;

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
				if(globalVariable.isInternet() == true && isprocessing == false && jsonObjRequest == null)
				try {
					isprocessing = true;
					sdf.setTimeZone(TimeZone.getTimeZone("gmt"));
							String uuid = imeiNo + UUID.randomUUID().toString() + sdf.format(new Date()); // get uuid
							JSONObject jsonObject2 = null;
							String url = ServerURLs.URL + ServerURLs.COUNTER;
							JSONObject jsonObject = new JSONObject();
							jsonObject
									.put("uuid", uuid)
									.put("women_in",
											globalVariable.getIntervalWomenIn())
									.put("women_out",
											globalVariable
													.getIntervalWomenOut())
									.put("men_in",
											globalVariable.getIntervalMenIn())
									.put("men_out",
											globalVariable.getIntervalMenOut())
									.put("time", sdf.format(new Date()))
									.put("business_id",
											globalVariable
													.getSelectedBusiness()
													.getId().get$oid());
							
							jsonObject2 = new JSONObject().put("counter",
									jsonObject);

							jsonObjRequest = new JsonObjectRequest(
									Method.POST, url, jsonObject2,
									new Response.Listener<JSONObject>() {
										@Override
										public void onResponse(JSONObject arg0) {
											try {
												globalVariable.setIntervalMenIn(0);
												globalVariable.setIntervalMenOut(0);
												globalVariable.setIntervalWomenIn(0);
												globalVariable.setIntervalWomenOut(0);
												globalVariable.saveSharedPreferences();
												
												globalVariable.setTotalInDB(arg0
														.getInt("total"));
												
												jsonObjRequest = null;
												
												
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}
									}, new Response.ErrorListener() {
										public void onErrorResponse(VolleyError arg0) {
											isprocessing = false;
										}
									});
							isprocessing = false;
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
