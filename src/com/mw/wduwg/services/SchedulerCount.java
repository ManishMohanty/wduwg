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
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

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

	RequestQueue queue;

	SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");

	public SchedulerCount(Context context,String imeiNo) {
		super();
		this.context = context;
		this.imeiNo = imeiNo;
		globalVariable = (GlobalVariable) context.getApplicationContext();
		queue = Volley.newRequestQueue(context);
	}

	public void run() {

		mHandler.post(new Runnable() {
			public void run() {
				if(globalVariable.isInternet() == true && isprocessing == false && jsonObjRequest == null)
				try {
					isprocessing = true;
					sdf.setTimeZone(TimeZone.getTimeZone("gmt"));
							// SaveCountAsync async = new SaveCountAsync();
							// async.execute(new String[] { "dfs" });
							String uuid = imeiNo + UUID.randomUUID().toString() + sdf.format(new Date()); // get uuid
							JSONObject jsonObject2 = null;
							String url = ServerURLs.URL + ServerURLs.COUNTER;
//							String url = "http://192.168.102.110:3000/counters.json";
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
							globalVariable.setIntervalMenIn(0);
							globalVariable.setIntervalMenOut(0);
							globalVariable.setIntervalWomenIn(0);
							globalVariable.setIntervalWomenOut(0);
							globalVariable.saveSharedPreferences();
							jsonObject2 = new JSONObject().put("counter",
									jsonObject);

							jsonObjRequest = new JsonObjectRequest(
									Method.POST, url, jsonObject2,
									new Response.Listener<JSONObject>() {

										@Override
										public void onResponse(JSONObject arg0) {
											// TODO Auto-generated method stub
											try {
												globalVariable.setTotalInDB(arg0
														.getInt("total"));
												jsonObjRequest = null;
												isprocessing = false;
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}
									}, new Response.ErrorListener() {

										@Override
										public void onErrorResponse(
												VolleyError arg0) {
											// TODO Auto-generated method stub

											Intent nextIntent = new Intent(
													"scheduler_response_message");
											queue.add(jsonObjRequest);
											isprocessing = false;

											if (arg0 instanceof NoConnectionError) {
												nextIntent
														.putExtra(
																"message",
																"NoConnectionError"
																		+ arg0.getStackTrace());
											} else if (arg0 instanceof NetworkError) {
												nextIntent
														.putExtra(
																"message",
																"NetworkError"
																		+ arg0.getStackTrace());
											} else if (arg0 instanceof ServerError) {
												nextIntent
														.putExtra(
																"message",
																"ServerError"
																		+ arg0.getStackTrace());
											} else if (arg0 instanceof VolleyError) {
												nextIntent
														.putExtra(
																"message",
																"volleyError"
																		+ arg0.toString()
																		+ "statck trace:"
																		+ arg0.getStackTrace());
											}

											LocalBroadcastManager.getInstance(
													context).sendBroadcast(
													nextIntent);
										}

									});

							RetryPolicy policy = new DefaultRetryPolicy(30000,
									DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
									DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
							jsonObjRequest.setRetryPolicy(policy);
							queue.add(jsonObjRequest);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// private class SaveCountAsync extends AsyncTask<String, Void, String> {
	// @Override
	// protected String doInBackground(String... params) {
	// jParser = new JSONParser();
	// String url = ServerURLs.URL + ServerURLs.COUNTER;
	// women_in += globalVariable.getIntervalWomenIn();
	// women_out += globalVariable.getIntervalWomenOut();
	// men_in += globalVariable.getIntervalMenIn();
	// men_out += globalVariable.getIntervalMenOut();
	// System.out.println("url is   : " + url);
	// JSONObject jsonObject2 = null;
	// // System.out.println(">>>>>>> cdt date:"+sdf.format(new
	// // Date().getDate()));
	// // System.out.println(">>>>>>> cdt hour:"+sdf.format(new
	// // Date().getHours()));
	// if (men_in > 0 || men_out > 0 || women_out > 0 || women_in > 0)
	// try {
	// if (isUnderProgress == false) {
	// JSONObject jsonObject;
	// jsonObject = new JSONObject()
	// .put("women_in", women_in)
	// .put("women_out", women_out)
	// .put("men_in", men_in)
	// .put("men_out", men_out)
	// .put("time", sdf.format(new Date()))
	// .put("business_id",
	// globalVariable.getSelectedBusiness()
	// .getId().get$oid());
	// jsonObject2 = new JSONObject().put("counter",
	// jsonObject);
	// globalVariable.setIntervalMenIn(0);
	// globalVariable.setIntervalMenOut(0);
	// globalVariable.setIntervalWomenIn(0);
	// globalVariable.setIntervalWomenOut(0);
	// globalVariable.saveSharedPreferences();
	// isUnderProgress = true;
	// jsonFromServer = jParser.getJSONFromUrlAfterHttpPost(
	// url, jsonObject2);
	// if (jsonFromServer != null) {
	// if (jsonFromServer.get("status").equals("ok")) {
	// isUnderProgress = false;
	// men_in = 0;
	// men_out = 0;
	// women_in = 0;
	// women_out = 0;
	// globalVariable.setTotalInDB(jsonFromServer
	// .getInt("total"));
	// return "ok";
	// } else
	// return "fail";
	// } else {
	// return "fail";
	// }
	// }
	// } catch (JSONException e) {
	// globalVariable.saveSharedPreferences();
	// e.printStackTrace();
	// return "fail";
	// } catch (Exception e) {
	// e.printStackTrace();
	// return "fail";
	// } catch (Throwable t) {
	// t.printStackTrace();
	// return "fail";
	// }
	//
	// Log.d("== Count ==", "Saved successfully");
	// return "ok";
	// }
	//
	// @Override
	// protected void onPostExecute(String result) {
	// if (result.equalsIgnoreCase("ok")) {
	// Toast.makeText(context,
	// "total at server:" + globalVariable.getTotalInDB(),
	// Toast.LENGTH_LONG).show();
	// } else {
	// Toast.makeText(context, "counter update failed",
	// Toast.LENGTH_LONG).show();
	// }
	//
	// }// onPostExecute
	// }// Async Task
}
