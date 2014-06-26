package com.example.wduwg;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.apphance.android.Log;
import com.example.wduwg.SpecialActivity.LoadStringsAsync;
import com.mw.wduwg.adapter.EventAdapter2;
import com.mw.wduwg.adapter.SpecialApater;
import com.mw.wduwg.model.Event;
import com.mw.wduwg.model.Special;
import com.mw.wduwg.services.CreateDialog;
import com.mw.wduwg.services.GlobalVariable;
import com.mw.wduwg.services.JSONParser;


public class BusinessDashboardActivity extends Activity {

	
	Typeface typefaceBold;
	GlobalVariable globalVariable;
	List<Event> eventList ;
	List<Special> specialList;
	ListView eventListView, SpecialListView;
	CreateDialog createDialog;
	ProgressDialog progressDialog;
	int men_in=0,women_in=0,men_out=0,women_out=0;
	
	TextView totalCount,menCount,womenCount,totalVisitors;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_business_dashboard);
		typefaceBold = Typeface.createFromAsset(getAssets(), "Fonts/OpenSans-Bold.ttf");
		 totalCount = (TextView)findViewById(R.id.totalCount);
		 menCount = (TextView)findViewById(R.id.menCount);
		 womenCount = (TextView) findViewById(R.id.womenCount);
		 totalVisitors = (TextView)findViewById(R.id.totalVisitor);
		eventListView = (ListView) findViewById(R.id.evntlistView);
		SpecialListView = (ListView)findViewById(R.id.speciallistView);
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
       createDialog = new CreateDialog(this);
		progressDialog = createDialog.createProgressDialog("Loading", "wait for a while", true, null);
		progressDialog.show();
		LoadStringsAsync asyncTask = new LoadStringsAsync();
		asyncTask.execute();
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
						Event event = new Event();
						JSONObject jsonobject = specialsjsonarr.getJSONObject(i);
						event.setName(jsonobject.getString("name"));
						String startTime = globalVariable.timeFormat(jsonobject.getString("start_date_time").replace('T', ',').substring(0, (jsonobject.getString("start_date_time").length()-8)));
						if(!event.getName().equalsIgnoreCase("defaultEvent"))
						{
							System.out.println(">>>>>>> endDate"+jsonobject.getString("end_date_time"));
						    String endTime =  globalVariable.timeFormat(jsonobject.getString("end_date_time").replace('T', ',').substring(0, jsonobject.getString("end_date_time").length()-8));
							event.setDescription("Start Time "+startTime + "\nEnd Time " + endTime);
						}else
						{
							String endTime =  "daily";
							event.setDescription("Start Time "+startTime + "\nEnd Time " + endTime);
						}
						event.setImageUrl("http://fifthgroup.com/boldamericancatering/wp-content/uploads/sites/10/2014/02/boldamerican-053.jpg");
						eventList.add(event);
					} // end of for
				} // end of if
				
			} catch (Exception e) {
				Log.d("Response========", "inside catch");
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
						Special special = new Special();
						JSONObject jsonobject = specialsjsonarr.getJSONObject(i);
						special.setName(jsonobject.getString("name"));
						String starts_from = globalVariable.timeFormat(jsonobject.getString("start_date_time").replace('T', ',').substring(0, (jsonobject.getString("start_date_time").length()-8)));
						String valid_upto =  globalVariable.timeFormat(jsonobject.getString("end_date_time").replace('T', ',').substring(0, jsonobject.getString("end_date_time").length()-8));
						special.setDescription("Start Time "+starts_from + "\nEnd Time " + valid_upto);
						special.setImageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSg92ThBRn7ux2rLEWxZUIKLK-rmMbBBgr6x9ugUZsYUocytf4z");
						specialList.add(special);
						
					}
				}
				
			} catch (Exception e) {
				Log.d("Response========", "inside catch");
				e.printStackTrace();
			}
			return specialList;
		}

		@Override
		protected void onPostExecute(final List<Special> specials) {
			System.out.println(">>>>>>> inside special postexecute");
			globalVariable.getSelectedBusiness().setSpecials(specials);
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
				Log.d("Response========", "inside catch");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void arg) {
			System.out.println(">>>>>>> inside counter postexecute");
			progressDialog.dismiss();
			System.out.println(">>>>>>> progress dialog dismiss");
			List<Special> specials = new ArrayList<Special>();
			List<Event> events = new ArrayList<Event>();
			if(globalVariable.getSelectedBusiness().getSpecials().size()>1)
			{
		     specials = globalVariable.getSelectedBusiness().getSpecials().subList(0, 2);
			}else
			specials = globalVariable.getSelectedBusiness().getSpecials();
            SpecialApater adapter = new SpecialApater(BusinessDashboardActivity.this, specials);
            SpecialListView.setAdapter(adapter);
            if(globalVariable.getSelectedBusiness().getEventList().size() >1)
            {
             events = globalVariable.getSelectedBusiness().getEventList().subList(0, 2);
            }else
            	events = globalVariable.getSelectedBusiness().getEventList();
            EventAdapter2 adapter1 = new EventAdapter2(BusinessDashboardActivity.this, events);
            eventListView.setAdapter(adapter1);
			totalCount.setText("Total :"+((men_in+women_in)-(men_out+women_out)));
			menCount.setText("Men :"+(men_in-men_out));
			womenCount.setText("Women :"+(women_in-women_out));
			totalVisitors.setText("Total Visitors Today :"+(men_in+women_in));
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.overflow_options_menu, menu);
		CharSequence rawTitle = "Logout";
		menu.findItem(R.id.menu_logout).setTitleCondensed(rawTitle);

		SpannableString logoutstr = new SpannableString(rawTitle);
		logoutstr.setSpan(typefaceBold, 0, logoutstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		menu.findItem(R.id.menu_logout).setTitle(logoutstr);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuItem logouItem = menu.findItem(R.id.menu_logout);
		if(globalVariable.getFb_access_token() !=null)
		{
			logouItem.setEnabled(true);
		}else
		{
			logouItem.setEnabled(false);
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
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
}
