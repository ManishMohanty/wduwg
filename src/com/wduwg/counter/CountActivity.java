package com.wduwg.counter;

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
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.gsm.SmsManager;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.apphance.android.activity.ApphanceActivity;
import com.wduwg.counter.R;
import com.manateeworks.cameraDemo.ActivityCapture;
import com.mw.wduwg.adapter.ContextMenuAdapter;
import com.mw.wduwg.model.ContextMenuItem;
import com.mw.wduwg.model.Event;
import com.mw.wduwg.services.CreateDialog;
import com.mw.wduwg.services.GlobalVariable;
import com.mw.wduwg.services.SchedulerCount;

public class CountActivity extends ApphanceActivity implements OnTouchListener {

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private static final int FACEBOOK = 69;
	private static final int SCANNER = 96;
	private static final int SETTING = 93;
	private static final int REPORT = 94;
	public static final int MOVE_BACK = 100;
	public static int MOVE_ANOTHER_STEP_BACK = 10;

	TextView inMaleTV, currentMaleTV, outMaleTV, inFemaleTV, currentFemaleTV,
			outFemaleTV, totalHeaderTV;

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
//		Event tempEvent = globalVariable.getSelectedEvent();
//		System.out.println(">>>>>>> " + tempEvent.getName());
//		totalHeaderTV.setTypeface(typeface);
//		System.out.println(">>>>>>> startDate:" + tempEvent.getStartDate());
//		if (tempEvent.getName().equals("defaultEvent"))
			totalHeaderTV.setText("Count started at: "
					+ globalVariable.getStartDate());
//		else {
			// if we dont use dateFormat it will show time in IST

//			totalHeaderTV.setText("You are counting for "
//					+ tempEvent.getName()
//					+ "\nEvent started at: "
//					+ globalVariable.timeFormat(tempEvent
//							.getStartDate()
//							.replace('T', ',')
//							.substring(0,
//									(tempEvent.getStartDate().length() - 8)))
//					+ "\nEvent ends at:  "
//					+ globalVariable.timeFormat(tempEvent
//							.getEndDate()
//							.replace('T', ',')
//							.substring(0,
//									(tempEvent.getEndDate().length() - 8))));
//		}
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
		setContentView(R.layout.activity_count);

		 Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
		findThings();
		initializeThings();
		registerForContextMenu(maleLayout);
		registerForContextMenu(femaleLayout);

		// schedule task
		timer = new Timer();
		timer.scheduleAtFixedRate(scheduledTask, 1000, 120000);
		for (int i = 0; i < 5; i++)
			scheduledTask.run();
		SchedulerCount.event = globalVariable.getSelectedEvent();

		ab.setDisplayShowCustomEnabled(true);
		ab.setCustomView(customActionBarView);

		child = inflater.inflate(R.layout.listview_context_menu, null);
		listView = (ListView) child.findViewById(R.id.listView_context_menu);
		headerTV = (TextView) child.findViewById(R.id.header_TV);
		headerTV.setTypeface(typeface);
        headerTV.setText(globalVariable.getSelectedBusiness().getName()+" - "+globalVariable.getSelectedFBPage().getName());
		contextMenuItems = new ArrayList<ContextMenuItem>();
		contextMenuItems.add(new ContextMenuItem(getResources().getDrawable(
				R.drawable.facebook), "Facebook"));

		contextMenuItems.add(new ContextMenuItem(getResources().getDrawable(
				R.drawable.scanner2), "Scanner"));

		contextMenuItems.add(new ContextMenuItem(getResources().getDrawable(
				R.drawable.flash2), "Flashlight"));
		contextMenuItems.add(new ContextMenuItem(getResources().getDrawable(
				R.drawable.settings), "Settings"));
		contextMenuItems.add(new ContextMenuItem(getResources().getDrawable(
				R.drawable.report), "Reports"));

		boolean isLogoutVisisble = false;
		if (globalVariable.getFb_access_token() != null) {
			isLogoutVisisble = true;
			System.out.println(">>>>>>> true");
		}
		isFlashCompatible = this.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA_FLASH);

		adapter = new ContextMenuAdapter(CountActivity.this, contextMenuItems,
				isLogoutVisisble, false);// isFlashCompatible

		listView.setAdapter(adapter);

		customDialog = new Dialog(CountActivity.this);
		customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		customDialog.setContentView(child);
		customDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.WHITE));
		customDialog.setTitle("Options");

		final GestureDetector gdt1 = new GestureDetector(new GestureListener1());
		femaleLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(final View view, final MotionEvent event) {
				gdt1.onTouchEvent(event);
				return true;
			}
		});

		final GestureDetector gdt2 = new GestureDetector(new GestureListener2());
		maleLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(final View view, final MotionEvent event) {
				gdt2.onTouchEvent(event);
				return true;
			}
		});

	}

	private class GestureListener1 extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				womenIn();
				return false; // Right to left
			} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				womenOut();
				return false; // Left to right
			}
			if (e2.getY() - e1.getY() > 100 && Math.abs(velocityY) > 800) {
				System.out.println(">>>>>>> swipe down");
				// openContextMenu(femaleLayout);
				customDialog.show();
				return false;
			}

			return false;
		}
	}

	private class GestureListener2 extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				menIn();
				return false; // Right to left
			} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				menOut();
				return false; // Left to right
			}
			if (e2.getY() - e1.getY() > 100 && Math.abs(velocityY) > 800) {
				System.out.println(">>>>>>> swipe down");
				// openContextMenu(maleLayout);
				customDialog.show();
				return false;
			}
			return false;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	private void menIn() {
		mPlayerIn.start();
		globalVariable.setMenIn(globalVariable.getMenIn() + 1);
		globalVariable.setIntervalMenIn(globalVariable.getIntervalMenIn()+1);
		inMaleTV.setText("" + globalVariable.getMenIn());
		currentMaleTV.setText(""
				+ (globalVariable.getMenIn() - globalVariable.getMenOut()));
		actionBarTextView
				.setText("Total Count : "
						+ ((globalVariable.getMenIn() - globalVariable
								.getMenOut()) + (globalVariable.getWomenIn() - globalVariable
								.getWomenOut())));
		int total = (globalVariable.getMenIn() - globalVariable.getMenOut())
				+ (globalVariable.getWomenIn() - globalVariable.getWomenOut());
		if (sharedPref.contains("prefNotificationFrequency")) {
			int message_frequency = Integer.parseInt(sharedPref.getString(
					"prefNotificationFrequency", ""));
			if (total > 0
					&& total % message_frequency == 0
					&& sharedPref.getBoolean("prefMessageSwitch", false) == true) {
				System.out.println(">>>>>>> inside men in");
				sendNotification();
			}
		}

	}

	private void menOut() {
		if ((globalVariable.getMenIn() - globalVariable.getMenOut()) > 0) {
			mPlayerOut.start();
			globalVariable.setMenOut(globalVariable.getMenOut() + 1);
			globalVariable.setIntervalMenOut(globalVariable.getIntervalMenOut()+1);
			outMaleTV.setText("" + globalVariable.getMenOut());
			currentMaleTV.setText(""
					+ (globalVariable.getMenIn() - globalVariable.getMenOut()));
			actionBarTextView
					.setText("Total Count : "
							+ ((globalVariable.getMenIn() - globalVariable
									.getMenOut()) + (globalVariable
									.getWomenIn() - globalVariable
									.getWomenOut())));
		}
	}

	private void womenIn() {
		mPlayerIn.start();
		globalVariable.setWomenIn(globalVariable.getWomenIn() + 1);
		globalVariable.setIntervalWomenIn(globalVariable.getIntervalWomenIn()+1);
		inFemaleTV.setText("" + globalVariable.getWomenIn());
		currentFemaleTV.setText(""
				+ (globalVariable.getWomenIn() - globalVariable.getWomenOut()));
		actionBarTextView
				.setText("Total Count : "
						+ ((globalVariable.getMenIn() - globalVariable
								.getMenOut()) + (globalVariable.getWomenIn() - globalVariable
								.getWomenOut())));
		if (sharedPref.contains("prefNotificationFrequency")) {
			int total = (globalVariable.getMenIn() - globalVariable.getMenOut())
					+ (globalVariable.getWomenIn() - globalVariable
							.getWomenOut());
			int message_frequency = Integer.parseInt(sharedPref.getString(
					"prefNotificationFrequency", ""));
			if (total > 0
					&& total % message_frequency == 0
					&& sharedPref.getBoolean("prefMessageSwitch", false) == true) {
				System.out.println(">>>>>>> inside women in");
				sendNotification();
			}
		}
	}

	private void womenOut() {
		if ((globalVariable.getWomenIn() - globalVariable.getWomenOut()) > 0) {
			mPlayerOut.start();
			globalVariable.setWomenOut(globalVariable.getWomenOut() + 1);
			globalVariable.setIntervalWomenOut(globalVariable.getIntervalWomenOut()+1);
			outFemaleTV.setText("" + globalVariable.getWomenOut());
			currentFemaleTV.setText(""
					+ (globalVariable.getWomenIn() - globalVariable
							.getWomenOut()));
			actionBarTextView
					.setText("Total Count : "
							+ ((globalVariable.getMenIn() - globalVariable
									.getMenOut()) + (globalVariable
									.getWomenIn() - globalVariable
									.getWomenOut())));
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
				updateCounts();
				restartSaving();
				ignoreOnRestart = true;
			} else if (resultCode == RESULT_CANCELED) {
				ignoreOnRestart = true;
				alertDialogBuilder = createDialog.createAlertDialog(
						"Scanner Error", "Unable to Scan", false);
				alertDialogBuilder.setPositiveButton("Try Again",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								nextIntent = new Intent(CountActivity.this,
										ActivityCapture.class);
								startActivityForResult(nextIntent, SCANNER);
								alertDialog.dismiss();
							}
						});
				alertDialogBuilder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								restartSaving();
								alertDialog.dismiss();
							}
						});
				alertDialog = alertDialogBuilder.create();
				alertDialog.show();
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
	}

	@Override
	public void onBackPressed() {
		alertDialogBuilder = createDialog.createAlertDialog("Alert",
				"Stop counting for this event?", false);

		alertDialogBuilder.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						CountActivity.this.setResult(MOVE_ANOTHER_STEP_BACK,
								previousIntent);
						saveLastCount();
						CountActivity.this.setResult(MOVE_BACK, previousIntent);
						saveLastCount();
						finish();
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

	public void onDone(View view) {
		globalVariable.setMenIn(0);
		globalVariable.setWomenIn(0);
		globalVariable.setMenOut(0);
		globalVariable.setWomenOut(0);
		alertDialogBuilder = createDialog.createAlertDialog("Alert",
				"Stop counting for this event?", false);

		alertDialogBuilder.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						CountActivity.this.setResult(MOVE_ANOTHER_STEP_BACK,
								previousIntent);
						saveLastCount();
						finish();
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
		timer.cancel();
		globalVariable.setSelectedEvent(null);
		globalVariable.saveSharedPreferences();
	}

	public void onLogout(View view) {
		if (LoginFacebookActivity.timer != null)
			LoginFacebookActivity.timer.cancel();

		adapter.swapData(contextMenuItems, false);
		adapter.notifyDataSetChanged();
		listView.invalidateViews();
		globalVariable.getCustomer().setPages(null);
		globalVariable.setFb_access_expire(0);
		globalVariable.setFb_access_token(null);
		globalVariable.saveSharedPreferences();
//		globalVariable.clearSP();
		Toast.makeText(this, "Logged out from FB.", Toast.LENGTH_SHORT).show();
		customDialog.dismiss();
		nextIntent = new Intent(this,SpalshFirstActivity.class);
		nextIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(nextIntent);
	}

	boolean isFlashOn = false;
	Camera cam;

	public void onMenuItemSelected(View view) {
		int position = listView.getPositionForView(view);
		customDialog.dismiss();
		if (position == 0) {
			nextIntent = new Intent(CountActivity.this,
					LoginFacebookActivity.class);
			nextIntent.putExtra("fromContext", true);
			startActivityForResult(nextIntent, FACEBOOK);
		} else if (position == 1) {
			nextIntent = new Intent(CountActivity.this, ActivityCapture.class);
			startActivityForResult(nextIntent, SCANNER);
		} else if (position == 2) {
			if (isFlashCompatible) {
				// Switch dsa = (Switch) view.findViewById(R.id.switchB);

				if (!isFlashOn) {
					cam = Camera.open();
					Parameters p = cam.getParameters();
					p.setFlashMode(Parameters.FLASH_MODE_TORCH);
					cam.setParameters(p);
					cam.startPreview();
				} else {
					cam.stopPreview();
					cam.release();
				}
				isFlashOn = !isFlashOn;
				// dsa.setChecked(isFlashOn);
			} else {
				Toast.makeText(this, "Flash not available", Toast.LENGTH_SHORT)
						.show();
			}
		}// if (position == 2)
		else if (position == 3) {
			nextIntent = new Intent(CountActivity.this,
					AppSettingsActivity.class);
			startActivityForResult(nextIntent, SETTING);
		} else if (position == 4) {
			nextIntent = new Intent(CountActivity.this,
					ReportActualActvivity.class);
			startActivityForResult(nextIntent, REPORT);
		}
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
					- (globalVariable.getWomenIn() - globalVariable
							.getWomenOut());
			SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a");
			df.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
			String strDate = df.format(new Date());
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(
					sharedPref.getString("prefPhone", "09019129275"), "wduwg",
					"Total Attendance at \""+globalVariable.getSelectedBusiness().getName()+"\" are " + (total)+ " at "+strDate,
					null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}