package com.example.wduwg.tiles;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.gsm.SmsManager;
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
	private  Timer autoUpdate;
	private static final int SETTING = 93;
	SharedPreferences sharedPreference;
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(autoUpdate != null)
		{
			autoUpdate.cancel();
			autoUpdate.purge();
			autoUpdate = new Timer();
			  autoUpdate.schedule(new TimerTask() {
			   @Override
			   public void run() {
			    runOnUiThread(new Runnable() {
			     public void run() {
			    	 LoadStringsAsync2 asyncTask = new LoadStringsAsync2();
						asyncTask.execute();
			     }
			    });
			   }
			  }, 0, 120000);
		}
		if(globalVariable.getSelectedBusiness()!= null)
		{
			commonMethod();
		}
		super.onResume();
	}

	List<Special> specialList;
	ListView eventListView, SpecialListView;
	TextView eventLabel ,specialLabel , visitorLabel;
	GridView eventgridView,specialgridView;
	CreateDialog createDialog ;
	AlertDialog.Builder alertDialogBuilder;
	AlertDialog alertDialog,alertDialog1;
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
		 sharedPreference = PreferenceManager.getDefaultSharedPreferences(BusinessDashboardActivity.this);
		typefaceBold = Typeface.createFromAsset(getAssets(), "Fonts/OpenSans-Bold.ttf");
		typefaceLight = Typeface.createFromAsset(getAssets(), "Fonts/OpenSans-Light.ttf");
		 
		 eventLabel = (TextView)findViewById(R.id.events_label);
		 eventLabel.setTypeface(typefaceBold);
		 specialLabel = (TextView)findViewById(R.id.special_labels);
		 specialLabel.setTypeface(typefaceBold);
		 visitorLabel = (TextView)findViewById(R.id.visitorLabel);
		 visitorLabel.setTypeface(typefaceBold);
		 
		 totalCount = (TextView)findViewById(R.id.totalCount);
		 womenCount = (TextView) findViewById(R.id.womenCount);
		 totalVisitors = (TextView)findViewById(R.id.totalVisitor);
		 menCount = (TextView)findViewById(R.id.menCount);
		 
		 totalCount.setTypeface(typefaceLight);
		 menCount.setTypeface(typefaceLight);
		 womenCount.setTypeface(typefaceLight);
		 totalVisitors.setTypeface(typefaceLight);
		 
		 eventgridView = (GridView)findViewById(R.id.eventGV);
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
		progressDialog = createDialog.createProgressDialog("Loading", "Please wait while we fetch your data.", true, null);
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
				if(specials.size()==0 || !specials.get(specials.size() - 1).getName().equalsIgnoreCase("Add a Special"))
				{
				Special newSpecial = new Special();
				newSpecial.setName("Add a Special");
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
					if(special.getName().equalsIgnoreCase("Add a Special"))
					{
						if(autoUpdate != null)
							autoUpdate.cancel();
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
        	   if(events.size() == 0 || !events.get(events.size()-1).getName().equalsIgnoreCase("Add an Event"))
        	   {
        		   
        		   Event newEvent = new Event();
        		   newEvent.setName("Add an Event");
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
					if(event1.getName().equalsIgnoreCase("Add an Event"))
					{
						if(autoUpdate != null)
							autoUpdate.cancel();
						intent.putExtra("addNew", true);
						globalVariable.getSelectedBusiness().getEventList().remove(position);
						System.out.println(">>>>> ******************************************");
					}
					intent.putExtra("event",event1);
					startActivity(intent);
				}
			
           });
	}
	
	public void allEvents(View v)
	{
		if(autoUpdate != null)
		autoUpdate.cancel();
		Intent nextIntent = new Intent(this,EventActivity.class);
		startActivity(nextIntent);
		overridePendingTransition(R.anim.anim_out, R.anim.anim_in);
	}
	
	public void allSpecials(View v)
	{
		if(autoUpdate != null)
		autoUpdate.cancel();
		Intent nextIntent = new Intent(this,SpecialActivity.class);
		startActivity(nextIntent);
		overridePendingTransition(R.anim.anim_out, R.anim.anim_in);
	}
	
	public void viewDetail(View v)
	{
		if(autoUpdate != null)
		autoUpdate.cancel();
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
				System.out.println(">>>>>>> inside background for event");
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
						String startTime = globalVariable.convertDate(jsonobject.getString("start_date_time").substring(0, 16));
						event.setStartDate(startTime);
						if(!event.getName().equalsIgnoreCase("defaultEvent"))
						{
							System.out.println(">>>>>>> endDate"+jsonobject.getString("end_date_time"));
						    String endTime =  globalVariable.convertDate(jsonobject.getString("end_date_time").substring(0, 16));
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
			System.out.println(">>> event list size:"+eventList.size());
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
						String starts_from = globalVariable.convertDate(jsonobject.getString("start_date_time").substring(0, 16));
						String valid_upto =  globalVariable.convertDate(jsonobject.getString("end_date_time").substring(0, 16));
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
					int visitors_total = 0;
					men_in = 0;
					men_out = 0;
					women_in = 0;
					women_out = 0;
					for(int i = 0; i< specialsjsonarr.length(); i++)
					{
						JSONObject jsonobject = specialsjsonarr.getJSONObject(i);
						men_in += Integer.parseInt(jsonobject.getString("men_in"));
						men_out += Integer.parseInt(jsonobject.getString("men_out"));
						women_in += Integer.parseInt(jsonobject.getString("women_in"));
						women_out += Integer.parseInt(jsonobject.getString("women_out"));
					}
					visitors_total = men_in + women_in;
					System.out.println(">>>>>>> visitors_total:"+visitors_total);
					globalVariable.getSelectedBusiness().setMenIn(men_in);
					globalVariable.getSelectedBusiness().setMenOut(men_out);
					globalVariable.getSelectedBusiness().setWomenIn(women_in);
					globalVariable.getSelectedBusiness().setWomenOut(women_out);
					if( sharedPreference.getBoolean("prefMessageSwitch", false) == true && visitors_total >= globalVariable.getMessage_frequency()&& globalVariable.getMessage_frequency() > 0)
					{
						int next_frequency = Integer.parseInt(sharedPreference.getString("prefNotificationFrequency", "0")) * (visitors_total / Integer.parseInt(sharedPreference.getString("prefNotificationFrequency", "0")));
						globalVariable.setMessage_frequency(next_frequency + Integer.parseInt(sharedPreference.getString("prefNotificationFrequency", "0")));
						globalVariable.saveSharedPreferences();
						try{
							SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a");
							df.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
							String strDate = df.format(new Date());
							SmsManager smsManager = SmsManager.getDefault();
							smsManager.sendTextMessage(
									sharedPreference.getString("prefPhone", "09019129275"), "wduwg",
									"Total Attendance at \""+globalVariable.getSelectedBusiness().getName()+"\" is " + (men_in+women_in - men_out - women_out)+ " at "+strDate,
									null, null);
						}catch(Exception e)
						{
							e.printStackTrace();
						}
						
					}
					
				}else
				{
					men_in = 0;
					men_out = 0;
					women_in = 0;
					women_out = 0;
					globalVariable.getSelectedBusiness().setMenIn(men_in);
					globalVariable.getSelectedBusiness().setMenOut(men_out);
					globalVariable.getSelectedBusiness().setWomenIn(women_in);
					globalVariable.getSelectedBusiness().setWomenOut(women_out);
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
			totalCount.setText("Total :"+((globalVariable.getSelectedBusiness().getMenIn()+globalVariable.getSelectedBusiness().getWomenIn())-(globalVariable.getSelectedBusiness().getMenOut()+globalVariable.getSelectedBusiness().getWomenOut())));
			menCount.setText("Men :"+(globalVariable.getSelectedBusiness().getMenIn()-globalVariable.getSelectedBusiness().getMenOut()));
			womenCount.setText("Women :"+(globalVariable.getSelectedBusiness().getWomenIn()-globalVariable.getSelectedBusiness().getWomenOut()));
			totalVisitors.setText("Total Visitors Today :"+(globalVariable.getSelectedBusiness().getMenIn()+globalVariable.getSelectedBusiness().getWomenIn()));
			 men_in =0;
	    	 men_out =0;
	    	 women_in = 0;
	    	 women_out = 0;
	    	 if(globalVariable.getSelectedBusiness() != null)
			commonMethod();
	    	 if(autoUpdate == null){
	    			autoUpdate = new Timer();
	    			  autoUpdate.schedule(new TimerTask() {
	    			   @Override
	    			   public void run() {
	    			    runOnUiThread(new Runnable() {
	    			     public void run() {
	    			    	 LoadStringsAsync2 asyncTask = new LoadStringsAsync2();
	    						asyncTask.execute();
	    			     }
	    			    });
	    			   }
	    			  }, 0, 120000);
	    			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.overflow_options_menu, menu);
		CharSequence rawTitle = "Logout";
		CharSequence delink = "Delink";
//		CharSequence delete = "Delete";
		CharSequence setting = "Settings";
		
		menu.findItem(R.id.menu_logout).setTitleCondensed(rawTitle);
		menu.findItem(R.id.menu_delink).setTitleCondensed(delink);
//		menu.findItem(R.id.menu_delete).setTitleCondensed(delete);
		menu.findItem(R.id.menu_settings).setTitleCondensed(setting);

		SpannableString logoutstr = new SpannableString(rawTitle);
		SpannableString delinkstr = new SpannableString(delink);
//		SpannableString deletestr = new SpannableString(delete);
		SpannableString settingstr = new SpannableString(setting);
		delinkstr.setSpan(typefaceBold, 0, delinkstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		menu.findItem(R.id.menu_delink).setTitle(delinkstr);
		logoutstr.setSpan(typefaceBold, 0, logoutstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		menu.findItem(R.id.menu_logout).setTitle(logoutstr);
//		deletestr.setSpan(typefaceBold, 0, deletestr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		menu.findItem(R.id.menu_delete).setTitle(deletestr);
		settingstr.setSpan(typefaceBold, 0, settingstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		menu.findItem(R.id.menu_settings).setTitle(settingstr);
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
			if(autoUpdate != null){
				autoUpdate.cancel();
				autoUpdate.purge();
				autoUpdate = null;
			}
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
			globalVariable.fbPostOff();
			globalVariable.saveSharedPreferences();
			globalVariable.saveSharedPreferences();
			if(AppSettingsActivity.timer != null)
			{
				AppSettingsActivity.timer.cancel();
			}
			Toast.makeText(this, "Logged out from FB.", Toast.LENGTH_SHORT).show();
			Intent nextIntent = new Intent(BusinessDashboardActivity.this,SpalshFirstActivity.class);
			nextIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(nextIntent);
			return true;
//		case R.id.menu_delete:
//			System.out.println(">>>>>>> dlete option");
//			if(autoUpdate != null)
//			autoUpdate.cancel();
//			alertDialogBuilder = createDialog
//			.createAlertDialog(
//					"Delete",
//					"Do you wish to delete business permanentaly ?",
//					false);
//	alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//		
//		@Override
//		public void onClick(DialogInterface dialog, int which) {
//			// TODO Auto-generated method stub
//			
//			
//			// call async task to delete business
//			if(autoUpdate != null){
//				autoUpdate.cancel();
//				autoUpdate.purge();
//				autoUpdate = null;
//				globalVariable.fbPostOff();
//				change_setting_preference();
//			}
//			alertDialog.dismiss();
//			progressDialog = createDialog.createProgressDialog("Deleting",
//					"Please wait while we delete this business from your login.", true, null);
//			progressDialog.show();
//			DeleteAsyncTask asyncTask = new DeleteAsyncTask();
//			asyncTask.execute();
//			
//		}
//	});
//	alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//		
//		@Override
//		public void onClick(DialogInterface dialog, int which) {
//			// TODO Auto-generated method stub
//			alertDialog.dismiss();
//		}
//	});
//	alertDialog = alertDialogBuilder.create();
//	alertDialog.show();     
//	return true;
		case R.id.menu_delink:
			System.out.println(">>>>>>> delink option");
			AlertDialog.Builder alertDialogBuilder1 = createDialog
			.createAlertDialog(
					"Delink",
					"Do you wish to delink business with device ?",
					false);
	alertDialogBuilder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			alertDialog1.dismiss();
//			if(autoUpdate != null)
//			{
			autoUpdate.cancel();
			autoUpdate.purge();
			autoUpdate = null;
//			}
			if(globalVariable.getIntervalMenIn() > 0 || globalVariable.getIntervalMenOut() > 0 || globalVariable.getIntervalWomenIn() > 0 || globalVariable.getIntervalWomenOut() > 0)
			{
				SchedulerCount scheduledTask = new SchedulerCount(BusinessDashboardActivity.this);
			    Timer timer = new Timer();
			    timer.scheduleAtFixedRate(scheduledTask, 1000, 10000);
			    scheduledTask.run();
			    SchedulerCount.event = globalVariable.getSelectedEvent();
			    timer.cancel();
			}
			globalVariable.setSelectedBusiness(null);
			globalVariable.setSelectedEvent(null);
			globalVariable.setMenIn(0);
			globalVariable.setMenOut(0);
			globalVariable.setWomenIn(0);
			globalVariable.setWomenOut(0);
			globalVariable.fbPostOff();
			globalVariable.saveSharedPreferences();
			Intent nextIntent = new Intent(BusinessDashboardActivity.this, BusinessOfUserActivity.class);
			startActivity(nextIntent);
			overridePendingTransition(R.anim.anim_out, R.anim.anim_in);
		}
	});
	alertDialogBuilder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			alertDialog1.dismiss();
		}
	});
	 alertDialog1 = alertDialogBuilder1.create();
	 alertDialog1.show();
	 System.out.println(">>>>>>> last line delnk");
	 return true;
		
	
		case R.id.menu_settings:
			if(autoUpdate != null)
			autoUpdate.cancel();
			nextIntent = new Intent(BusinessDashboardActivity.this,
					AppSettingsActivity.class);
			startActivityForResult(nextIntent, SETTING);
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	
	
	private class DeleteAsyncTask extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONParser jParser = new JSONParser();
			jParser.deleteObject("http://dcounter.herokuapp.com/businesses/", globalVariable.getSelectedBusiness().get_id().get$oid() );
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			
			super.onPostExecute(result);
			globalVariable.getCustomer().getBusinesses().remove(globalVariable.getSelectedBusiness());
			progressDialog.dismiss();
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
		
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(autoUpdate != null)
		{
			autoUpdate.cancel();
			autoUpdate.purge();
			autoUpdate = null;
		}
		super.onBackPressed();
	}
	
}
