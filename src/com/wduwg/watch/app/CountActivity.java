package com.wduwg.watch.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.LayoutInflater;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.apphance.android.activity.ApphanceActivity;
import com.mw.wduwg.adapter.ContextMenuAdapter;
import com.mw.wduwg.model.ContextMenuItem;
import com.mw.wduwg.services.CreateDialog;
import com.mw.wduwg.services.GlobalVariable;
import com.mw.wduwg.services.SchedulerCount;
import com.mw.wduwg.services.ServerURLs;

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

	RequestQueue queue;

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

	int men_in = 0, men_out = 0, women_in = 0, women_out = 0;

//	private BroadcastReceiver myMessageReceiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			// Extract data included in the Intent
//
//			String message = intent.getStringExtra("message");
//			// message = message + "  Additional text after exception message";
//
//			// Toast.makeText(context, message, Toast.LENGTH_LONG).show();
//			alertDialogBuilder = createDialog.createAlertDialog("Error",
//					message, false);
//
//			alertDialogBuilder.setPositiveButton("Yes",
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int id) {
//							dialog.dismiss();
//						}
//
//					});
//			alertDialog = alertDialogBuilder.create();
//			alertDialog.show();
//
//		}
//	};

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		globalVariable.saveSharedPreferences();
//		LocalBroadcastManager.getInstance(this).unregisterReceiver(
//				myMessageReceiver);
	}

	private void findThings() {

		// inMaleTV = (TextView) findViewById(R.id.male_in);
		// inFemaleTV = (TextView) findViewById(R.id.female_in);
		//
		// outMaleTV = (TextView) findViewById(R.id.male_out);
		// outFemaleTV = (TextView) findViewById(R.id.female_out);
		// currentMaleTV = (TextView) findViewById(R.id.total_male);
		// currentFemaleTV = (TextView) findViewById(R.id.total_female);
		//
		// totalHeaderTV = (TextView) findViewById(R.id.total_header);
		//
		//
		// femaleLayout = (LinearLayout) findViewById(R.id.female_counter);
		// maleLayout = (LinearLayout) findViewById(R.id.male_counter);
		// countPageEntireLL = (LinearLayout)
		// findViewById(R.id.count_entire_page_LL);

	}

	private void initializeThings() {
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		typeface = Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Bold.ttf");
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
		totalHeaderTV.setTypeface(typeface);
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
		typeface = Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Bold.ttf");
		inMaleTV = (TextView) findViewById(R.id.menInTV);
		outMaleTV = (TextView) findViewById(R.id.menOutTV);
		inFemaleTV = (TextView) findViewById(R.id.womenInTV);
		outFemaleTV = (TextView) findViewById(R.id.womenOutTV);
		total_attendance = (TextView) findViewById(R.id.total_attendance);

		RelativeLayout womenOut = (RelativeLayout) findViewById(R.id.women_out);
		RelativeLayout womenIn = (RelativeLayout) findViewById(R.id.women_in);
		RelativeLayout menOut = (RelativeLayout) findViewById(R.id.men_out);
		RelativeLayout menIn = (RelativeLayout) findViewById(R.id.men_in);

		inMaleTV.setTypeface(typeface);
		outMaleTV.setTypeface(typeface);
		inFemaleTV.setTypeface(typeface);
		outFemaleTV.setTypeface(typeface);
		inMaleTV.setText("" + globalVariable.getMenIn());
		outMaleTV.setText("" + globalVariable.getMenOut());
		inFemaleTV.setText("" + globalVariable.getWomenIn());
		outFemaleTV.setText("" + globalVariable.getWomenOut());
		total_attendance
				.setText(""
						+ ((globalVariable.getMenIn() - globalVariable
								.getMenOut()) + (globalVariable.getWomenIn() - globalVariable
								.getWomenOut())));
		inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout menLayout = (LinearLayout) findViewById(R.id.menLayout);
		LinearLayout womenLayout = (LinearLayout) findViewById(R.id.womenLayout);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;

		createDialog = new CreateDialog(this);

		womenIn.setOnLongClickListener(new OnLongClickListener() {

			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				headerTV.setText(globalVariable.getSelectedBusiness().getName()
						+ "\nTotal Attendance At server ->"
						+ (globalVariable.getTotalInDB()
								+ globalVariable.getIntervalMenIn()
								+ globalVariable.getIntervalWomenIn()
								- globalVariable.getIntervalMenOut() - globalVariable
									.getIntervalWomenOut()));
				customDialog.show();
				return false;
			}
		});
		womenOut.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				// TODO Auto-generated method stub
				headerTV.setText(globalVariable.getSelectedBusiness().getName()
						+ "\nTotal Attendance At server -> "
						+ (globalVariable.getTotalInDB()
								+ globalVariable.getIntervalMenIn()
								+ globalVariable.getIntervalWomenIn()
								- globalVariable.getIntervalMenOut() - globalVariable
									.getIntervalWomenOut()));
				customDialog.show();
				return false;

			}

		});

		menIn.setOnLongClickListener(new OnLongClickListener() {

			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub

				headerTV.setText(globalVariable.getSelectedBusiness().getName()
						+ "\nTotal Attendance At server -> "
						+ (globalVariable.getTotalInDB()
								+ globalVariable.getIntervalMenIn()
								+ globalVariable.getIntervalWomenIn()
								- globalVariable.getIntervalMenOut() - globalVariable
									.getIntervalWomenOut()));
				customDialog.show();
				return false;
			}
		});
		menOut.setOnLongClickListener(new OnLongClickListener() {

			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				headerTV.setText(globalVariable.getSelectedBusiness().getName()
						+ "\nTotal Attendance At server -> "
						+ (globalVariable.getTotalInDB()
								+ globalVariable.getIntervalMenIn()
								+ globalVariable.getIntervalWomenIn()
								- globalVariable.getIntervalMenOut() - globalVariable
									.getIntervalWomenOut()));
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
		// mPlayerIn.start();
		globalVariable.setMenIn(globalVariable.getMenIn() + 1);
		globalVariable.setIntervalMenIn(globalVariable.getIntervalMenIn() + 1);
		globalVariable.saveSharedPreferences();
		inMaleTV.setText("" + globalVariable.getMenIn());
		int total = (globalVariable.getMenIn() - globalVariable.getMenOut())
				+ (globalVariable.getWomenIn() - globalVariable.getWomenOut());
		total_attendance.setText("" + total);

	}

	public void menOut_watch(View v) {
		if ((globalVariable.getMenIn() - globalVariable.getMenOut()) > 0) {
			// mPlayerOut.start();
			globalVariable.setMenOut(globalVariable.getMenOut() + 1);
			globalVariable
					.setIntervalMenOut(globalVariable.getIntervalMenOut() + 1);
			globalVariable.saveSharedPreferences();
			outMaleTV.setText("" + globalVariable.getMenOut());
			int total = (globalVariable.getMenIn() - globalVariable.getMenOut())
					+ (globalVariable.getWomenIn() - globalVariable
							.getWomenOut());
			total_attendance.setText("" + total);
		}
	}

	public void womenIn_watch(View v) {
		// mPlayerIn.start();
		globalVariable.setWomenIn(globalVariable.getWomenIn() + 1);
		globalVariable
				.setIntervalWomenIn(globalVariable.getIntervalWomenIn() + 1);
		globalVariable.saveSharedPreferences();
		inFemaleTV.setText("" + globalVariable.getWomenIn());
		int total1 = (globalVariable.getMenIn() - globalVariable.getMenOut())
				+ (globalVariable.getWomenIn() - globalVariable.getWomenOut());
		total_attendance.setText("" + total1);
	}

	public void womenOut_watch(View v) {
		if ((globalVariable.getWomenIn() - globalVariable.getWomenOut()) > 0) {
			// mPlayerOut.start();
			globalVariable.setWomenOut(globalVariable.getWomenOut() + 1);
			globalVariable.setIntervalWomenOut(globalVariable
					.getIntervalWomenOut() + 1);
			globalVariable.saveSharedPreferences();
			outFemaleTV.setText("" + globalVariable.getWomenOut());
			int total = (globalVariable.getMenIn() - globalVariable.getMenOut())
					+ (globalVariable.getWomenIn() - globalVariable
							.getWomenOut());
			total_attendance.setText("" + total);
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
		if (requestCode == REPORT) {
			if (resultCode == RESULT_OK)
				ignoreOnRestart = true;
			return;
		}
		if (requestCode == SCANNER) {
			if (resultCode == RESULT_OK) {
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
		// queue = Volley.newRequestQueue(this);

//		LocalBroadcastManager.getInstance(this).registerReceiver(
//				myMessageReceiver,
//				new IntentFilter("scheduler_response_message"));

		try {
			if (timer != null) {
				timer.cancel();
				timer.purge();
				scheduledTask.cancel();
			}
			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
			String imeiNo = telephonyManager.getDeviceId();
			scheduledTask = new SchedulerCount(this , imeiNo);
			timer = new Timer();
			timer.scheduleAtFixedRate(scheduledTask, 1000, 60000);
		} catch (Throwable t) {
			t.printStackTrace();
		}

		child = inflater.inflate(R.layout.listview_context_menu, null);
		listView = (ListView) child.findViewById(R.id.listView_context_menu);
		headerTV = (TextView) child.findViewById(R.id.header_TV);
		headerTV.setTypeface(typeface);

		headerTV.setText(globalVariable.getSelectedBusiness().getName()
				+ "\n"
				+ "Total Attendance -> "
				+ (globalVariable.getTotalInDB() + globalVariable.getMenIn()
						+ globalVariable.getWomenIn()
						- globalVariable.getWomenOut() - globalVariable
							.getMenOut()));

		contextMenuItems = new ArrayList<ContextMenuItem>();
		contextMenuItems.add(new ContextMenuItem("Reset Counting"));
		adapter = new ContextMenuAdapter(CountActivity.this, contextMenuItems,
				false);// isFlashCompatible
		listView.setAdapter(adapter);
		customDialog = new Dialog(CountActivity.this);
		customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		customDialog.setContentView(child);
		customDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.WHITE));
		customDialog.setTitle("Options");
		customDialog.getWindow().getAttributes().verticalMargin = 0.2F;
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
						inMaleTV.setText("" + 0);
						outMaleTV.setText("" + 0);
						inFemaleTV.setText("" + 0);
						outFemaleTV.setText("" + 0);
						total_attendance.setText("" + 0);
					}

				});

		alertDialogBuilder.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});

		alertDialog = alertDialogBuilder.create();
		alertDialog.getWindow().getAttributes().verticalMargin = 0.2F;
		alertDialog.show();
	}

	void saveLastCount() {
		// scheduledTask.run();
		globalVariable.setMenIn(0);
		globalVariable.setMenOut(0);
		globalVariable.setWomenIn(0);
		globalVariable.setWomenOut(0);
		globalVariable.saveSharedPreferences();
	}

	boolean isFlashOn = false;
	Camera cam;

	public void onMenuItemSelected(View view) {
		int position = listView.getPositionForView(view);
		customDialog.dismiss();
		if (position == 0) {
			onDone(null);
		}
	}

	// count sending to db network call

	public void sendToDB() {

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
			sdf.setTimeZone(TimeZone.getTimeZone("gmt"));
			women_in += globalVariable.getIntervalWomenIn();
			women_out += globalVariable.getIntervalWomenOut();
			men_in += globalVariable.getIntervalMenIn();
			men_out += globalVariable.getIntervalMenOut();
			JSONObject jsonObject2 = null;
			String url = ServerURLs.URL + ServerURLs.COUNTER;
			JSONObject jsonObject = new JSONObject();
			jsonObject
					.put("women_in", women_in)
					.put("women_out", women_out)
					.put("men_in", men_in)
					.put("men_out", men_out)
					.put("time", sdf.format(new Date()))
					.put("business_id",
							globalVariable.getSelectedBusiness().getId()
									.get$oid());
			globalVariable.setIntervalMenIn(0);
			globalVariable.setIntervalMenOut(0);
			globalVariable.setIntervalWomenIn(0);
			globalVariable.setIntervalWomenOut(0);
			globalVariable.saveSharedPreferences();
			jsonObject2 = new JSONObject().put("counter", jsonObject);

			JsonObjectRequest jsonObjRequest = new JsonObjectRequest(
					Method.POST, url, jsonObject2,
					new Response.Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject arg0) {
							// TODO Auto-generated method stub
							men_in = 0;
							men_out = 0;
							women_in = 0;
							women_out = 0;
							try {
								globalVariable.setTotalInDB(arg0
										.getInt("total"));
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError arg0) {
							// TODO Auto-generated method stub
							if (arg0 instanceof NetworkError) {
							}
							if (arg0 instanceof NoConnectionError) {
							}
							if (arg0 instanceof ServerError) {
							}
						}

					});

			RetryPolicy policy = new DefaultRetryPolicy(30000,
					DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
					DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
			jsonObjRequest.setRetryPolicy(policy);
			queue.add(jsonObjRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
