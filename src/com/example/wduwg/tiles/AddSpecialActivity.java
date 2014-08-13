package com.example.wduwg.tiles;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout.Alignment;
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
import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.android.Facebook;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mw.wduwg.model.Event;
import com.mw.wduwg.model.Special;
import com.mw.wduwg.services.CreateDialog;
import com.mw.wduwg.services.GlobalVariable;
import com.mw.wduwg.services.JSONParser;
import com.mw.wduwg.services.ServerURLs;
import com.parse.Parse;
import com.parse.entity.mime.HttpMultipartMode;
import com.parse.entity.mime.MultipartEntity;
import com.parse.entity.mime.content.ByteArrayBody;
import com.parse.entity.mime.content.StringBody;

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

	int[] drawableArray = {R.drawable.bar1,R.drawable.bar2,R.drawable.bar3,R.drawable.bar4,R.drawable.bar5,R.drawable.bar6,
			R.drawable.bar7,R.drawable.bar8,R.drawable.bar10};
	
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
				
				JSONObject jsonFromServer = jsonparser.getJSONFromUrlAfterHttpPost(url, jsonObject2);
				Gson gson = new Gson();
				selectedSpecial = gson.fromJson(jsonFromServer.toString(), Special.class);
				selectedSpecial.setStartDate(jsonFromServer.getString("start_date_time"));
				selectedSpecial.setEndDate(jsonFromServer.getString("end_date_time"));

			} catch (Exception e) {
				Log.d("Response========", "inside catch");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void arg0) {
			progressDialog.dismiss();
			alertDialogBuilder = createDialog.createAlertDialog(
					"Special added successfully", null, false);
			alertDialogBuilder.setCancelable(false);
			alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					alertDialog.dismiss();
					Intent intent = new Intent(AddSpecialActivity.this,
							SpecialActivity.class);
					startActivity(intent);
				}
			});
			alertDialog = alertDialogBuilder.create();
			alertDialog.show();
			FacebookPostAsyncExample asyncExample = new FacebookPostAsyncExample();
			asyncExample.execute(new String[] { "Helllo Worls" }); 
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
	
	
	private class FacebookPostAsyncExample extends AsyncTask<String, Void, Boolean> {

		@SuppressWarnings("deprecation")
		@Override
		protected Boolean doInBackground(String... params) {
//			System.out.println(">>>>>>> in post async");
			boolean returnBool = false;
			String postMessage="";
			String specialName = "";
			specialName = specialName + "\n Special:\t\t\t\t\t\t\t\t\t\t\t"+ selectedSpecial.getName();
				System.out.println(">>>>>>> special date:"+selectedSpecial.getStartDate());
				postMessage = postMessage 
						+ " Start Time:\t\t\t\t\t\t\t\t\t\t"+ convertDate(selectedSpecial.getStartDate().substring(0, 16)) + "\n End Time:\t\t\t\t\t\t\t\t\t\t\t" + convertDate(selectedSpecial.getEndDate().substring(0, 16));
			
		System.out.println(">>>>>>> Message"+postMessage);
			// ********************************Convert String to Image **************************
			try {
		// =================== image append ===================	
				 int lower = 0;
				 int upper = 8;
				 int r =Integer.valueOf((int) ((Math.random() * (upper - lower)) + lower)) ;
				 
				 Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), drawableArray[r]);
				 
				 
				 final Rect bounds = new Rect();
					TextPaint textPaint = new TextPaint() {
					    {
					    	setColor(Color.parseColor("#000000"));
					        setTextAlign(Paint.Align.LEFT);
					        setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(),
					    			"Fonts/OpenSans-Bold.ttf"));
					        setTextSize(35f);
					        setAntiAlias(true);
					    }
					};
					textPaint.getTextBounds(specialName, 0, specialName.length(), bounds);
					StaticLayout mTextLayout = new StaticLayout(specialName, textPaint,
							myBitmap.getWidth(), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
					int maxWidth = -1;
					for (int i = 0; i < mTextLayout.getLineCount(); i++) {
					    if (maxWidth < mTextLayout.getLineWidth(i)) {
					        maxWidth = (int) mTextLayout.getLineWidth(i);
					    }
					}
					final Bitmap bmp = Bitmap.createBitmap(myBitmap.getWidth() , mTextLayout.getHeight(),
					            Bitmap.Config.ARGB_8888);
					
					bmp.eraseColor(Color.parseColor("#ffffff"));// just adding black background
					final Canvas canvas = new Canvas(bmp);
					mTextLayout.draw(canvas);
					
					
					
					
					TextPaint textPaint1 = new TextPaint() {
					    {
					        setColor(Color.parseColor("#837777"));
					        setTextAlign(Paint.Align.LEFT);
					        setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(),
					    			"Fonts/OpenSans-Bold.ttf"));
					        setTextSize(30f);
					        setAntiAlias(true);
					    }
					};
					textPaint.getTextBounds(postMessage, 0, postMessage.length(), bounds);
					StaticLayout mTextLayout1 = new StaticLayout(postMessage, textPaint1,
							myBitmap.getWidth(), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
					int maxWidth1 = -1;
					for (int i = 0; i < mTextLayout1.getLineCount(); i++) {
					    if (maxWidth1 < mTextLayout1.getLineWidth(i)) {
					        maxWidth1 = (int) mTextLayout1.getLineWidth(i);
					    }
					}
					final Bitmap bmp1 = Bitmap.createBitmap(myBitmap.getWidth() , mTextLayout1.getHeight(),
					            Bitmap.Config.ARGB_8888);
					
					bmp1.eraseColor(Color.parseColor("#ffffff"));// just adding black background
					final Canvas canvas1 = new Canvas(bmp1);
					mTextLayout1.draw(canvas1);
				 
				 
				 
				 Bitmap bmOverlay = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight()+bmp.getHeight()+bmp1.getHeight(),  Bitmap.Config.ARGB_8888);
				 Canvas canvasAppend = new Canvas(bmOverlay);
				 canvasAppend.drawBitmap(myBitmap, 0.f, 0.f, null);
				 canvasAppend.drawBitmap(bmp, 0.f, myBitmap.getHeight(), null);
				 canvasAppend.drawBitmap(bmp1, 0.f, myBitmap.getHeight()+bmp.getHeight(), null);
				 OutputStream os = null; 
				 byte[] data = null;
				    	
				      ByteArrayOutputStream baos = new ByteArrayOutputStream();
				      bmp.recycle();
				      bmOverlay.compress(CompressFormat.JPEG, 100, baos); 
				      data = baos.toByteArray();
				      Facebook facebook = new Facebook("743382039036135");
				   // *********************************end conversion ***********************
				      // posting to page wall
				      ByteArrayBody bab = new ByteArrayBody(data, "test.png");
					 try{
						 // create new Session with page access_token
						 Session.openActiveSessionWithAccessToken(getApplicationContext(),AccessToken.createFromExistingAccessToken(globalVariable.getSelectedBusiness().getSelectedFBPage().getAccess_token(), new Date(facebook.getAccessExpires()), new Date( facebook.getLastAccessUpdate()), AccessTokenSource.FACEBOOK_APPLICATION_NATIVE, Arrays.asList("manage_pages","publish_stream","photo_upload")) , new Session.StatusCallback() {
								@Override
								public void call(Session session, SessionState state, Exception exception) {
									System.out.println(">>>>>>> session status callback");
									// TODO Auto-generated method stub
									if(session != null && session.isOpened()) {
						                Session.setActiveSession(session);
						                Session session1  = Session.getActiveSession();
						                System.out.println(">>>>>>> is Manage"+session1.isPublishPermission("manage_pages"));
						            }
								}
							});// session open closed
							System.out.println(">>>>>>> new session open");
					
						 
					String url = "https://graph.facebook.com/"+globalVariable.getSelectedBusiness().getSelectedFBPage().getId()+"/photos";
					HttpPost postRequest = new HttpPost(url);
					HttpParams http_parameters = new BasicHttpParams();
				    HttpConnectionParams.setConnectionTimeout(http_parameters, 3000);
				    HttpClient httpClient = new DefaultHttpClient();
				    MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				    reqEntity.addPart("access_token", new StringBody(Session.getActiveSession().getAccessToken()));
//				    reqEntity.addPart("message", new StringBody(postMessage));
				    reqEntity.addPart("picture", bab);
				    postRequest.setEntity(reqEntity);
				    HttpResponse response1 = httpClient.execute(postRequest);
				    System.out.println(">>>>>>> response"+response1);
				    if (response1 == null || response1.equals("")
							|| response1.equals("false")) {
						System.out.println(">>>>>>> Blank response.");
					} else {
						System.out.println(">>>>>>> Message posted to your facebook wall!");
						returnBool = true;
					}
					 }catch(Exception e)
					 {
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
		
		public String convertDate(String datestr)
		{
			String formatedDate="";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try{
				int hh = Integer.parseInt(datestr.split("T")[1].split(":")[0]);
				DateFormat df = new SimpleDateFormat("dd MMM yyyy");
				if(hh<13)
				{
				formatedDate=formatedDate+df.format(sdf.parse(datestr.split("T")[0])) + " ,  "+datestr.split("T")[1]+"  AM";
				}else
				{
					formatedDate=formatedDate+df.format(sdf.parse(datestr.split("T")[0])) + " ,  "+datestr.split("T")[1]+"  PM";
				}
			}catch(Exception e)
			{
				e.printStackTrace();
				
			}
			return formatedDate;
		}

	}

}
