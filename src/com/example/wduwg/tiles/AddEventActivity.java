package com.example.wduwg.tiles;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.text.DateFormat;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
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
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.android.Facebook;
import com.google.gson.Gson;
import com.mw.wduwg.model.Event;
import com.mw.wduwg.services.CreateDialog;
import com.mw.wduwg.services.GlobalVariable;
import com.mw.wduwg.services.JSONParser;
import com.mw.wduwg.services.MyAutoCompleteTextView;
import com.mw.wduwg.services.SchedulerCount;
import com.mw.wduwg.services.ServerURLs;
import com.parse.ParseObject;
import com.parse.entity.mime.HttpMultipartMode;
import com.parse.entity.mime.MultipartEntity;
import com.parse.entity.mime.content.ByteArrayBody;
import com.parse.entity.mime.content.StringBody;

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
	TextView headerTV, continueTV, deleteEvent;
	int[] drawableArray = { R.drawable.bar1, R.drawable.bar2, R.drawable.bar3,
			R.drawable.bar4, R.drawable.bar5, R.drawable.bar6, R.drawable.bar7,
			R.drawable.bar8, R.drawable.bar10 };

	static final int DATE_PICKER_ID_Start = 1111;
	static final int DATE_PICKER_ID_end = 2222;
	static final int TIME_PICKER_ID_start = 3333;
	static final int TIME_PICKER_ID_end = 4444;

	static int RANDOM_NUMBER = 1;

	Typeface typeface;
	Typeface typeface2;
	Event event;

	// Date startDate;
	Date startDateTime = new Date();

	Date endDate;
	Date endDateTime;

	Event selectedEvent;

	CreateDialog createDialog;
	ProgressDialog progressDialog;
	AlertDialog.Builder alertDialogBuilder;
	AlertDialog alertDialog;

	Intent nextIntent;
	ParseObject parentBusinessPO;
	List<ParseObject> eventsList;

	// boolean status
	boolean isAdded;
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(AddEventActivity.this , BusinessDashboardActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		super.onBackPressed();
	}

	boolean isDefaultEvent = false;

	SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d");
	SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm");

	// SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_event);
		System.out.println(">>>>>>> AddEvent:: onCreate");
		findThings();
		initializeThings();

		if (getIntent().hasExtra("event") && !getIntent().hasExtra("addNew")) {
			selectedEvent = (Event) getIntent().getSerializableExtra("event");
			nameACTV.setText(selectedEvent.getName());
			nameACTV.setKeyListener(null);
			nameACTV.setFocusable(false);
			nameACTV.setFocusableInTouchMode(false);
			nameACTV.setClickable(false);
			startDateET.setText(selectedEvent.getStartDate().split(",")[0]);
			startDateET.setKeyListener(null);
			startDateET.setFocusable(false);
			startDateET.setFocusableInTouchMode(false);
			startDateET.setClickable(false);
			System.out.println(">>>>>>> satrt date:"
					+ selectedEvent.getStartDate());
			endDateET.setText(selectedEvent.getEndDate().split(",")[0]);
			endDateET.setKeyListener(null);
			endDateET.setFocusable(false);
			endDateET.setFocusableInTouchMode(false);
			endDateET.setClickable(false);
			System.out
					.println(">>>>>>> end date:" + selectedEvent.getEndDate());
			startTimeET.setText(selectedEvent.getStartDate().split(",")[1]);
			startTimeET.setKeyListener(null);
			startTimeET.setFocusable(false);
			startTimeET.setFocusableInTouchMode(false);
			startTimeET.setClickable(false);
			endTimeET.setText(selectedEvent.getEndDate().split(",")[1]);
			endTimeET.setKeyListener(null);
			endTimeET.setFocusable(false);
			endTimeET.setFocusableInTouchMode(false);
			endTimeET.setClickable(false);
			ActionBar actionbar = getActionBar();
			LayoutInflater inflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View customActionBar = inflater.inflate(R.layout.custom_action_bar,
					null);
			TextView title = (TextView) customActionBar
					.findViewById(R.id.action_bar_TV);
			title.setText("Event Details");
			title.setTextSize(19);
			Typeface font = Typeface.createFromAsset(getAssets(),
					"Fonts/OpenSans-Bold.ttf");
			title.setTypeface(font);
			actionbar.setDisplayShowCustomEnabled(true);
			actionbar.setCustomView(customActionBar);
			continueTV.setVisibility(View.GONE);
			headerTV.setVisibility(View.GONE);
		} else {
			deleteEvent.setVisibility(View.GONE);
			actionBarAndKeyboardAndListener();
			// checkPreferences();
			Calendar calendar = Calendar.getInstance();
			year = calendar.get(Calendar.YEAR);

			month = calendar.get(Calendar.MONTH);
			day = calendar.get(Calendar.DAY_OF_MONTH);
			hour = calendar.get(Calendar.HOUR_OF_DAY);
			minute = calendar.get(Calendar.MINUTE);

			onTouchListeners();
		}
	}

	public void onDone(View v) {
		Intent intent = new Intent(this, BusinessDashboardActivity.class);
		intent.putExtra("isFromMain", true);
		startActivity(intent);
		overridePendingTransition(R.anim.anim_out, R.anim.anim_in);
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
		continueTV = (TextView) findViewById(R.id.next_page_continueTV);
		deleteEvent = (TextView) findViewById(R.id.deleteEvent);
	}

	private void initializeThings() {
		typeface2 = Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Light.ttf");
		typeface = Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Bold.ttf");
		// sdf.setTimeZone(TimeZone.getTimeZone("US/Central"));
		nameACTV.setTypeface(typeface);
		endDateET.setTypeface(typeface2);
		startDateET.setTypeface(typeface2);
		headerTV.setTypeface(typeface);
		continueTV.setTypeface(typeface);
		deleteEvent.setTypeface(typeface);
		globalVariable = (GlobalVariable) getApplicationContext();
		createDialog = new CreateDialog(this);
		startDateET.setText(dateFormat.format(startDateTime));
		startTimeET.setText(dateFormat2.format(startDateTime));
		startDateET.setFocusable(false);
		startTimeET.setFocusable(false);
		endDateET.setFocusable(false);
		endTimeET.setFocusable(false);
	}

	private void actionBarAndKeyboardAndListener() {
		int titleId = getResources().getIdentifier("action_bar_title", "id",
				"android");
		TextView yourTextView = (TextView) findViewById(titleId);
		yourTextView.setTextSize(19);
		yourTextView.setTextColor(Color.parseColor("#016AB2"));

		yourTextView.setTypeface(typeface);

		((ScrollView) findViewById(R.id.my_scrollview))
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
//				((EditText) v).setVisibility(View.GONE);

				showDialog(DATE_PICKER_ID_Start);
				return false;
			}
		});

		endDateET.setOnTouchListener(new OnTouchListener() {

			@Override
			@SuppressWarnings("deprecation")
			public boolean onTouch(View v, MotionEvent event) {
//				((EditText) v).setVisibility(View.GONE);
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
		if (globalVariable.getSelectedEvent() != null
				&& !getIntent().hasExtra("from_event")) {
			System.out.println(">>>>>>> selected event"
					+ globalVariable.getSelectedEvent().getName());
			alertDialogBuilder = createDialog
					.createAlertDialog(
							"Event Exists",
							"You are already counting for an event. Do you wish to start a new count?",
							false);
			alertDialogBuilder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							SchedulerCount scheduledTask = new SchedulerCount(
									AddEventActivity.this);
							Timer timer = new Timer();
							timer.scheduleAtFixedRate(scheduledTask, 1000,
									10000);
							scheduledTask.run();
							SchedulerCount.event = globalVariable
									.getSelectedEvent();
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
		// if(getIntent().hasExtra("from_event"))
		// {
		nextIntent = new Intent(AddEventActivity.this, EventActivity.class);
		startActivityForResult(nextIntent, 100);
		// }
		// else
		// {
		// nextIntent = new Intent(AddEventActivity.this, CountActivity.class);
		// startActivityForResult(nextIntent, RANDOM_NUMBER);
		// }
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

		progressDialog = createDialog.createProgressDialog("Deleting",
				"Please wait while we delete this Event.", true, null);
		progressDialog.show();

		SaveEventToParseAndPreferencesAsync asyncTask = new SaveEventToParseAndPreferencesAsync();
		asyncTask.execute(new String[] { "Hello World" });
	}

	public void onDelete(View v) {
		progressDialog = createDialog.createProgressDialog("Deleting",
				"Please wait while we delete this Event.", true, null);
		progressDialog.show();
		DeleteAsyncTask asyncTask = new DeleteAsyncTask();
		asyncTask.execute();
	}

	public void onContinue(View v) {
		if (!validate())
			return;

		progressDialog = createDialog.createProgressDialog("Loading",
				"Please wait while we register your event.", true, null);
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
			System.out.println(">>>>>>> start date before:" + startDateTime);
			startDateTime = createDate(startDateTime, startTimeET.getText()
					.toString());
			System.out.println(">>>>>>> start date after" + startDateTime);
		}
		if (endDateET.getText().toString().trim().length() > 0
				&& endTimeET.getText().toString().trim().length() > 0) {
			System.out.println(">>>>>>> end date before" + endDate);
			endDateTime = createDate(endDate, endTimeET.getText().toString());
			System.out.println(">>>>>>> end date after" + endDateTime);
		}

		Date d = new Date();
		String time = d.getHours() + ":" + d.getMinutes() + ":"
				+ d.getSeconds();
		if (startDateTime.compareTo(createDate(d, time)) >= 0) {

			if (endDateTime != null
					&& startDateTime.compareTo(endDateTime) >= 0) {
				endDateET.setError("");
				Toast.makeText(this,
						"End date or time must be After start date and time",
						Toast.LENGTH_LONG).show();
				bool = false;
			}
		} else {
			alertDialogBuilder = createDialog.createAlertDialog("Error",
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
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
		String dateTime = dateFormat.format(date) + " " + time;
		System.out.println(">>>>>>> middle  : " + dateTime);
		SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm");
		formatter.setTimeZone(TimeZone.getTimeZone("US/Central"));
		try {
			finalDate = new Date(formatter.format(new Date(dateTime)));
			// finalDate = formatter.parse(dateTime);
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

		// ParseObject eventPO;

		@Override
		protected Void doInBackground(String... params) {
			jParser = new JSONParser();

			if (globalVariable == null)
				System.out.println(">>>>>>> global im  null");
			if (globalVariable.getSelectedBusiness() == null)
				System.out.println(">>>>>>> sel bus im null");
			startDateTime = createDate(startDateTime, startTimeET.getText()
					.toString());
			String url = ServerURLs.URL + ServerURLs.EVENTS;
			System.out.println(">>>>>>> url is   : " + url);
			System.out.println(">>>>>>> selectedBusinessId:"
					+ globalVariable.getSelectedBusiness().getId().get$oid());
			JSONObject jsonObject2 = null;
			try {
				JSONObject jsonObject;
				if (isDefaultEvent)

					jsonObject = new JSONObject()
							.put("name", "defaultEvent")
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

				if (!jsonFromServer.has("error")) {
					selectedEvent = gson.fromJson(jsonFromServer.toString(),
							Event.class);
					if (!selectedEvent.getStartDate().equalsIgnoreCase(
							"is already taken")) {
					selectedEvent.setStartDate(globalVariable.convertDate(jsonFromServer.getString("start_date_time").substring(0, 16)));
					selectedEvent.setEndDate(globalVariable.convertDate(jsonFromServer.getString("end_date_time").substring(0, 16)));
					System.out.println(">>>>>>> Event start date: "
							+ selectedEvent.getStartDate());
						isAdded = true;
						globalVariable.setSelectedEvent(selectedEvent);
					} else {
						isAdded = false;
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

			if (isAdded) {
				globalVariable.setMenIn(0);
				globalVariable.setMenOut(0);
				globalVariable.setWomenIn(0);
				globalVariable.setWomenOut(0);
				FacebookPostAsyncExample facebookAsync = new FacebookPostAsyncExample();
				facebookAsync.execute("heloo");
				alertDialogBuilder = createDialog.createAlertDialog(
						"Event added successfully", null, false);
				alertDialogBuilder.setCancelable(false);
				alertDialogBuilder.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								alertDialog.dismiss();
								Intent intent = new Intent(AddEventActivity.this,EventActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(intent);
							}
						});
			} else {
				alertDialogBuilder = createDialog.createAlertDialog(
						"There is already an Event with the same name and start time. Please change the name or the start time to create a new Event.", null, false);
				alertDialogBuilder.setCancelable(false);
				alertDialogBuilder.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								alertDialog.dismiss();
							}
						});
			}
			alertDialog = alertDialogBuilder.create();
			alertDialog.show();
			isDefaultEvent = false;
		}// onPostExecute

	}// end Async Task for add record

	private class DeleteAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			jParser = new JSONParser();
			jParser.deleteObject("http://dcounter.herokuapp.com/events/",
					selectedEvent.getId().get$oid());
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressDialog.dismiss();
			Toast.makeText(AddEventActivity.this, "selected event deleted",
					Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(AddEventActivity.this,
					BusinessDashboardActivity.class);
			startActivity(intent);
		}

	}

	private class FacebookPostAsyncExample extends
			AsyncTask<String, Void, Boolean> {

		@SuppressWarnings("deprecation")
		@Override
		protected Boolean doInBackground(String... params) {
			// System.out.println(">>>>>>> in post async");
			boolean returnBool = false;
			String postMessage = "";
			try {
			SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy, h:mm a");
			Date startDate = sdf.parse(globalVariable.getSelectedEvent().getStartDate());
			Date endDate = sdf.parse(globalVariable.getSelectedEvent().getEndDate());
			String eventName = "\n\t  "+ globalVariable.getSelectedEvent().getName();
			SimpleDateFormat sdf2=new SimpleDateFormat("EEE, d MMM, h:mm a");
			String startDateTime = sdf2.format(startDate);
			String endDateTime = sdf2.format(endDate);
			System.out.println(">>>>>>>> startDatetime***"+startDateTime);
			System.out.println(">>>>>>> endDate.getDate"+endDate.getDate());
			System.out.println(">>>>>>> startDate.getDate"+startDate.getDate());
			if(endDate.getDate() > startDate.getDate() )
			{
			 postMessage = postMessage
			 + "\t "
			 +startDateTime
			 +"\t-\t " 
			 +endDateTime+"\n";
			}else
			{
				postMessage = postMessage
						 + "\t "
						 +startDateTime.substring(0, 11)+"\t\t\t\t\t\t\t\t\t\t\t\t\t"+startDateTime.substring(12,startDateTime.length())+"-"+endDateTime.substring(12, endDateTime.length())+"\n";
			}

			System.out.println(">>>>>>> Message11" + postMessage);
			// ********************************Convert String to Image
			// **************************
				// =================== image append ===================
			int lower = 0;
			int upper = 8;
			int r = Integer
					.valueOf((int) ((Math.random() * (upper - lower)) + lower));

			Bitmap myBitmap = BitmapFactory.decodeResource(
					AddEventActivity.this.getResources(), drawableArray[r]);
			int width = myBitmap.getWidth();
			int height = myBitmap.getHeight();
			System.out.println(">>>>>> height =" + height);
			height = (int) (height * 850) / width;
			System.out.println(">>>>>> new height =" + height);
			myBitmap = Bitmap.createScaledBitmap(myBitmap, 850, height,
					true);

			final Rect bounds = new Rect();
			TextPaint textPaint = new TextPaint() {
				{
					setColor(Color.parseColor("#837777"));
					setTextAlign(Paint.Align.LEFT);
					setTypeface(Typeface.createFromAsset(
							AddEventActivity.this.getAssets(), "Fonts/ufonts.com_segoe_ui_semibold.ttf"));
					setTextSize(30f);
					setAntiAlias(true);
				}
			};
			textPaint.getTextBounds(eventName, 0, eventName.length(), bounds);
			StaticLayout mTextLayout = new StaticLayout(eventName,
					textPaint, 850, Alignment.ALIGN_NORMAL, 1.0f, 0.0f,
					false);
			int maxWidth = -1;
			for (int i = 0; i < mTextLayout.getLineCount(); i++) {
				if (maxWidth < mTextLayout.getLineWidth(i)) {
					maxWidth = (int) mTextLayout.getLineWidth(i);
				}
			}
			final Bitmap bmp = Bitmap.createBitmap(850,
					mTextLayout.getHeight(), Bitmap.Config.ARGB_8888);

			bmp.eraseColor(Color.parseColor("#ffffff"));// just adding white
														// background
			final Canvas canvas = new Canvas(bmp);
			mTextLayout.draw(canvas);
			

			TextPaint textPaint1 = new TextPaint() {
				{
					setColor(Color.parseColor("#000000"));
					setTextAlign(Paint.Align.LEFT);
					setTypeface(Typeface.createFromAsset(
							AddEventActivity.this.getAssets(), "Fonts/SEGOEUIL.ttf"));
					setTextSize(40f);
					setAntiAlias(true);
				}
			};
			textPaint1.getTextBounds(postMessage, 0, postMessage.length(),
					bounds);
			StaticLayout mTextLayout1 = new StaticLayout(postMessage,
					textPaint1, myBitmap.getWidth(),
					Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
			int maxWidth1 = -1;
			for (int i = 0; i < mTextLayout1.getLineCount(); i++) {
				if (maxWidth1 < mTextLayout1.getLineWidth(i)) {
					maxWidth1 = (int) mTextLayout1.getLineWidth(i);
				}
			}
			final Bitmap bmp1 = Bitmap.createBitmap(850,
					mTextLayout1.getHeight(), Bitmap.Config.ARGB_8888);

			bmp1.eraseColor(Color.parseColor("#ffffff"));// just adding
															// white
															// background
			final Canvas canvas1 = new Canvas(bmp1);
			mTextLayout1.draw(canvas1);

			Bitmap bmOverlay = Bitmap.createBitmap(
					850 + 40,
					myBitmap.getHeight() + bmp.getHeight()
							+ bmp1.getHeight() +40,
					Bitmap.Config.ARGB_8888);
			Canvas canvasAppend = new Canvas(bmOverlay);
			Paint paint = new Paint();
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.WHITE);
			canvasAppend.drawRect(0, 0, 850 + 40, myBitmap.getHeight()
					+ bmp.getHeight() + bmp1.getHeight() + 40, paint);
			canvasAppend.drawBitmap(myBitmap, 20, 20, null);
			Paint paint1 = new Paint();
			paint1.setColor(Color.LTGRAY);
			paint1.setStrokeWidth(1);
			canvasAppend.drawRect(
					19,
					myBitmap.getHeight() + 20,
					850 + 21,
					myBitmap.getHeight() + bmp.getHeight()
							+ bmp1.getHeight() + 21, paint1);
			canvasAppend.drawBitmap(bmp, 20, myBitmap.getHeight() + 20,
					null);
			canvasAppend.drawBitmap(bmp1, 20,
					myBitmap.getHeight() + bmp.getHeight() + 20, null);
			OutputStream os = null;
			byte[] data = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bmp.recycle();
			bmOverlay.compress(CompressFormat.JPEG, 100, baos);
			data = baos.toByteArray();
				Facebook facebook = new Facebook("743382039036135");
				// *********************************end conversion
				// ***********************
				// posting to page wall
				ByteArrayBody bab = new ByteArrayBody(data, "test.png");
				try {
					// create new Session with page access_token
					Session.openActiveSessionWithAccessToken(
							getApplicationContext(),
							AccessToken
									.createFromExistingAccessToken(
											globalVariable.getSelectedBusiness().getSelectedFBPage()
													.getAccess_token(),
											new Date(facebook
													.getAccessExpires()),
											new Date(facebook
													.getLastAccessUpdate()),
											AccessTokenSource.FACEBOOK_APPLICATION_NATIVE,
											Arrays.asList("manage_pages",
													"publish_stream",
													"photo_upload")),
							new Session.StatusCallback() {
								@Override
								public void call(Session session,
										SessionState state, Exception exception) {
									System.out
											.println(">>>>>>> session status callback");
									// TODO Auto-generated method stub
									if (session != null && session.isOpened()) {
										Session.setActiveSession(session);
										Session session1 = Session
												.getActiveSession();
										System.out.println(">>>>>>> is Manage"
												+ session1
														.isPublishPermission("manage_pages"));
									}
								}
							});// session open closed
					System.out.println(">>>>>>> new session open");

					String url = "https://graph.facebook.com/"
							+ globalVariable.getSelectedBusiness().getSelectedFBPage().getId()
							+ "/photos";
					System.out.println(">>>>>>> Url fb:" + url);
					HttpPost postRequest = new HttpPost(url);
					HttpParams http_parameters = new BasicHttpParams();
					HttpConnectionParams.setConnectionTimeout(http_parameters,
							3000);
					HttpClient httpClient = new DefaultHttpClient();
					MultipartEntity reqEntity = new MultipartEntity(
							HttpMultipartMode.BROWSER_COMPATIBLE);
					reqEntity.addPart("access_token", new StringBody(Session
							.getActiveSession().getAccessToken()));
//					reqEntity.addPart("message", new StringBody(postMessage));
					reqEntity.addPart("picture", bab);
					postRequest.setEntity(reqEntity);
					HttpResponse response1 = httpClient.execute(postRequest);
					System.out.println(">>>>>>> response" + response1);
					if (response1 == null || response1.equals("")
							|| response1.equals("false")) {
						System.out.println(">>>>>>> Blank response.");
					} else {
						System.out
								.println(">>>>>>> Message posted to your facebook wall!");
						returnBool = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				System.out.println("Failed to post to wall!");
				e.printStackTrace();
			}

			return returnBool;
		}

		@Override
		protected void onPostExecute(Boolean result) {
		}

		public String convertDate(String datestr) {
			System.out.println(">>>>>>> event date input came for conversion:"+datestr);

			String formatedDate = "";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				System.out.println(">>>>>>> param :" + datestr);
				int hh = Integer.parseInt(datestr.split("T")[1].split(":")[0]);
				DateFormat df = new SimpleDateFormat("dd MMM yyyy");
				if (hh < 13) {
					formatedDate = formatedDate
							+ df.format(sdf.parse(datestr.split("T")[0]))
							+ " ,  " + datestr.split("T")[1] + "  am";
					System.out.println(">>>>>>> formated date:" + formatedDate);
				} else {
					System.out.println(">>>>>>> param :" + datestr);
					formatedDate = formatedDate
							+ df.format(sdf.parse(datestr.split("T")[0]))
							+ " ,  " + datestr.split("T")[1] + "  pm";
					System.out.println(">>>>>>> formated date:" + formatedDate);
				}
			} catch (Exception e) {
				System.out.println(">>>>error:" + e.getMessage());
				e.printStackTrace();

			}
			return formatedDate;
		}

	}

}