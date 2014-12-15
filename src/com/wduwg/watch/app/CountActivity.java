package com.wduwg.watch.app;

import java.util.List;
import java.util.Timer;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mw.wduwg.adapter.ContextMenuAdapter;
import com.mw.wduwg.model.ContextMenuItem;
import com.mw.wduwg.services.CreateDialog;
import com.mw.wduwg.services.GlobalVariable;
import com.mw.wduwg.services.SchedulerCount;
import com.mw.wduwg.services.UpdateService;

public class CountActivity extends Activity implements OnTouchListener {

	TextView inMaleTV, currentMaleTV, outMaleTV, inFemaleTV, currentFemaleTV,
	outFemaleTV, totalHeaderTV, total_attendance;

	Typeface typeface;
	Typeface typeface2;
	
	Vibrator myVibrator;

	SharedPreferences sharedPreference;
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

	LayoutInflater inflater;

	ContextMenuAdapter adapter;

	View customActionBarView;
	ActionBar ab;	

	SharedPreferences sharedPref;

	int men_in = 0, men_out = 0, women_in = 0, women_out = 0;

	@Override
	protected void onPause() {
		super.onPause();
		globalVariable.saveSharedPreferences();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_count);
		globalVariable = (GlobalVariable) getApplicationContext();
		sharedPreference = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		typeface = Typeface.createFromAsset(getAssets(),"Fonts/OpenSans-Bold.ttf");
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
		total_attendance.setText("" + ((globalVariable.getMenIn() - globalVariable.getMenOut()) + (globalVariable.getWomenIn() - globalVariable.getWomenOut())));
		inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		createDialog = new CreateDialog(this);

		womenIn.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				headerTV.setText(globalVariable.getSelectedBusiness().getName()
						+ "\nTotal Attendance At server ->"
						+ globalVariable.getTotalInDB());
				customDialog.show();
				return false;
			}
		});
		womenOut.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				headerTV.setText(globalVariable.getSelectedBusiness().getName()
						+ "\nTotal Attendance At server -> "
						+ globalVariable.getTotalInDB());
				customDialog.show();
				return false;
			}
		});

		menIn.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				headerTV.setText(globalVariable.getSelectedBusiness().getName()
						+ "\nTotal Attendance At server -> "
						+ globalVariable.getTotalInDB());
				customDialog.show();
				return false;
			}
		});
		
		menOut.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				headerTV.setText(globalVariable.getSelectedBusiness().getName()
						+ "\nTotal Attendance At server -> "
						+ globalVariable.getTotalInDB());
				customDialog.show();
				return false;
			}
		});
		
		if(null == globalVariable.getTimer()){
			globalVariable.setTimer(new Timer());
			globalVariable.getTimer().scheduleAtFixedRate(new SchedulerCount(this , globalVariable.getIMEINo()), 0, 1000 * 10);
		}
	}

	public void menIn_watch(View v) {
		myVibrator.vibrate(300);
		globalVariable.setMenIn(globalVariable.getMenIn() + 1);
		globalVariable.saveSharedPreferences();
		inMaleTV.setText("" + globalVariable.getMenIn());
		int total = (globalVariable.getMenIn() - globalVariable.getMenOut()) + (globalVariable.getWomenIn() - globalVariable.getWomenOut());
		total_attendance.setText("" + total);

	}

	public void menOut_watch(View v) {
		myVibrator.vibrate(300);
		globalVariable.setMenOut(globalVariable.getMenOut() + 1);
		globalVariable.saveSharedPreferences();
		outMaleTV.setText("" + globalVariable.getMenOut());
		int total = (globalVariable.getMenIn() - globalVariable.getMenOut()) + (globalVariable.getWomenIn() - globalVariable.getWomenOut());
		total_attendance.setText("" + total);
	}

	public void womenIn_watch(View v) {
		myVibrator.vibrate(300);
		globalVariable.setWomenIn(globalVariable.getWomenIn() + 1);
		globalVariable.saveSharedPreferences();
		inFemaleTV.setText("" + globalVariable.getWomenIn());
		int total1 = (globalVariable.getMenIn() - globalVariable.getMenOut()) + (globalVariable.getWomenIn() - globalVariable.getWomenOut());
		total_attendance.setText("" + total1);
	}

	public void womenOut_watch(View v) {
		myVibrator.vibrate(300);
		globalVariable.setWomenOut(globalVariable.getWomenOut() + 1);
		globalVariable.saveSharedPreferences();
		outFemaleTV.setText("" + globalVariable.getWomenOut());
		int total = (globalVariable.getMenIn() - globalVariable.getMenOut()) + (globalVariable.getWomenIn() - globalVariable.getWomenOut());
		total_attendance.setText("" + total);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@SuppressLint("InflateParams")
	@Override
	protected void onResume() {
		super.onResume();
		myVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		child = inflater.inflate(R.layout.listview_context_menu, null);
		listView = (ListView) child.findViewById(R.id.listView_context_menu);
		headerTV = (TextView) child.findViewById(R.id.header_TV);
		headerTV.setTypeface(typeface);

		headerTV.setText(globalVariable.getSelectedBusiness().getName()
				+ "\n"
				+ "Total Attendance -> "
				+ (globalVariable.getTotalInDB() + globalVariable.getMenIn() + globalVariable.getWomenIn() - globalVariable.getWomenOut() - globalVariable.getMenOut()));

		customDialog = new Dialog(CountActivity.this);
		customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		customDialog.setContentView(child);
		customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		customDialog.setTitle("Options");
		customDialog.getWindow().getAttributes().verticalMargin = 0.2F;
		
		CountActivity.this.startService(new Intent(
				CountActivity.this, UpdateService.class));
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}
}
