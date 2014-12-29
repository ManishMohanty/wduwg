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
import android.os.AsyncTask;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.wduwg.tiles.CountActivity;
import com.mw.wduwg.model.Event;
import com.parse.ParseObject;

public class SchedulerCount extends TimerTask {

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
				&& globalVariable.intervalMenIn == 0 && globalVariable.intervalMenOut == 0 && globalVariable.isInternet())) {
				SaveCountAsync async = new SaveCountAsync();
				async.execute(new String[] { "dfs" });
		} else {
			Log.d("== Count ==", "Everything is ZERO");
		}
	}

	private class SaveCountAsync extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			jParser = new JSONParser();
			String url = ServerURLs.URL + ServerURLs.COUNTER;
			JSONObject jsonObject2 = null;
			try {
				JSONObject jsonObject;
				jsonObject = new JSONObject()
						.put("women_in", globalVariable.getIntervalWomenIn())
						.put("women_out", globalVariable.getIntervalWomenOut())
						.put("men_in", globalVariable.getIntervalMenIn())
						.put("men_out", globalVariable.getIntervalMenOut())
						.put("time", sdf.format(new Date()))
						.put("business_id",
								globalVariable.getSelectedBusiness().getId().get$oid());
				jsonObject2 = new JSONObject().put("counter", jsonObject);
				globalVariable.setIntervalMenIn(0);
				globalVariable.setIntervalMenOut(0);
				globalVariable.setIntervalWomenIn(0);
				globalVariable.setIntervalWomenOut(0);
	            globalVariable.saveSharedPreferences();

			} catch (JSONException e) {
				e.printStackTrace();
			}

			jsonFromServer = jParser.getJSONFromUrlAfterHttpPost(url,
					jsonObject2);
			
			Log.d("== Count ==", "Saved successfully");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
		}
	}
}
