// SchedulerCountActivity.java

package com.mw.wduwg.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public class SchedulerCount extends TimerTask {

	Looper looper = Looper.getMainLooper();
	Handler mHandler = new Handler(looper);
	GlobalVariable globalVariable;
	String imeiNo, currentUUID;
	boolean isprocessing = false;
	RequestQueue queue;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	public SchedulerCount(Context context, String incomingImeiNo) {
		super();
		sdf.setTimeZone(TimeZone.getTimeZone("gmt"));
		imeiNo = incomingImeiNo;
		queue = Volley.newRequestQueue(context);
		globalVariable = (GlobalVariable) context.getApplicationContext();
		currentUUID = imeiNo + "--" + UUID.randomUUID().toString() + "--" + sdf.format(new Date());
	}

	public void run() {
		mHandler.post(new Runnable() {
			public void run() {
				if (globalVariable.isInternet() == true
						&& isprocessing == false) {
					try {
						isprocessing = true;
						String url = ServerURLs.URL + ServerURLs.COUNTER;

						JSONObject jsonObject = new JSONObject();
						jsonObject.put("uuid", currentUUID);
						jsonObject.put("device_id", imeiNo);
						jsonObject.put("womenin",
								globalVariable.getWomenIn());
						jsonObject.put("womenout",
								globalVariable.getWomenOut());
						jsonObject.put("menin",
								globalVariable.getMenIn());
						jsonObject.put("menout",
								globalVariable.getMenOut());
						jsonObject.put("time", sdf.format(new Date()));
						jsonObject.put("business_id", globalVariable
								.getSelectedBusiness().getId().get$oid());

						JsonObjectRequest jsonObjRequest = new JsonObjectRequest(
								Method.POST, url, jsonObject,
								new Response.Listener<JSONObject>() {
									@Override
									public void onResponse(JSONObject arg0) {
										try {											
											globalVariable.setTotalInDB(arg0.getInt("total"));											
											globalVariable.saveSharedPreferences();
											currentUUID = imeiNo + "--" + UUID.randomUUID().toString() + "--" + sdf.format(new Date());
										} catch (JSONException e) {
										}
										isprocessing = false;
									}
								}, new Response.ErrorListener() {
									@Override
									public void onErrorResponse(VolleyError arg0) {
										isprocessing = false;
									}									
								});
						
						queue.add(jsonObjRequest);

					} catch (Exception e) {
						// Don't do anything -- Skip any error stuff

						isprocessing = false;
					}
				}
			}
		});
	}
}
