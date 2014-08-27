package com.wduwg.watch.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.prefs.Preferences;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.gsm.SmsManager;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apphance.android.activity.ApphanceActivity;
import com.manateeworks.cameraDemo.ActivityCapture;
import com.mw.wduwg.adapter.ContextMenuAdapter;
import com.mw.wduwg.model.ContextMenuItem;
import com.mw.wduwg.model.Event;
import com.mw.wduwg.services.CreateDialog;
import com.mw.wduwg.services.GlobalVariable;
import com.mw.wduwg.services.SchedulerCount;

public class CountActivity extends ApphanceActivity implements OnTouchListener {

	private static final int SWIPE_MIN_DISTANCE = 20;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private static final int FACEBOOK = 69;
	private static final int SCANNER = 96;
	private static final int SETTING = 93;
	private static final int REPORT = 94;
	public static final int MOVE_BACK = 100;
	public static int MOVE_ANOTHER_STEP_BACK = 10;

	TextView inMaleTV, currentMaleTV, outMaleTV, inFemaleTV, currentFemaleTV,
			outFemaleTV, totalHeaderTV, total_attendance;

	TextView actionBarTextView;

	LinearLayout countPageEntireLL;
	LinearLayout maleLayout;
	LinearLayout femaleLayout;

	String businessName;
	Event selectedEvent;
	

	Handler handler = new Handler();
	MediaPlayer mPlayerIn;
	MediaPlayer mPlayerOut;

	Typeface typeface;
	Typeface typeface2;

	SharedPreferences sharedPreference;
	Editor editor;
	GlobalVariable globalVariable;

	CreateDialog createDialog;
	ProgressDialog progressDialog;
	AlertDialog.Builder alertDialogBuilder;
	AlertDialog alertDialog;

	boolean isFlashCompatible;

	ListView listView;
	View child;
	List<ContextMenuItem> contextMenuItems;
	TextView headerTV;
	Dialog customDialog;

	SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd MMM, yy");

	Intent previousIntent;
	Intent nextIntent;

	LayoutInflater inflater;

	Timer timer;
	ContextMenuAdapter adapter;

	View customActionBarView;
	ActionBar ab;
	SchedulerCount scheduledTask;

	SharedPreferences sharedPref;

	private void findThings() {

		inMaleTV = (TextView) findViewById(R.id.male_in);
		inFemaleTV = (TextView) findViewById(R.id.female_in);

		outMaleTV = (TextView) findViewById(R.id.male_out);
		outFemaleTV = (TextView) findViewById(R.id.female_out);
		currentMaleTV = (TextView) findViewById(R.id.total_male);
		currentFemaleTV = (TextView) findViewById(R.id.total_female);

		totalHeaderTV = (TextView) findViewById(R.id.total_header);
		

		femaleLayout = (LinearLayout) findViewById(R.id.female_counter);
		maleLayout = (LinearLayout) findViewById(R.id.male_counter);
		countPageEntireLL = (LinearLayout) findViewById(R.id.count_entire_page_LL);

	}

	private void initializeThings() {
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		typeface = Typeface
				.createFromAsset(getAssets(), "Fonts/OpenSans-Bold.ttf");
		inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		createDialog = new CreateDialog(this);
		businessName = getIntent().getStringExtra("business_name");
		mPlayerIn = MediaPlayer.create(this, R.raw.in_sound);
		mPlayerOut = MediaPlayer.create(this, R.raw.out_sound);

		sharedPreference = PreferenceManager.getDefaultSharedPreferences(this
				.getApplicationContext());
		editor = sharedPreference.edit();

		globalVariable = (GlobalVariable) getApplicationContext();
		selectedEvent = globalVariable.getSelectedEvent();

		scheduledTask = new SchedulerCount(this);

		ab = getActionBar();
		customActionBarView = inflater
				.inflate(R.layout.custom_action_bar, null);
		actionBarTextView = (TextView) customActionBarView
				.findViewById(R.id.action_bar_TV);

		inMaleTV.setTypeface(typeface);
		inFemaleTV.setTypeface(typeface);
		outMaleTV.setTypeface(typeface);
		outFemaleTV.setTypeface(typeface);
		currentMaleTV.setTypeface(typeface);
		currentFemaleTV.setTypeface(typeface);
		Event tempEvent = globalVariable.getSelectedEvent();
		System.out.println(">>>>>>> " + tempEvent.getName());
		totalHeaderTV.setTypeface(typeface);
		System.out.println(">>>>>>> istempEventNull:" + (tempEvent == null));
		if (tempEvent.getName().equals("defaultEvent"))
			totalHeaderTV.setText("No event information.\nCount started at: "
					+ globalVariable.timeFormat(tempEvent
							.getStartDate()
							.replace('T', ',')
							.substring(0,
									(tempEvent.getStartDate().length() - 8))));
		else {
			// if we dont use dateFormat it will show time in IST

			totalHeaderTV.setText("You are counting for "
					+ tempEvent.getName()
					+ "\nEvent started at: "
					+ globalVariable.timeFormat(tempEvent
							.getStartDate()
							.replace('T', ',')
							.substring(0,
									(tempEvent.getStartDate().length() - 8)))
					+ "\nEvent ends at:  "
					+ globalVariable.timeFormat(tempEvent
							.getEndDate()
							.replace('T', ',')
							.substring(0,
									(tempEvent.getEndDate().length() - 8))));
		}
		updateCounts();
	}

	private void updateCounts() {
		inMaleTV.setText("" + globalVariable.getMenIn());
		inFemaleTV.setText("" + globalVariable.getWomenIn());
		outMaleTV.setText("" + globalVariable.getMenOut());
		outFemaleTV.setText("" + globalVariable.getWomenOut());
		
		currentMaleTV.setText(""
				+ (globalVariable.getMenIn() - globalVariable.getMenOut()));
		currentFemaleTV.setText(""
				+ (globalVariable.getWomenIn() - globalVariable.getWomenOut()));

		actionBarTextView
		.setText("Total Count : "
				+ ((globalVariable.getMenIn() - globalVariable
						.getMenOut()) + (globalVariable.getWomenIn() - globalVariable
								.getWomenOut())));
		actionBarTextView.setTypeface(typeface);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_count);
		globalVariable = (GlobalVariable) getApplicationContext();
		sharedPreference = PreferenceManager.getDefaultSharedPreferences(this
				.getApplicationContext());
		editor = sharedPreference.edit();
		mPlayerIn = MediaPlayer.create(this, R.raw.in_sound);
		mPlayerOut = MediaPlayer.create(this, R.raw.out_sound);
		typeface = Typeface
				.createFromAsset(getAssets(), "Fonts/OpenSans-Bold.ttf");
		inMaleTV = (TextView)findViewById(R.id.menInTV);
		outMaleTV = (TextView)findViewById(R.id.menOutTV);
		inFemaleTV = (TextView)findViewById(R.id.womenInTV);
		outFemaleTV = (TextView)findViewById(R.id.womenOutTV);
		total_attendance =(TextView)findViewById(R.id.total_attendance);
		
		RelativeLayout womenOut = (RelativeLayout)findViewById(R.id.women_out);
		RelativeLayout womenIn = (RelativeLayout)findViewById(R.id.women_in);
		RelativeLayout menOut = (RelativeLayout)findViewById(R.id.men_out);
		RelativeLayout menIn = (RelativeLayout)findViewById(R.id.men_in);
		
		inMaleTV.setTypeface(typeface);
		outMaleTV.setTypeface(typeface);
		inFemaleTV.setTypeface(typeface);
		outFemaleTV.setTypeface(typeface);
		inMaleTV.setText("" + globalVariable.getMenIn());
		outMaleTV.setText(""+globalVariable.getMenOut());
		inFemaleTV.setText(""+globalVariable.getWomenIn());
		outFemaleTV.setText(""+globalVariable.getWomenOut());
		total_attendance.setText(""+((globalVariable.getMenIn() - globalVariable
				.getMenOut()) + (globalVariable.getWomenIn() - globalVariable
						.getWomenOut())));
		inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout menLayout = (RelativeLayout) findViewById(R.id.menLayout);
		RelativeLayout womenLayout = (RelativeLayout) findViewById(R.id.womenLayout);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		System.out.println(">>>>>>> Width:"+width);
		System.out.println(">>>>>>> Height:"+height);
		
		createDialog = new CreateDialog(this);
		
		
		womenIn.setOnLongClickListener(new OnLongClickListener() {
			
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
//				headerTV.setText(globalVariable.getSelectedBusiness().getName()+"->"+globalVariable.getSelectedFBPage().getName()+"\n"
//		        		+"Total Attendance -> "+(globalVariable.getMenIn()+globalVariable.getWomenIn() -globalVariable.getWomenOut()-globalVariable.getMenOut()));
				headerTV.setText(globalVariable.getSelectedBusiness().getName()
		        		+"\nTotal Attendance At server ->"+(globalVariable.getTotalInDB()+globalVariable.getIntervalMenIn()+globalVariable.getIntervalWomenIn() - globalVariable.getIntervalMenOut() - globalVariable.getIntervalWomenOut()));
				customDialog.show();
				return false;
			}
		});
		womenOut.setOnLongClickListener(new OnLongClickListener() {
			
			   @Override
			
			   public boolean onLongClick(View v) {
			
			    // TODO Auto-generated method stub
				   headerTV.setText(globalVariable.getSelectedBusiness().getName()
			        		+"\nTotal Attendance At server -> "+(globalVariable.getTotalInDB()+globalVariable.getIntervalMenIn()+globalVariable.getIntervalWomenIn() - globalVariable.getIntervalMenOut() - globalVariable.getIntervalWomenOut()));
				   customDialog.show();
					return false;
			
			   }
			
			  });
		
				menIn.setOnLongClickListener(new OnLongClickListener() {
							
							public boolean onLongClick(View v) {
								// TODO Auto-generated method stub
//								headerTV.setText(globalVariable.getSelectedBusiness().getName()+"->"+globalVariable.getSelectedFBPage().getName()+"\n"
//						        		+"Total Attendance -> "+(globalVariable.getMenIn()+globalVariable.getWomenIn() -globalVariable.getWomenOut()-globalVariable.getMenOut()));
								
								headerTV.setText(globalVariable.getSelectedBusiness().getName()
						        		+"\nTotal Attendance At server -> "+(globalVariable.getTotalInDB()+globalVariable.getIntervalMenIn()+globalVariable.getIntervalWomenIn() - globalVariable.getIntervalMenOut() - globalVariable.getIntervalWomenOut()));
								customDialog.show();
								return false;
							}
						});
				menOut.setOnLongClickListener(new OnLongClickListener() {
					
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						headerTV.setText(globalVariable.getSelectedBusiness().getName()
				        		+"\nTotal Attendance At server -> "+(globalVariable.getTotalInDB()+globalVariable.getIntervalMenIn()+globalVariable.getIntervalWomenIn() - globalVariable.getIntervalMenOut() - globalVariable.getIntervalWomenOut()));
						customDialog.show();
						return false;
					}
				});
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}
	
	// ******************************************
	
	public void menIn_watch(View v) {
//		mPlayerIn.start();
		globalVariable.setMenIn(globalVariable.getMenIn() + 1);
		globalVariable.setIntervalMenIn(globalVariable.getIntervalMenIn()+1);
		inMaleTV.setText("" + globalVariable.getMenIn());
		int total = (globalVariable.getMenIn() - globalVariable.getMenOut())
				+ (globalVariable.getWomenIn() - globalVariable.getWomenOut());
		total_attendance.setText(""+total);
		System.out.println(">>>>>>> inside men_in:"+globalVariable.getMenIn());
		System.out.println(">>>>>>> time:"+new Date());
		System.out.println(">>>>>>> count at Server"+globalVariable.getTotalInDB());
		globalVariable.saveSharedPreferences();
//		if(total % 50 == 0)
//		{
//			int a = 10/0;
//		}
		
//		if (sharedPreference.contains("prefNotificationFrequency")) {
//			int message_frequency = Integer.parseInt(sharedPreference.getString(
//					"prefNotificationFrequency", ""));
//			if (total > 0
//					&& total % message_frequency == 0
//					&& sharedPreference.getBoolean("prefMessageSwitch", false) == true) {
////				System.out.println(">>>>>>> message frequency:"+message_frequency);
////				sendNotification();
//			}
//		}

	}

	public void menOut_watch(View v) {
		if ((globalVariable.getMenIn() - globalVariable.getMenOut()) > 0) {
//			mPlayerOut.start();
			globalVariable.setMenOut(globalVariable.getMenOut() + 1);
			globalVariable.setIntervalMenOut(globalVariable.getIntervalMenOut()+1);
			outMaleTV.setText("" + globalVariable.getMenOut());
			System.out.println(">>>>>>> inside men_out:"+globalVariable.getMenOut());
			System.out.println(">>>>>>> time:"+new Date());
			System.out.println(">>>>>>> count at Server"+globalVariable.getTotalInDB());
			globalVariable.saveSharedPreferences();
			int total = (globalVariable.getMenIn() - globalVariable.getMenOut())
					+ (globalVariable.getWomenIn() - globalVariable.getWomenOut());
			total_attendance.setText(""+total);
			
		}
	}

	public void womenIn_watch(View v) {
//		mPlayerIn.start();
		globalVariable.setWomenIn(globalVariable.getWomenIn() + 1);
		globalVariable.setIntervalWomenIn(globalVariable.getIntervalWomenIn()+1);
		inFemaleTV.setText("" + globalVariable.getWomenIn());
		int total1 = (globalVariable.getMenIn() - globalVariable.getMenOut())
				+ (globalVariable.getWomenIn() - globalVariable.getWomenOut());
		total_attendance.setText(""+total1);
		System.out.println(">>>>>>> inside women in");
		System.out.println(">>>>>>> inside women_in:"+globalVariable.getWomenIn());
		System.out.println(">>>>>>> time:"+new Date());
		System.out.println(">>>>>>> count at Server"+globalVariable.getTotalInDB());
		globalVariable.saveSharedPreferences();
//		if(total1 % 50 == 0)
//		{
//			int a = 10/0;
//		}
//		if (sharedPreference.contains("prefNotificationFrequency")) {
//			int total = (globalVariable.getMenIn() - globalVariable.getMenOut())
//					+ (globalVariable.getWomenIn() - globalVariable
//							.getWomenOut());
//			int message_frequency = Integer.parseInt(sharedPreference.getString(
//					"prefNotificationFrequency", ""));
//			if (total > 0
//					&& total % message_frequency == 0
//					&& sharedPreference.getBoolean("prefMessageSwitch", false) == true) {
////				System.out.println(">>>>>>> message frequency:"+message_frequency);
////				sendNotification();
//			}
//		}
	}

	public void womenOut_watch(View v) {
		if ((globalVariable.getWomenIn() - globalVariable.getWomenOut()) > 0) {
			System.out.println(">>>>>>> inside women_out:"+globalVariable.getWomenOut());
			System.out.println(">>>>>>> time:"+new Date());
			System.out.println(">>>>>>> count at Server"+globalVariable.getTotalInDB());
//			mPlayerOut.start();
			globalVariable.setWomenOut(globalVariable.getWomenOut() + 1);
			globalVariable.setIntervalWomenOut(globalVariable.getIntervalWomenOut()+1);
			outFemaleTV.setText("" + globalVariable.getWomenOut());
			globalVariable.saveSharedPreferences();
			int total = (globalVariable.getMenIn() - globalVariable.getMenOut())
					+ (globalVariable.getWomenIn() - globalVariable.getWomenOut());
			total_attendance.setText(""+total);
		}
	}

	boolean ignoreOnRestart = false;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SETTING) {
			if (resultCode == RESULT_OK)
				ignoreOnRestart = true;
			return;
		}
		if (requestCode == FACEBOOK) {
			adapter.swapData(contextMenuItems, true);
			adapter.notifyDataSetChanged();
			listView.invalidateViews();
			ignoreOnRestart = true;
			restartSaving();
		}
		if(requestCode == REPORT)
		{
			if(resultCode == RESULT_OK)
				ignoreOnRestart = true;
			return;
		}
		if (requestCode == SCANNER) {
			if (resultCode == RESULT_OK) {
				System.out.println(">>>>>>> scaner result ok");
				ignoreOnRestart = true;
			} else if (resultCode == RESULT_CANCELED) {
			}
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	private void restartSaving() {
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(timer == null)
		{
		scheduledTask = new SchedulerCount(this);
		timer = new Timer();
		timer.scheduleAtFixedRate(scheduledTask, 1000, 120000);
		}
		
		child = inflater.inflate(R.layout.listview_context_menu, null);
		listView = (ListView) child.findViewById(R.id.listView_context_menu);
		headerTV = (TextView) child.findViewById(R.id.header_TV);
		headerTV.setTypeface(typeface);
//        headerTV.setText(globalVariable.getSelectedBusiness().getName()+"->"+globalVariable.getSelectedFBPage().getName()+"\n"
//        		+"Total Attendance -> "+(globalVariable.getMenIn()+globalVariable.getWomenIn() -globalVariable.getWomenOut()-globalVariable.getMenOut()));
		
		headerTV.setText(globalVariable.getSelectedBusiness().getName()+"\n"
        		+"Total Attendance -> "+(globalVariable.getMenIn()+globalVariable.getWomenIn() -globalVariable.getWomenOut()-globalVariable.getMenOut()));
		
		contextMenuItems = new ArrayList<ContextMenuItem>();
		contextMenuItems.add(new ContextMenuItem(getResources().getDrawable(
				R.drawable.done), "Reset Counting"));
//		contextMenuItems.add(new ContextMenuItem(getResources().getDrawable(
//				R.drawable.settings), "Settings"));
//		contextMenuItems.add(new ContextMenuItem(getResources().getDrawable(
//				R.drawable.facebook), "Logout"));
//		boolean isLogoutVisisble = false;
//		if (globalVariable.getFb_access_token() != null) {
//			isLogoutVisisble = true;
//			System.out.println(">>>>>>> true");
//		}
//		isFlashCompatible = this.getPackageManager().hasSystemFeature(
//				PackageManager.FEATURE_CAMERA_FLASH);
//
		adapter = new ContextMenuAdapter(CountActivity.this, contextMenuItems, false);// isFlashCompatible
//
		listView.setAdapter(adapter);
//
		customDialog = new Dialog(CountActivity.this);
		customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		customDialog.setContentView(child);
		customDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.WHITE));
		customDialog.setTitle("Options");
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	public void onDone(View view) {
		
		alertDialogBuilder = createDialog.createAlertDialog("Alert",
				"Do you wish to reset Count?", false);

		alertDialogBuilder.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						saveLastCount();
//						CountActivity.this.setResult(MOVE_ANOTHER_STEP_BACK,
//								previousIntent);
						inMaleTV.setText("" + 0);
						outMaleTV.setText(""+0);
						inFemaleTV.setText(""+0);
						outFemaleTV.setText(""+0);
						total_attendance.setText(""+0);
//						finish();
					}

				});

		alertDialogBuilder.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});

		alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	void saveLastCount() {
		scheduledTask.run();
//		timer.cancel();
		globalVariable.setSelectedEvent(null);
		globalVariable.setMenIn(0);
		globalVariable.setMenOut(0);
		globalVariable.setWomenIn(0);
		globalVariable.setWomenOut(0);
		globalVariable.saveSharedPreferences();
	}

//	public void onLogout(View view) {
//		if (LoginFacebookActivity.timer != null)
//			LoginFacebookActivity.timer.cancel();
//
//		adapter.swapData(contextMenuItems, false);
//		adapter.notifyDataSetChanged();
//		listView.invalidateViews();
//		globalVariable.getCustomer().setPages(null);
//		globalVariable.setFb_access_expire(0);
//		globalVariable.setFb_access_token(null);
//		globalVariable.saveSharedPreferences();
//		Toast.makeText(this, "Logged out from FB.", Toast.LENGTH_SHORT).show();
//		customDialog.dismiss();
//		nextIntent = new Intent(this,SpalshFirstActivity.class);
//		nextIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//		startActivity(nextIntent);
//	}

	boolean isFlashOn = false;
	Camera cam;

	public void onMenuItemSelected(View view) {
		int position = listView.getPositionForView(view);
		customDialog.dismiss();
		if (position == 0) {
			onDone(null);
		} 
//	else if (position == 1) {
//			nextIntent = new Intent(CountActivity.this,
//					AppSettingsActivity.class);
//			startActivityForResult(nextIntent, SETTING);
//	      }
//		else if (position == 2) {
//			onLogout(null);
//		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		globalVariable.saveSharedPreferences();
	}

	@SuppressWarnings("deprecation")
	public void sendNotification() {
		try {
			int total = (globalVariable.getMenIn() - globalVariable.getMenOut())
					+ (globalVariable.getWomenIn() - globalVariable
							.getWomenOut());
			System.out.println(">>>>>>> notification ph no:"+sharedPreference.getString("prefPhone", "XXXXXXXXXX"));
			SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a");
			df.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
			String strDate = df.format(new Date());
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(
					sharedPreference.getString("prefPhone", "09019129275"), "wduwg",
					"Total Attendance at \""+globalVariable.getSelectedBusiness().getName()+"\" are " + (total)+ " at "+strDate,
					null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}