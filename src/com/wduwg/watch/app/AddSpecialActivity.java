package com.wduwg.watch.app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.apphance.android.Log;
import com.mw.wduwg.model.Special;
import com.mw.wduwg.services.CreateDialog;
import com.mw.wduwg.services.GlobalVariable;
import com.mw.wduwg.services.JSONParser;
import com.mw.wduwg.services.MyAutoCompleteTextView;
import com.mw.wduwg.services.ServerURLs;
import com.parse.Parse;
import com.wduwg.watch.app.SpecialActivity.LoadStringsAsync;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddSpecialActivity extends Activity {

	CreateDialog createDialog;
	ProgressDialog progressDialog;
	GlobalVariable globalVariable;
	EditText nameET, startDateET, endDateET, startTimeET, endTimeET;

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

	SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d");
	SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm");

	private void findThings() {
		nameET = (EditText) findViewById(R.id.nameET);
		startDateET = (EditText) findViewById(R.id.startsLabel);
		endDateET = (EditText) findViewById(R.id.endsLabel);
		startTimeET = (EditText) findViewById(R.id.startstime);
		endTimeET = (EditText) findViewById(R.id.endstime);
	}

	private void initializeThings() {
		Parse.initialize(this, "mTkWVlwx9IsCpHvGaYG6zZOa5tB9NuIokmlSWQMS",
				"6H7bzx0gZL7I2Zz9MQ1u5ZPYaOhlHAkqeyooexCS");
		typeface2 = Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Light.ttf");

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

		Calendar calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);
		// year = Calendar.YEAR;

		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);

		onTouchListeners();
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
		List<Special> specials = globalVariable.getSelectedBusiness()
				.getSpecials();
		boolean isExist = false;
		if (specials.size() > 0) {
			for (int i = 0; i < specials.size(); i++) {
				if (specials.get(i).getName()
						.equalsIgnoreCase(nameET.getText().toString())) {
					Toast.makeText(
							this,
							"'" + nameET.getText().toString()
									+ "' specials already exists",
							Toast.LENGTH_SHORT).show();
					isExist = true;
					break;
				}
			}
		}
		if (isExist == false) {
			progressDialog.show();
			LoadStringsAsync asyncTask = new LoadStringsAsync();
			asyncTask.execute();
		}
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

		if (endDateTime != null && startDateTime.compareTo(endDateTime) >= 0) {
			endDateET.setError("");
			Toast.makeText(this, "End date must be greater than start date.",
					Toast.LENGTH_LONG).show();
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
