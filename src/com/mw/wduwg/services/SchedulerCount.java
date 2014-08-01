// SchedulerCountActivity.java


package com.mw.wduwg.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mw.wduwg.model.Event;
import com.parse.ParseObject;
import com.wduwg.counter.CountActivity;

public class SchedulerCount extends TimerTask {
	int men_in=0,men_out = 0, women_in = 0, women_out = 0;
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
	}

	public void run() {
		globalVariable = (GlobalVariable) context.getApplicationContext();
        sdf.setTimeZone(TimeZone.getTimeZone("gmt"));
		if (!(globalVariable.intervalWomenIn == 0
				&& globalVariable.intervalWomenOut == 0
				&& globalVariable.intervalMenIn == 0 && globalVariable.intervalMenOut == 0 ) ) {
			if(globalVariable.isInternet()== true)
			{
					SaveCountAsync async = new SaveCountAsync();
					async.execute(new String[] { "dfs" });
			}
		} else {
			Log.d("== Count ==", "Everything is ZERO");
		}
	}

	private class SaveCountAsync extends AsyncTask<String, Void, Void> {
		
		@Override
		protected Void doInBackground(String... params) {
			jParser = new JSONParser();
			men_in += globalVariable.getIntervalMenIn();
			men_out += globalVariable.getIntervalMenOut();
			women_in += globalVariable.getIntervalWomenIn();
			women_out += globalVariable.getIntervalWomenOut();
			String url = ServerURLs.URL + ServerURLs.COUNTER;
			System.out.println("url is   : " + url);
			JSONObject jsonObject2 = null;
			System.out.println(">>>>>>> time:"+sdf.format(new Date()));
			try {
				JSONObject jsonObject;
				jsonObject = new JSONObject()
						.put("women_in", women_in)
						.put("women_out", women_out)
						.put("men_in",men_in )
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
	            
	            jsonFromServer = jParser.getJSONFromUrlAfterHttpPost(url,
						jsonObject2);
				System.out.println(">>>>>>> counter response:"+jsonFromServer);
				if(jsonFromServer.get("status").equals("ok"))
				{
					men_in = 0;
					men_out = 0;
					women_in = 0;
					women_out = 0;
				}

			} catch (JSONException e) {
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
