package com.example.wduwg.tiles;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.apphance.android.Log;
import com.example.wduwg.tiles.R;
import com.example.wduwg.tiles.SpecialActivity.LoadStringsAsync;
import com.google.gson.Gson;
import com.mw.wduwg.adapter.EventAdapter2;
import com.mw.wduwg.adapter.SpecialApater;
import com.mw.wduwg.model.Event;
import com.mw.wduwg.model.Special;
import com.mw.wduwg.services.CreateDialog;
import com.mw.wduwg.services.GlobalVariable;
import com.mw.wduwg.services.JSONParser;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

public class EventActivity extends Activity {
ListView eventLV;
GridView eventsGV;
Event selectedEvent;
	
	CreateDialog createDialog;
	ProgressDialog progressDialog;
	GlobalVariable globalVariable;
	View customActionBar;
	LayoutInflater inflater;
	ActionBar actionBar;
	Gson gson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_event);
		
		gson = new Gson();
		inflater =(LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		actionBar = getActionBar();
		
		globalVariable = (GlobalVariable)getApplicationContext();
//		eventLV = (ListView)findViewById(R.id.eventList);
		eventsGV = (GridView)findViewById(R.id.eventsGV);
		createDialog = new CreateDialog(this);
		progressDialog = createDialog.createProgressDialog("Loading", "Please wait while we load your events.", true, null);
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
		if(selectedEvent.getName().equalsIgnoreCase("Add New Event"))
		{
			intent.putExtra("addNew", true);
		}else
		{
		intent.putExtra("event", selectedEvent);
		}
		startActivity(intent);
		overridePendingTransition(R.anim.anim_out, R.anim.anim_in);
	}
	
	public void onDone(View v)
	{
		Intent intent = new Intent(this,BusinessDashboardActivity.class);
		intent.putExtra("isFromMain", true);
		startActivity(intent);
		overridePendingTransition(R.anim.anim_out, R.anim.anim_in);
//		LoadStringsAsync asyncTask = new LoadStringsAsync();
//		asyncTask.execute();
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
						
						JSONObject jsonobject = specialsjsonarr.getJSONObject(i);
						Event event = gson.fromJson(jsonobject.toString(), Event.class);
						event.setName(jsonobject.getString("name"));
						String startTime = globalVariable.convertDate(jsonobject.getString("start_date_time").substring(0, 16));
						if(!event.getName().equalsIgnoreCase("defaultEvent"))
						{
							
						    String endTime =  globalVariable.convertDate(jsonobject.getString("end_date_time").substring(0, 16));
							event.setDescription("Start @ "+startTime + "\nEnd @ " + endTime);
							event.setStartDate(startTime);
							event.setEndDate(endTime);
						}else
						{
							String endTime =  "daily";
							event.setDescription("Start @ "+startTime + "\nEnd @ " + endTime);
						}
						event.setImageUrl("http://fifthgroup.com/boldamericancatering/wp-content/uploads/sites/10/2014/02/boldamerican-053.jpg");
						eventList.add(event);
					}
				}
				
			} catch (Exception e) {
				Log.d("Response========", "inside catch");
				e.printStackTrace();
			}
			globalVariable.getSelectedBusiness().setEventList(eventList);
			return eventList;
		}

		@Override
		protected void onPostExecute(final List<Event> events) {
			progressDialog.dismiss();
			globalVariable.getSelectedBusiness().setEventList(events);
			globalVariable.saveSharedPreferences();
            Event newEvent = new Event();
            newEvent.setName("Add Event");
            newEvent.setImageUrl("http://images.dashtickets.co.nz/images/events/listings/event_default.jpg");
            events.add(newEvent);
			
            EventAdapter2 adapter = new EventAdapter2(EventActivity.this, events);
//            eventLV.setAdapter(adapter);
            eventsGV.setAdapter(adapter);
            eventsGV.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					Event selectedEvent = events.get(position);
//						newEvent(null);
					Intent intent = new Intent(EventActivity.this,AddEventActivity.class);
					if(selectedEvent.getName().equalsIgnoreCase("Add Event"))
					{
						intent.putExtra("addNew", true);
					}else
					{
						intent.putExtra("event", selectedEvent);
						
					}
					startActivity(intent);
					overridePendingTransition(R.anim.anim_out, R.anim.anim_in);
				}
			});
			}
	}
	
	
}
