package com.wduwg.watch.app;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mw.wduwg.adapter.AutoCompleteAdapter;
import com.mw.wduwg.model.Event;
import com.mw.wduwg.services.CreateDialog;
import com.mw.wduwg.services.GlobalVariable;
import com.mw.wduwg.services.JSONParser;
import com.mw.wduwg.services.MyAutoCompleteTextView;
import com.mw.wduwg.services.SchedulerCount;
import com.mw.wduwg.services.ServerURLs;
import com.parse.Parse;
import com.parse.ParseObject;

@SuppressLint("SimpleDateFormat")
public class AddEventActivity extends Activity {
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;

	// String name;
	GlobalVariable globalVariable;
	EditText startDateET, endDateET, startTimeET, endTimeET;
	MyAutoCompleteTextView nameACTV;
	TextView headerTV,skipTV,continueTV;


	static final int DATE_PICKER_ID_Start = 1111;
	static final int DATE_PICKER_ID_end = 2222;
	static final int TIME_PICKER_ID_start = 3333;
	static final int TIME_PICKER_ID_end = 4444;

	static int RANDOM_NUMBER = 1;

	Typeface typeface;
	Typeface typeface2;


	// Date startDate;
	Date startDateTime = new Date();

	Date endDate;
	Date endDateTime;

	CreateDialog createDialog;
	ProgressDialog progressDialog;
	AlertDialog.Builder alertDialogBuilder;
	AlertDialog alertDialog;

	Intent nextIntent;
	ParseObject parentBusinessPO;
	List<ParseObject> eventsList;
	
	// boolean status
	boolean isAdded ;
	boolean isDefaultEvent = false;

	SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d");
	SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm");
	SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_event);
        System.out.println(">>>>>>> AddEvent:: onCreate");
		findThings();
		initializeThings();
		if(getIntent().hasExtra("from_event"))
		{
			skipTV.setVisibility(View.INVISIBLE);
		}
		else
		{
			skipTV.setVisibility(View.VISIBLE);
		}
		actionBarAndKeyboardAndListener();
        checkPreferences();
		Calendar calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);

		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);
        
		onTouchListeners();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_PICKER_ID_Start:
			return new DatePickerDialog(this, startDatePickerListener, year,
					month, day);
		case DATE_PICKER_ID_end:
			return new DatePickerDialog(this, endDatePickerListener, year,
					month, day);
		case TIME_PICKER_ID_start:
			return new TimePickerDialog(this, timePickerListener, hour, minute,
					true);
		case TIME_PICKER_ID_end:
			return new TimePickerDialog(this, timePickerListener1, hour,
					minute, true);
		}
		return null;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RANDOM_NUMBER) {
			if (resultCode == CountActivity.MOVE_ANOTHER_STEP_BACK) {
				finish();
			} else if (resultCode == CountActivity.MOVE_BACK) {
				checkPreferences();
			}
		}
	}
	

	private void findThings() {
		nameACTV = (MyAutoCompleteTextView) findViewById(R.id.event_name_editText);
		startDateET = (EditText) findViewById(R.id.startsLabel);
		endDateET = (EditText) findViewById(R.id.endsLabel);
		startTimeET = (EditText) findViewById(R.id.startstime);
		endTimeET = (EditText) findViewById(R.id.endstime);
		headerTV = (TextView) findViewById(R.id.header_TV);
		skipTV = (TextView)findViewById(R.id.skipTV);
		continueTV = (TextView)findViewById(R.id.next_page_continueTV);
	}

	private void initializeThings() {
		typeface2 = Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Light.ttf");
		typeface = Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Bold.ttf");
		sdf.setTimeZone(TimeZone.getTimeZone("US/Central"));
		nameACTV.setTypeface(typeface);
		endDateET.setTypeface(typeface2);
		startDateET.setTypeface(typeface2);
		headerTV.setTypeface(typeface);
		skipTV.setTypeface(typeface);
		continueTV.setTypeface(typeface);
		globalVariable = (GlobalVariable) getApplicationContext();
		createDialog = new CreateDialog(this);
		startDateET.setText(dateFormat.format(startDateTime));
		startTimeET.setText(dateFormat2.format(startDateTime));
	}

	private void actionBarAndKeyboardAndListener() {
		int titleId = getResources().getIdentifier("action_bar_title", "id",
				"android");
		TextView yourTextView = (TextView) findViewById(titleId);
		yourTextView.setTextSize(19);
		yourTextView.setTextColor(Color.parseColor("#016AB2"));
		
		yourTextView.setTypeface(typeface);

		((RelativeLayout) findViewById(R.id.add_event_LL))
				.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(getCurrentFocus()
								.getWindowToken(), 0);
						return false;
					}
				});

		nameACTV.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				nameACTV.setError(null);
			}
		});
	}

	private void onTouchListeners() {
		startDateET.setOnTouchListener(new OnTouchListener() {
			@SuppressWarnings("deprecation")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				((EditText) v).setVisibility(View.GONE);

				showDialog(DATE_PICKER_ID_Start);
				return false;
			}
		});

		endDateET.setOnTouchListener(new OnTouchListener() {

			@Override
			@SuppressWarnings("deprecation")
			public boolean onTouch(View v, MotionEvent event) {
				((EditText) v).setVisibility(View.GONE);
				showDialog(DATE_PICKER_ID_end);
				return false;
			}
		});

		startTimeET.setOnTouchListener(new OnTouchListener() {

			@Override
			@SuppressWarnings("deprecation")
			public boolean onTouch(View v, MotionEvent event) {
				showDialog(TIME_PICKER_ID_start);
				return false;
			}
		});

		endTimeET.setOnTouchListener(new OnTouchListener() {

			@Override
			@SuppressWarnings("deprecation")
			public boolean onTouch(View v, MotionEvent event) {
				showDialog(TIME_PICKER_ID_end);
				return false;
			}
		});
	}

	private void checkPreferences() {
		if (globalVariable.getSelectedEvent() != null) {
			System.out.println(">>>>>>> selected event"+globalVariable.getSelectedEvent().getName());
			alertDialogBuilder = createDialog
					.createAlertDialog(
							"Event Exists",
							"You are already counting for an event. Do you wish to start a new count?",
							false);
			alertDialogBuilder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							SchedulerCount scheduledTask = new SchedulerCount(AddEventActivity.this);
							Timer timer = new Timer();
							timer.scheduleAtFixedRate(scheduledTask, 1000, 10000);
							scheduledTask.run();
							SchedulerCount.event = globalVariable.getSelectedEvent();
							timer.cancel();
							globalVariable.setSelectedEvent(null);
							globalVariable.setSelectedReportsEvent(null);
							globalVariable.setMenIn(0);
							globalVariable.setMenOut(0);
							globalVariable.setWomenIn(0);
							globalVariable.setWomenOut(0);
							globalVariable.saveSharedPreferences();
							dialog.dismiss();
						}
					});

			alertDialogBuilder.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
							nextActivity();
						}
					});
			alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}
		
	}

	private void nextActivity() {
		if(getIntent().hasExtra("from_event"))
		{
			nextIntent = new Intent(AddEventActivity.this, EventActivity.class);
			startActivityForResult(nextIntent, 100);
		}
		else
		{
		nextIntent = new Intent(AddEventActivity.this, CountActivity.class);
		startActivityForResult(nextIntent, RANDOM_NUMBER);
		}
		overridePendingTransition(R.anim.anim_out, R.anim.anim_in);
	}

	private Date getDateFromCalendar(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(year, month, day, 0, 0, 0);
		return cal.getTime();
	}

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

			System.out.println(">>>>>>> start date  :  " + startDateTime);

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

			System.out.println(">>>>>>> end date  :  " + endDate);

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

	public void onSkip(View view) {
		isDefaultEvent = true;

		progressDialog = createDialog.createProgressDialog("Saving",
				"Please wait for a while.", true, null);
		progressDialog.show();

		SaveEventToParseAndPreferencesAsync asyncTask = new SaveEventToParseAndPreferencesAsync();
		asyncTask.execute(new String[] { "Hello World" });
	}

	public void onContinue(View v) {
		if (!validate())
			return;

		progressDialog = createDialog.createProgressDialog("Saving",
				"Please wait for a while.", true, null);
		progressDialog.show();

		SaveEventToParseAndPreferencesAsync asyncTask = new SaveEventToParseAndPreferencesAsync();
		asyncTask.execute(new String[] { "Hello World" });

	}

	private boolean validate() {
		endDateET.setError(null);
		boolean bool = true;
		if (nameACTV.getText().toString().trim().length() == 0) {
			nameACTV.setError("Enter name");
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
			System.out.println(">>>>>>> start date before:"+startDateTime);
			startDateTime = createDate(startDateTime, startTimeET.getText()
					.toString());
			System.out.println(">>>>>>> start date after"+startDateTime);
		}
		if (endDateET.getText().toString().trim().length() > 0
				&& endTimeET.getText().toString().trim().length() > 0) {
			System.out.println(">>>>>>> end date before"+endDate);
			endDateTime = createDate(endDate, endTimeET.getText().toString());
			System.out.println(">>>>>>> end date after"+endDateTime);
		}

		if (endDateTime != null && startDateTime.compareTo(endDateTime) >= 0) {
			endDateET.setError("");
			Toast.makeText(this, "End date or time must be After start date and time",
					Toast.LENGTH_LONG).show();
			bool = false;
		}
		return bool;
	}

	private Date createDate(Date date, String time) {
		Date finalDate = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
		String dateTime = dateFormat.format(date) + " " + time;
		System.out.println(">>>>>>> middle  : "+dateTime);
		SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm");
		formatter.setTimeZone(TimeZone.getTimeZone("US/Central"));
		try {
			finalDate = new Date(formatter.format(new Date(dateTime)));
//			finalDate = formatter.parse(dateTime);
			System.out.println(">>>>>>> US Central date is  :  " + finalDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return finalDate;
	}

	JSONParser jParser;
	JSONObject jsonFromServer;

	private class SaveEventToParseAndPreferencesAsync extends
			AsyncTask<String, Void, Void> {
		boolean isEventAlreadyThere = false;
//		ParseObject eventPO;

		
		@Override
		protected Void doInBackground(String... params) {
			jParser = new JSONParser();
			

			if (globalVariable == null)
				System.out.println(">>>>>>> global im  null");
			if (globalVariable.getSelectedBusiness() == null)
				System.out.println(">>>>>>> sel bus im null");
             startDateTime = createDate(startDateTime, startTimeET.getText().toString());
			String url = ServerURLs.URL + ServerURLs.EVENTS;
			System.out.println(">>>>>>> url is   : " + url);
			System.out.println(">>>>>>> selectedBusinessId:"+globalVariable.getSelectedBusiness().getId().get$oid() );
			JSONObject jsonObject2 = null;
			try {
				JSONObject jsonObject;
				if (isDefaultEvent)
					
					jsonObject = new JSONObject()
							.put("name", "defaultEvent")
//							.put("start_date_time", sdf.format(new Date(startTimeET.getText().toString())))
							.put("start_date_time", startDateTime)
							.put("business_id",
									globalVariable.getSelectedBusiness()
											.getId().get$oid());
				else
					jsonObject = new JSONObject()
							.put("name", nameACTV.getText().toString().trim())
							.put("start_date_time", startDateTime)
							.put("end_date_time", endDateTime)
							.put("business_id",
									globalVariable.getSelectedBusiness()
											.getId().get$oid());
				jsonObject2 = new JSONObject().put("event", jsonObject);
				jsonFromServer = jParser.getJSONFromUrlAfterHttpPost(url,
						jsonObject2);
				
				
					Gson gson = new Gson();
				    
				    
				    if(!jsonFromServer.has("error"))
				    {
				    	Event event = gson.fromJson(jsonFromServer.toString(), Event.class);
					    System.out.println(">>>>>>> Event start date: "+ event.getStartDate());
					    if(event.getStartDate().equalsIgnoreCase("is already taken"))
						{
						    isAdded = false;
						}
					    else{
					    	globalVariable.setSelectedEvent(event);
					    	System.out.println("while skip inside addevent global slected event"+globalVariable.getSelectedEvent().getName());
					    	isAdded = true;
					    }
				    }

			} catch (JSONException e) {
				e.printStackTrace();
			}

			
			return null;
			
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			if(isAdded)
			{
				globalVariable.setMenIn(0);
				globalVariable.setMenOut(0);
				globalVariable.setWomenIn(0);
				globalVariable.setWomenOut(0);
				nextActivity();	
			}
			else{
				Toast.makeText(getApplicationContext(), "This Event already exists at same time", Toast.LENGTH_SHORT).show();
//				finish();
			}
		
			isDefaultEvent = false;
		}// onPostExecute

	}// Async Task

	

}
