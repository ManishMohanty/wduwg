// SchedulerCountActivity.java


package com.mw.wduwg.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.format.Time;
import android.util.Log;

import com.mw.wduwg.model.Event;
import com.parse.ParseObject;
import com.wduwg.watch.app.CountActivity;

public class SchedulerCount extends TimerTask {
       
	int men_in = 0, men_out = 0 , women_in = 0 , women_out = 0; 
//	boolean isUnderProgress = false;
	
	
	Looper looper = Looper.getMainLooper();
	private Handler mHandler = new Handler(looper);
    
	public static Event event;
	Editor editor;
	Context context;
	JSONParser jParser;
	JSONObject jsonFromServer;
	GlobalVariable globalVariable;
	SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
	

	public SchedulerCount(Context context) {
		super();
		this.context = context;
		globalVariable = (GlobalVariable) context.getApplicationContext();
	}
	public void run() {
		
		mHandler.post(new Runnable() {
            public void run() {
            	try{
            	sdf.setTimeZone(TimeZone.getTimeZone("gmt"));
        		if (!(globalVariable.getIntervalWomenIn() == 0
        				&& globalVariable.getIntervalWomenOut() == 0
        				&& globalVariable.getIntervalMenIn() == 0 && globalVariable.getIntervalMenOut() == 0)) {
        			System.out.println(">>>>>>>> inside run count");
        			if(globalVariable.isInternet() == true)
        			{
        				System.out.println(">>>>>>> last count before");
        				SaveCountAsync async = new SaveCountAsync();
        				async.execute(new String[] { "dfs" });
        				System.out.println(">>>>>>> last count after");
        			}
        		} else {
        			Log.d("== Count ==", "Everything is ZERO");
        		}
            	}catch(Exception e)
            	{
            		e.printStackTrace();
            	}
            }
        });
	}

	private class SaveCountAsync extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			jParser = new JSONParser();
			String url = ServerURLs.URL + ServerURLs.COUNTER;
			women_in +=  globalVariable.getIntervalWomenIn();
			women_out += globalVariable.getIntervalWomenOut();
			men_in += globalVariable.getIntervalMenIn();
			men_out += globalVariable.getIntervalMenOut();
			System.out.println("url is   : " + url);
			JSONObject jsonObject2 = null;
//			System.out.println(">>>>>>> cdt date:"+sdf.format(new Date().getDate()));
//			System.out.println(">>>>>>> cdt hour:"+sdf.format(new Date().getHours()));
			if(men_in > 0 || men_out > 0 || women_out > 0 || women_in > 0 )
			try {
//				if(isUnderProgress == false){
				JSONObject jsonObject;
				jsonObject = new JSONObject()
						.put("women_in", women_in)
						.put("women_out", women_out)
						.put("men_in", men_in)
						.put("men_out", men_out)
						.put("time", sdf.format(new Date()))
						.put("business_id",
								globalVariable.getSelectedBusiness().getId().get$oid());
				jsonObject2 = new JSONObject().put("counter", jsonObject);
				globalVariable.setIntervalMenIn(0);
				globalVariable.setIntervalMenOut(0);
				globalVariable.setIntervalWomenIn(0);
				globalVariable.setIntervalWomenOut(0);
	            globalVariable.saveSharedPreferences();
//	            isUnderProgress = true;
	            jsonFromServer = jParser.getJSONFromUrlAfterHttpPost(url,
	            		jsonObject2);
	            if(jsonFromServer.get("status").equals("ok"))
	            {
//	            	isUnderProgress = false;
	            	men_in = 0;
	            	men_out = 0;
	            	women_in = 0;
	            	women_out = 0;
	            	globalVariable.setTotalInDB(jsonFromServer.getInt("total"));
	            }
//			}
			} catch (JSONException e) {
				globalVariable.saveSharedPreferences();
				e.printStackTrace();
			}

			
			Log.d("== Count ==", "Saved successfully");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			
		}// onPostExecute
	}// Async Task
}
