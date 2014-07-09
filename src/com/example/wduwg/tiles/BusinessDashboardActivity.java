package com.example.wduwg.tiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mw.wduwg.adapter.EventAdapter2;
import com.mw.wduwg.adapter.SpecialApater;
import com.mw.wduwg.model.Event;
import com.mw.wduwg.model.Special;
import com.mw.wduwg.services.CreateDialog;
import com.mw.wduwg.services.GlobalVariable;
import com.mw.wduwg.services.JSONParser;
import com.mw.wduwg.services.SchedulerCount;


public class BusinessDashboardActivity extends Activity {

	
	Typeface typefaceBold,typefaceLight;
	GlobalVariable globalVariable;
	List<Event> eventList ;
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		commonMethod();
		super.onResume();
	}

	List<Special> specialList;
	ListView eventListView, SpecialListView;
	TextView eventLabel ,specialLabel , visitorLabel;
	GridView eventgridView,specialgridView;
	CreateDialog createDialog ;
	AlertDialog.Builder alertDialogBuilder;
	AlertDialog alertDialog;
	ProgressDialog progressDialog;
	int men_in=0,women_in=0,men_out=0,women_out=0;
	
	TextView totalCount,menCount,womenCount,totalVisitors;
	Gson gson;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_business_dashboard);
		
		gson = new Gson();
		
		typefaceBold = Typeface.createFromAsset(getAssets(), "Fonts/OpenSans-Bold.ttf");
		typefaceLight = Typeface.createFromAsset(getAssets(), "Fonts/OpenSans-Light.ttf");
		 totalCount = (TextView)findViewById(R.id.totalCount);
		 menCount = (TextView)findViewById(R.id.menCount);
		 womenCount = (TextView) findViewById(R.id.womenCount);
		 totalVisitors = (TextView)findViewById(R.id.totalVisitor);
		 
		 totalCount.setTypeface(typefaceLight);
		 menCount.setTypeface(typefaceLight);
		 womenCount.setTypeface(typefaceLight);
		 totalVisitors.setTypeface(typefaceLight);
		 
		 
		 eventLabel = (TextView)findViewById(R.id.events_label);
		 eventLabel.setTypeface(typefaceBold);
		 specialLabel = (TextView)findViewById(R.id.special_labels);
		 specialLabel.setTypeface(typefaceBold);
		 visitorLabel = (TextView)findViewById(R.id.visitorLabel);
		 visitorLabel.setTypeface(typefaceBold);
		 
//		eventListView = (ListView) findViewById(R.id.evntlistView);
		 eventgridView = (GridView)findViewById(R.id.eventGV);
//		SpecialListView = (ListView)findViewById(R.id.speciallistView);
		 specialgridView = (GridView)findViewById(R.id.specialGV);
		globalVariable = (GlobalVariable)getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
       View v= (View) inflater.inflate(R.layout.custom_action_bar, null);
       TextView title = (TextView)v.findViewById(R.id.action_bar_TV);
       title.setTypeface(typefaceBold);
       title.setTextSize(19);
       title.setText(globalVariable.getSelectedBusiness().getName());
       ImageButton ib =(ImageButton) v.findViewById(R.id.doneButton);
       ib.setVisibility(View.GONE);
       ActionBar ab = getActionBar();
       ab.setDisplayShowCustomEnabled(true);
       ab.setCustomView(v);
//       commomMethod();
        createDialog = new CreateDialog(this);
		progressDialog = createDialog.createProgressDialog("Loading", "wait for a while", true, null);
		progressDialog.show();
		LoadStringsAsync asyncTask = new LoadStringsAsync();
		asyncTask.execute();
	}
	
	private void commonMethod()
	{
		List<Special> specials = new ArrayList<Special>();
		List<Event> events = new ArrayList<Event>();
		if(globalVariable.getSelectedBusiness().getSpecials().size()>1)
		    {
		      specials = globalVariable.getSelectedBusiness().getSpecials().subList(0, 2);
			}else
			{
			    specials = globalVariable.getSelectedBusiness().getSpecials();
				if(specials.size()==0 || !specials.get(specials.size() - 1).getName().equalsIgnoreCase("Add Special"))
				{
				Special newSpecial = new Special();
				newSpecial.setName("Add Special");
				newSpecial.setImageUrl("https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcTkopjYDLX80cyPjXWkx8Cb0eoKyW_N6rGn7p6JlhYYghXhV_ot");
				specials.add(newSpecial);
				}
			}
           SpecialApater adapter = new SpecialApater(BusinessDashboardActivity.this, specials);
           specialgridView.setAdapter(adapter);
           System.out.println(">>>>>>> specials size:"+globalVariable.getSelectedBusiness().getSpecials().size());
           specialgridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					Special special = globalVariable.getSelectedBusiness().getSpecials().get(position);
					Intent intent = new Intent(BusinessDashboardActivity.this,AddSpecialActivity.class);
					if(special.getName().equalsIgnoreCase("Add Special"))
					{
						intent.putExtra("newAdd", true);
						globalVariable.getSelectedBusiness().getSpecials().remove(position);
					}
					intent.putExtra("special", special);
					startActivity(intent);
				}
			
           
           });
           if(globalVariable.getSelectedBusiness().getEventList().size() >1)
           {
             events = globalVariable.getSelectedBusiness().getEventList().subList(0, 2);
           }else
           {
        	   events = globalVariable.getSelectedBusiness().getEventList();
        	   if(events.size() == 0 || !events.get(events.size()-1).getName().equalsIgnoreCase("Add Event"))
        	   {
        		   Event newEvent = new Event();
        		   newEvent.setName("Add Event");
        		   newEvent.setImageUrl("http://images.dashtickets.co.nz/images/events/listings/event_default.jpg");
        		   events.add(newEvent);
        	   }
           }
           EventAdapter2 adapter1 = new EventAdapter2(BusinessDashboardActivity.this, events);
           eventgridView.setAdapter(adapter1);
           eventgridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					Event event1 = globalVariable.getSelectedBusiness().getEventList().get(position);
					Intent intent = new Intent(BusinessDashboardActivity.this,AddEventActivity.class);
					if(event1.getName().equalsIgnoreCase("Add Event"))
					{
						intent.putExtra("addNew", true);
						globalVariable.getSelectedBusiness().getEventList().remove(position);
					}
					intent.putExtra("event",event1);
					startActivity(intent);
				}
			
           });
	}
	
	public void allEvents(View v)
	{
		Intent nextIntent = new Intent(this,EventActivity.class);
		startActivity(nextIntent);
		overridePendingTransition(R.anim.anim_out, R.anim.anim_in);
	}
	
	public void allSpecials(View v)
	{
		Intent nextIntent = new Intent(this,SpecialActivity.class);
		startActivity(nextIntent);
		overridePendingTransition(R.anim.anim_out, R.anim.anim_in);
	}
	
	public void viewDetail(View v)
	{
		Intent nextIntent = new Intent(this,ReportActualActvivity.class);
		startActivity(nextIntent);
		overridePendingTransition(R.anim.anim_out, R.anim.anim_in);
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
				JSONParser jsonparser = new JSONParser(BusinessDashboardActivity.this);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("business_id",globalVariable.getSelectedBusiness().getId().get$oid() ));
				JSONArray specialsjsonarr = jsonparser.getJSONArrayFromUrlAfterHttpGet("http://dcounter.herokuapp.com/events.json",params);
				System.out.println(">>>>>>> inside backgound for event");
				System.out.println(">>>>>>> response length:"+specialsjsonarr.length());
				if(specialsjsonarr.length()>0)
				{
					
					for(int i = 0; i< specialsjsonarr.length(); i++)
					{
						Event event ;
						JSONObject jsonobject = specialsjsonarr.getJSONObject(i);
							System.out.println(">>>>>>> json:"+jsonobject.toString());
						 event = gson.fromJson(jsonobject.toString(), Event.class);
		     			event.setName(jsonobject.getString("name"));
		     			System.out.println(">>>>>>> event id:"+event.getId().get$oid());
						String startTime = globalVariable.timeFormat(jsonobject.getString("start_date_time").replace('T', ',').substring(0, (jsonobject.getString("start_date_time").length()-8)));
						event.setStartDate(startTime);
						if(!event.getName().equalsIgnoreCase("defaultEvent"))
						{
							System.out.println(">>>>>>> endDate"+jsonobject.getString("end_date_time"));
						    String endTime =  globalVariable.timeFormat(jsonobject.getString("end_date_time").replace('T', ',').substring(0, jsonobject.getString("end_date_time").length()-8));
							event.setDescription("Start @ "+startTime+"\nEnd @ "+ endTime);
							event.setEndDate(endTime);
						}else
						{
							String endTime =  "daily";
							event.setDescription("Start @ "+startTime + "\nEnd @ " + endTime);
							event.setEndDate(endTime);
						}
						
						event.setImageUrl("http://fifthgroup.com/boldamericancatering/wp-content/uploads/sites/10/2014/02/boldamerican-053.jpg");
						eventList.add(event);
					} // end of for
				} // end of if
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return eventList;
		}

		@Override
		protected void onPostExecute(final List<Event> events) {
//			progressDialog.dismiss();
			System.out.println(">>>>>>> inside event postexecute");
            System.out.print(">>>>>>> list-size:"+events.size());
			globalVariable.getSelectedBusiness().setEventList(events);
			LoadStringsAsync1 asyncTask = new LoadStringsAsync1();
			asyncTask.execute();
			
		}
	}
	
	
	
	
	// special 
	public class LoadStringsAsync1 extends AsyncTask<Void, Void, List<Special>> {

		// new thread for imagedownloading res
		Bitmap bitmap;
		JSONArray photos, array;
		JSONObject photo;
		String name, formatedAddress;

		List<Special> specialList = new ArrayList<Special>();

		public LoadStringsAsync1() {

		}

		@Override
		protected List<Special> doInBackground(Void... arg0) {
			try {
				JSONParser jsonparser = new JSONParser(BusinessDashboardActivity.this);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("business_id",globalVariable.getSelectedBusiness().getId().get$oid() ));
				JSONArray specialsjsonarr = jsonparser.getJSONArrayFromUrlAfterHttpGet("http://dcounter.herokuapp.com/specials.json",params);
				if(specialsjsonarr.length()>0)
				{
					
					for(int i = 0; i< specialsjsonarr.length(); i++)
					{
						JSONObject jsonobject = specialsjsonarr.getJSONObject(i);
						Special special = gson.fromJson(jsonobject.toString(), Special.class);
						special.setName(jsonobject.getString("name"));
						String starts_from = globalVariable.timeFormat(jsonobject.getString("start_date_time").replace('T', ',').substring(0, (jsonobject.getString("start_date_time").length()-8)));
						String valid_upto =  globalVariable.timeFormat(jsonobject.getString("end_date_time").replace('T', ',').substring(0, jsonobject.getString("end_date_time").length()-8));
						special.setStartDate(starts_from);
						special.setEndDate(valid_upto);
						special.setDescription("Start @ "+starts_from + "\nEnd @ " + valid_upto);
						special.setImageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSg92ThBRn7ux2rLEWxZUIKLK-rmMbBBgr6x9ugUZsYUocytf4z");
						specialList.add(special);
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return specialList;
		}

		@Override
		protected void onPostExecute(final List<Special> specials) {
			System.out.println(">>>>>>> inside special postexecute");
			globalVariable.getSelectedBusiness().setSpecials(specials);
			globalVariable.saveSharedPreferences();
			LoadStringsAsync2 asyncTask = new LoadStringsAsync2();
			asyncTask.execute();
		}
	}
	
	
	
	//counter
	public class LoadStringsAsync2 extends AsyncTask<Void, Void, Void> {

		// new thread for imagedownloading res
		public LoadStringsAsync2() {

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				JSONParser jsonparser = new JSONParser(BusinessDashboardActivity.this);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("business_id",globalVariable.getSelectedBusiness().getId().get$oid() ));
				JSONArray specialsjsonarr = jsonparser.getJSONArrayFromUrlAfterHttpGet("http://dcounter.herokuapp.com/counters/today_counter.json",params);
				if(specialsjsonarr.length()>0)
				{
					for(int i = 0; i< specialsjsonarr.length(); i++)
					{
						JSONObject jsonobject = specialsjsonarr.getJSONObject(i);
						men_in += Integer.parseInt(jsonobject.getString("men_in"));
						men_out += Integer.parseInt(jsonobject.getString("men_out"));
						women_in += Integer.parseInt(jsonobject.getString("women_in"));
						women_out += Integer.parseInt(jsonobject.getString("women_out"));
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void arg) {
			System.out.println(">>>>>>> inside counter postexecute");
			progressDialog.dismiss();
			System.out.println(">>>>>>> progress dialog dismiss");
			totalCount.setText("Total :"+((men_in+women_in)-(men_out+women_out)));
			menCount.setText("Men :"+(men_in-men_out));
			womenCount.setText("Women :"+(women_in-women_out));
			totalVisitors.setText("Total Visitors Today :"+(men_in+women_in));
			commonMethod();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.overflow_options_menu, menu);
		CharSequence rawTitle = "Logout";
		CharSequence delink = "Delink";
		menu.findItem(R.id.menu_logout).setTitleCondensed(rawTitle);
		menu.findItem(R.id.menu_delink).setTitleCondensed(delink);

		SpannableString logoutstr = new SpannableString(rawTitle);
		SpannableString delinkstr = new SpannableString(delink);
		delinkstr.setSpan(typefaceBold, 0, delinkstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		menu.findItem(R.id.menu_delink).setTitle(delinkstr);
		logoutstr.setSpan(typefaceBold, 0, logoutstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		menu.findItem(R.id.menu_logout).setTitle(logoutstr);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuItem logoutItem = menu.findItem(R.id.menu_logout);
		if(globalVariable.getFb_access_token() !=null)
		{
			logoutItem.setEnabled(true);
		}else
		{
			logoutItem.setEnabled(false);
		}
		MenuItem delinkItem = menu.findItem(R.id.menu_delink);
		if(globalVariable.getSelectedBusiness()!=null)
		{
			delinkItem.setEnabled(true);
		}else
		{
			delinkItem.setEnabled(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_logout:
			if (LoginFacebookActivity.timer != null)
				LoginFacebookActivity.timer.cancel();
			globalVariable.getCustomer().setPages(null);
			globalVariable.setFb_access_expire(0);
			globalVariable.setFb_access_token(null);
			globalVariable.setSelectedBusiness(null);
			globalVariable.setSelectedEvent(null);
			globalVariable.setMenIn(0);
			globalVariable.setMenOut(0);
			globalVariable.setWomenIn(0);
			globalVariable.setWomenOut(0);
			globalVariable.saveSharedPreferences();
			globalVariable.saveSharedPreferences();
			Toast.makeText(this, "Logged out from FB.", Toast.LENGTH_SHORT).show();
			Intent nextIntent = new Intent(BusinessDashboardActivity.this,SpalshFirstActivity.class);
			nextIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(nextIntent);
		case R.id.menu_delink:
			
			alertDialogBuilder = createDialog
			.createAlertDialog(
					"Delink",
					"Do you wish to delink business with device ?",
					false);
	alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			alertDialog.dismiss();
			SchedulerCount scheduledTask = new SchedulerCount(BusinessDashboardActivity.this);
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(scheduledTask, 1000, 10000);
			scheduledTask.run();
			SchedulerCount.event = globalVariable.getSelectedEvent();
			timer.cancel();
			globalVariable.setSelectedBusiness(null);
			globalVariable.setSelectedEvent(null);
			globalVariable.setMenIn(0);
			globalVariable.setMenOut(0);
			globalVariable.setWomenIn(0);
			globalVariable.setWomenOut(0);
			globalVariable.saveSharedPreferences();
			Intent nextIntent = new Intent(BusinessDashboardActivity.this, BusinessOfUserActivity.class);
			startActivity(nextIntent);
			overridePendingTransition(R.anim.anim_out, R.anim.anim_in);
		}
	});
	alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			alertDialog.dismiss();
		}
	});
	alertDialog = alertDialogBuilder.create();
	alertDialog.show();
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
}
