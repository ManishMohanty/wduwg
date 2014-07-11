package com.example.wduwg.tiles;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.apphance.android.Log;
import com.mw.wduwg.model.Special;
import com.mw.wduwg.services.CreateDialog;
import com.mw.wduwg.services.GlobalVariable;
import com.mw.wduwg.services.JSONParser;
import com.mw.wduwg.services.ServerURLs;
import com.parse.Parse;

public class AddSpecialActivity extends Activity {

	CreateDialog createDialog;
	ProgressDialog progressDialog;
	GlobalVariable globalVariable;
	AlertDialog.Builder alertDialogBuilder;
	AlertDialog alertDialog;
	EditText nameET, startDateET, endDateET, startTimeET, endTimeET;
	TextView continueTV,deleteSpecial,headerTV;

	Typeface typeface2;

	static final int DATE_PICKER_ID_StartSPCL = 1111;
	static final int DATE_PICKER_ID_endSPCL = 2222;
	static final int TIME_PICKER_ID_startSPCL = 3333;
	static final int TIME_PICKER_ID_endSPCL = 4444;

	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;

	// Date startDate;
	Date startDateTime = new Date();

	Date endDate;
	Date endDateTime;
	
	Typeface typefaceBold;

	SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d");
	SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm");
	
	Special selectedSpecial;

	private void findThings() {
		nameET = (EditText) findViewById(R.id.nameET);
		startDateET = (EditText) findViewById(R.id.startsLabel);
		endDateET = (EditText) findViewById(R.id.endsLabel);
		startTimeET = (EditText) findViewById(R.id.startstime);
		endTimeET = (EditText) findViewById(R.id.endstime);
		continueTV = (TextView)findViewById(R.id.continueTV);
		headerTV = (TextView)findViewById(R.id.header_TV);
		deleteSpecial = (TextView)findViewById(R.id.deleteSpecial);
	}

	private void initializeThings() {
		Parse.initialize(this, "mTkWVlwx9IsCpHvGaYG6zZOa5tB9NuIokmlSWQMS",
				"6H7bzx0gZL7I2Zz9MQ1u5ZPYaOhlHAkqeyooexCS");
		typeface2 = Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Light.ttf");
	    typefaceBold = Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Bold.ttf");
	    continueTV.setTypeface(typefaceBold);
	    deleteSpecial.setTypeface(typefaceBold);
		nameET.setTypeface(typeface2);
		endDateET.setTypeface(typeface2);
		startDateET.setTypeface(typeface2);

		globalVariable = (GlobalVariable) getApplicationContext();
		createDialog = new CreateDialog(this);
		progressDialog = createDialog.createProgressDialog("Loading",
				"Please wait for a while", true, null);

		// nameET.setText(dateFormat3.format(startDateTime));
		startDateET.setText(dateFormat.format(startDateTime));
		startTimeET.setText(dateFormat2.format(startDateTime));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addspecial);

		findThings();
		initializeThings();
		
		if(getIntent().hasExtra("special")&& ! getIntent().hasExtra("newAdd"))
		{
			selectedSpecial = (Special)getIntent().getSerializableExtra("special");
			nameET.setText(selectedSpecial.getName());
			nameET.setKeyListener(null);
			nameET.setFocusable(false);
			nameET.setFocusableInTouchMode(false);
			nameET.setClickable(false);
			startDateET.setText(selectedSpecial.getStartDate().substring(7, selectedSpecial.getStartDate().length()));
			startDateET.setKeyListener(null);
			startDateET.setFocusable(false);
			startDateET.setFocusableInTouchMode(false);
			startDateET.setClickable(false);
			System.out.println(">>>>>>> satrt date:"+selectedSpecial.getStartDate());
			endDateET.setText(selectedSpecial.getEndDate().substring(7, selectedSpecial.getEndDate().length()));
			endDateET.setKeyListener(null);
			endDateET.setFocusable(false);
			endDateET.setFocusableInTouchMode(false);
			endDateET.setClickable(false);
			System.out.println(">>>>>>> end date:"+selectedSpecial.getEndDate());
			startTimeET.setText(selectedSpecial.getStartDate().substring(0, 6));
			startTimeET.setKeyListener(null);
			startTimeET.setFocusable(false);
			startTimeET.setFocusableInTouchMode(false);
			startTimeET.setClickable(false);
			endTimeET.setText(selectedSpecial.getEndDate().substring(0, 6));
			endTimeET.setKeyListener(null);
			endTimeET.setFocusable(false);
			endTimeET.setFocusableInTouchMode(false);
			endTimeET.setClickable(false);
			
			ActionBar actionbar = getActionBar();
			LayoutInflater inflater =(LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View customActionBar = inflater.inflate(R.layout.custom_action_bar, null);
			TextView title = (TextView)customActionBar.findViewById(R.id.action_bar_TV);
			title.setText("Special Details");
			title.setTextSize(19);
			
			title.setTypeface(typefaceBold);
			actionbar.setDisplayShowCustomEnabled(true);
			actionbar.setCustomView(customActionBar);
			continueTV.setVisibility(View.GONE);
			headerTV.setVisibility(View.GONE);
		}else
		{
		deleteSpecial.setVisibility(View.GONE);
		Calendar calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);
		// year = Calendar.YEAR;

		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);

		onTouchListeners();
		}
	}

	public void onDelete(View v)
	{
		progressDialog = createDialog.createProgressDialog("Saving",
				"Please wait for a while.", true, null);
		progressDialog.show();
		DeleteAsyncTask asyncTask = new DeleteAsyncTask();
		asyncTask.execute();
	}
	
	
	private class DeleteAsyncTask extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONParser jParser = new JSONParser();
			jParser.deleteObject("http://dcounter.herokuapp.com/specials/", selectedSpecial.getId().get$oid() );
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressDialog.dismiss();
			Toast.makeText(AddSpecialActivity.this, "selected Special deleted", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(AddSpecialActivity.this,BusinessDashboardActivity.class);
			startActivity(intent);
		}
		
		
	}
	
	public void onDone(View v)
	{
		Intent intent = new Intent(this,BusinessDashboardActivity.class);
		intent.putExtra("isFromMain", true);
		startActivity(intent);
		overridePendingTransition(R.anim.anim_out, R.anim.anim_in);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		int titleId = getResources().getIdentifier("action_bar_title", "id",
				"android");
		TextView yourTextView = (TextView) findViewById(titleId);
		yourTextView.setTextColor(Color.parseColor("#016AB2"));
		yourTextView.setTextSize(19);
		yourTextView.setTypeface(Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Bold.ttf"));
	}

	public class LoadStringsAsync extends AsyncTask<Void, Void, Void> {

		// new thread for imagedownloading res
		List<String> specialList = new ArrayList<String>();

		public LoadStringsAsync() {

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			JSONObject jsonObject2 = null;
			try {
				String url = ServerURLs.URL + ServerURLs.SPECIALS;
				JSONParser jsonparser = new JSONParser(AddSpecialActivity.this);
				JSONObject newObject = new JSONObject();
				newObject.put("name", nameET.getText().toString());
				newObject.put("business_id", globalVariable
						.getSelectedBusiness().getId().get$oid());
				newObject.put("start_date_time", startDateTime);
				newObject.put("end_date_time", endDateTime);
				jsonObject2 = new JSONObject().put("special", newObject);
				jsonparser.getJSONFromUrlAfterHttpPost(url, jsonObject2);

			} catch (Exception e) {
				Log.d("Response========", "inside catch");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void arg0) {
			progressDialog.dismiss();
			Intent intent = new Intent(AddSpecialActivity.this,
					SpecialActivity.class);
			startActivity(intent);
		}

	}

	public void save(View v) {

		if (!validate())
			return;
		
			progressDialog.show();
			LoadStringsAsync asyncTask = new LoadStringsAsync();
			asyncTask.execute();
		
	}

	private void onTouchListeners() {
		startDateET.setOnTouchListener(new OnTouchListener() {
			@SuppressWarnings("deprecation")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				((EditText) v).setVisibility(View.GONE);

				showDialog(DATE_PICKER_ID_StartSPCL);
				return false;
			}
		});

		endDateET.setOnTouchListener(new OnTouchListener() {

			@Override
			@SuppressWarnings("deprecation")
			public boolean onTouch(View v, MotionEvent event) {
				((EditText) v).setVisibility(View.GONE);
				showDialog(DATE_PICKER_ID_endSPCL);
				return false;
			}
		});

		startTimeET.setOnTouchListener(new OnTouchListener() {

			@Override
			@SuppressWarnings("deprecation")
			public boolean onTouch(View v, MotionEvent event) {
				showDialog(TIME_PICKER_ID_startSPCL);
				return false;
			}
		});

		endTimeET.setOnTouchListener(new OnTouchListener() {

			@Override
			@SuppressWarnings("deprecation")
			public boolean onTouch(View v, MotionEvent event) {
				showDialog(TIME_PICKER_ID_endSPCL);
				return false;
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_PICKER_ID_StartSPCL:
			return new DatePickerDialog(this, startDatePickerListener, year,
					month, day);
		case DATE_PICKER_ID_endSPCL:
			return new DatePickerDialog(this, endDatePickerListener, year,
					month, day);
		case TIME_PICKER_ID_startSPCL:
			return new TimePickerDialog(this, timePickerListener, hour, minute,
					true);
		case TIME_PICKER_ID_endSPCL:
			return new TimePickerDialog(this, timePickerListener1, hour,
					minute, true);
		}
		return null;
	}

	private Date getDateFromCalendar(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(year, month, day, 0, 0, 0);
		return cal.getTime();
	}

	// date and time listener
	private DatePickerDialog.OnDateSetListener startDatePickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		@Override
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			startDateET.setError(null);
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;
			startDateTime = getDateFromCalendar(year, month, day);

			System.out.println("start date  :  " + startDateTime);

			startDateET.setText(dateFormat.format(startDateTime));
			startDateET.setVisibility(View.VISIBLE);
		}
	};

	private DatePickerDialog.OnDateSetListener endDatePickerListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			endDateET.setError(null);
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;
			endDate = getDateFromCalendar(year, month, day);

			System.out.println("end date  :  " + endDate);

			endDateET.setText(dateFormat.format(endDate));
			endDateET.setVisibility(View.VISIBLE);
		}
	};

	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minuteofHour) {
			startTimeET.setError(null);
			String hh, mm;
			hour = hourOfDay;
			if (hour < 10) {
				hh = "0" + hour;
			} else {
				hh = "" + hour;
			}
			if (minuteofHour < 10) {
				mm = "0" + minuteofHour;
			} else {
				mm = "" + minuteofHour;
			}
			minute = minuteofHour;
			startTimeET.setText(hh + ":" + mm);
		}
	};

	private TimePickerDialog.OnTimeSetListener timePickerListener1 = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minuteofHour) {
			endTimeET.setError(null);
			String hh, mm;
			hour = hourOfDay;
			if (hour < 10) {
				hh = "0" + hour;
			} else {
				hh = "" + hour;
			}
			if (minuteofHour < 10) {
				mm = "0" + minuteofHour;
			} else {
				mm = "" + minuteofHour;
			}
			minute = minuteofHour;
			endTimeET.setText(hh + ":" + mm);
		}
	};

	private boolean validate() {
		endDateET.setError(null);
		boolean bool = true;
		if (nameET.getText().toString().trim().length() == 0) {
			nameET.setError("Enter name");
			bool = false;
		}
		if (startDateET.getText().toString().trim().length() == 0) {
			startDateET.setError("Enter start date");
			bool = false;
		}
		if (startTimeET.getText().toString().trim().length() == 0) {
			startTimeET.setError("Enter start time");
			bool = false;
		}
		if (endDateET.getText().toString().trim().length() == 0) {
			endDateET.setError("Enter end date");
			bool = false;
		}
		if (endTimeET.getText().toString().trim().length() == 0) {
			endTimeET.setError("Enter end time");
			bool = false;
		}
		if (startDateET.getText().toString().trim().length() > 0
				&& startTimeET.getText().toString().trim().length() > 0) {
			startDateTime = createDate(startDateTime, startTimeET.getText()
					.toString());
			System.out.println(startDateTime);
		}
		if (endDateET.getText().toString().trim().length() > 0
				&& endTimeET.getText().toString().trim().length() > 0) {
			endDateTime = createDate(endDate, endTimeET.getText().toString());
			System.out.println(endDateTime);
		}
		Date d = new Date();
		String time = d.getHours() + ":"+d.getMinutes()+":"+d.getSeconds();
		if (startDateTime.compareTo(createDate(d, time)) >= 0) {

			if (endDateTime != null
					&& startDateTime.compareTo(endDateTime) >= 0) {
				endDateET.setError("");
				Toast.makeText(this,
						"End date must be greater than start date.",
						Toast.LENGTH_LONG).show();
				bool = false;
			}
		}else
		{
			alertDialogBuilder = createDialog
					.createAlertDialog(
							"Error",
							"Start date and time must be after Current date and time",
							false);
			alertDialogBuilder.setPositiveButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							alertDialog.dismiss();
						}
			});
			alertDialog = alertDialogBuilder.create();
			alertDialog.show();
			bool = false;
		}
		return bool;
	}

	private Date createDate(Date date, String time) {
		Date finalDate = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		String dateTime = dateFormat.format(date) + " " + time;
		System.out.println(dateTime);
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		try {
			finalDate = formatter.parse(dateTime);
			System.out.println("date is  :  " + finalDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return finalDate;
	}

}
