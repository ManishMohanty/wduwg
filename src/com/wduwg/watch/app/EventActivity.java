package com.wduwg.watch.app;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.apphance.android.Log;
import com.mw.wduwg.adapter.EventAdapter2;
import com.mw.wduwg.adapter.SpecialApater;
import com.mw.wduwg.model.Event;
import com.mw.wduwg.model.Special;
import com.mw.wduwg.services.CreateDialog;
import com.mw.wduwg.services.GlobalVariable;
import com.mw.wduwg.services.JSONParser;
import com.wduwg.watch.app.SpecialActivity.LoadStringsAsync;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class EventActivity extends Activity {
ListView eventLV;
	
	CreateDialog createDialog;
	ProgressDialog progressDialog;
	GlobalVariable globalVariable;
	View customActionBar;
	LayoutInflater inflater;
	ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_event);
		inflater =(LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		actionBar = getActionBar();
		
		globalVariable = (GlobalVariable)getApplicationContext();
		eventLV = (ListView)findViewById(R.id.eventList);
		createDialog = new CreateDialog(this);
		progressDialog = createDialog.createProgressDialog("Loading", "wait for a while", true, null);
		progressDialog.show();
		LoadStringsAsync asyncTask = new LoadStringsAsync();
		asyncTask.execute();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		customActionBar = inflater.inflate(R.layout.custom_action_bar, null);
		TextView title = (TextView)customActionBar.findViewById(R.id.action_bar_TV);
		title.setText("Events");
		title.setTextSize(19);
		Typeface font = Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Bold.ttf");
		title.setTypeface(font);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(customActionBar);
	}

	public void newEvent(View v)
	{
		Intent intent = new Intent(this,AddEventActivity.class);
		intent.putExtra("from_event", true);
		startActivity(intent);
		overridePendingTransition(R.anim.anim_out, R.anim.anim_in);
	}
	
	public void onDone(View v)
	{
		Intent intent = new Intent(this,BusinessHomePageActivity.class);
		intent.putExtra("isFromMain", true);
		startActivity(intent);
		overridePendingTransition(R.anim.anim_out, R.anim.anim_in);
		LoadStringsAsync asyncTask = new LoadStringsAsync();
		asyncTask.execute();
	}
	
	public class LoadStringsAsync extends AsyncTask<Void, Void, List<Event>> {

		// new thread for imagedownloading res
		Bitmap bitmap;
		JSONArray photos, array;
		JSONObject photo;
		String name, formatedAddress;

		List<Event> eventList = new ArrayList<Event>();

		public LoadStringsAsync() {

		}

		@Override
		protected List<Event> doInBackground(Void... arg0) {
			try {
				System.out.println(">>>>>>> inside backgound");
				JSONParser jsonparser = new JSONParser(EventActivity.this);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("business_id",globalVariable.getSelectedBusiness().getId().get$oid() ));
				JSONArray specialsjsonarr = jsonparser.getJSONArrayFromUrlAfterHttpGet("http://dcounter.herokuapp.com/events.json",params);
				if(specialsjsonarr.length()>0)
				{
					for(int i = 0; i< specialsjsonarr.length(); i++)
					{
						Event event = new Event();
						JSONObject jsonobject = specialsjsonarr.getJSONObject(i);
						event.setName(jsonobject.getString("name"));
						String startTime = jsonobject.getString("start_date_time").replace('T', ',').substring(0, (jsonobject.getString("start_date_time").length()-13));
						if(!event.getName().equalsIgnoreCase("defaultEvent"))
						{
						    String endTime =  jsonobject.getString("end_date_time").replace('T', ',').substring(0, jsonobject.getString("end_date_time").length()-13);
							event.setDescription("Start Time "+startTime + "\nEnd Time " + endTime);
						}else
						{
							String endTime =  "daily";
							event.setDescription("Start Time "+startTime + "\nEnd Time " + endTime);
						}
						event.setImageUrl("http://fifthgroup.com/boldamericancatering/wp-content/uploads/sites/10/2014/02/boldamerican-053.jpg");
						eventList.add(event);
					}
				}
				
			} catch (Exception e) {
				Log.d("Response========", "inside catch");
				e.printStackTrace();
			}
			return eventList;
		}

		@Override
		protected void onPostExecute(final List<Event> events) {
			progressDialog.dismiss();
			if(events != null && events.size() > 0)
			{
            System.out.print(">>>>>>> list-size:"+events.size()+ "===========data"+events.get(0).getName());
            for(int i=0;i<events.size();i++)
            	System.out.println(">>>>>>> "+events.get(i).getName());
			globalVariable.getSelectedBusiness().setEventList(events);
            EventAdapter2 adapter = new EventAdapter2(EventActivity.this, events);
            eventLV.setAdapter(adapter);
			}
		}
	}
	
	
}
