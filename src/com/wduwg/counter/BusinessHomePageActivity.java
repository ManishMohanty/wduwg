package com.wduwg.counter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.zip.Inflater;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wduwg.counter.R;
import com.google.gson.Gson;
import com.mw.wduwg.model.Business;
import com.mw.wduwg.model.BusinessFBPage;
import com.mw.wduwg.services.CreateDialog;
import com.mw.wduwg.services.GlobalVariable;
import com.mw.wduwg.services.JSONParser;
import com.mw.wduwg.services.SchedulerCount;
import com.mw.wduwg.services.ServerURLs;

public class BusinessHomePageActivity extends Activity {

	TextView addressLabel;
	// TextView specialTv;
	EditText businessNameET;
	EditText businessAddressET;
    Bitmap bitmap;
	ImageView roundPhotoIV;
	Typeface typeface;
	Typeface typeface2;

	Intent previousIntent;
	Intent nextIntent;
	String deviceId;

	Business selectedBusiness;

	boolean isAdded;
	private Bitmap[] buffers = new Bitmap[1];

	CreateDialog createDialog;
	AlertDialog.Builder alertDialogBuilder;
	AlertDialog alertDialog;
	ProgressDialog progressDialog;

	RelativeLayout buttonLayout1,buttonLayout2;
	TextView link, selectedPageFB;
	GlobalVariable globalVariable;
	JSONParser jParser;
	JSONObject jsonFromServer;
	List<String> Pagelist;
	BusinessFBPage fbPage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_event_and_gateway);
		findThings();
		initializeThings();
		int titleId = getResources().getIdentifier("action_bar_title", "id",
				"android");
		TextView yourTextView = (TextView) findViewById(titleId);
		yourTextView.setTextSize(19);
		yourTextView.setTextColor(Color.parseColor("#016AB2"));
		yourTextView.setTypeface(typeface);
		final BitmapFactory.Options options = new BitmapFactory.Options();
		if (globalVariable.getSelectedBusiness() != null
				&& previousIntent.hasExtra("isFromMain")) {
			Gson gson = new Gson();
			selectedBusiness = globalVariable.getSelectedBusiness();
			 String json = globalVariable.getSelectedBusiness()
			 .getImageEncoded();
			 if (json != null && json.length() > 0) {
			 byte[] b = Base64.decode(json, Base64.DEFAULT);
			 roundPhotoIV.setImageBitmap(globalVariable.getRoundedShape(BitmapFactory.decodeByteArray(b, 0, b.length)));
			 b=null;
			 Runtime.getRuntime().gc();
			 }
			businessNameET.setText(selectedBusiness.getName());
			businessAddressET.setText(selectedBusiness.getAddress());
			businessNameET.setFocusable(false);
			businessAddressET.setFocusable(false);
			businessNameET.setFocusableInTouchMode(false);
			businessAddressET.setFocusableInTouchMode(false);
		} else {
			if (previousIntent.getBooleanExtra("defaultImage", false)) {
				roundPhotoIV.setImageBitmap(globalVariable.getRoundedShape(BitmapFactory.decodeResource(getResources(), R.drawable.wduwg_logo2223)));
			} else {
				roundPhotoIV.setImageBitmap(globalVariable.getRoundedShape( BitmapFactory.decodeByteArray(
						previousIntent.getByteArrayExtra("byteArray"),
						0,
						previousIntent.getByteArrayExtra("byteArray").length)));
			}
			if (previousIntent.hasExtra("business_name")) {
				businessNameET.setText(""
						+ previousIntent.getStringExtra("business_name"));
				businessAddressET.setKeyListener(null);
			}
			businessAddressET.setText(previousIntent
					.getStringExtra("complete_address"));
			if (!previousIntent.hasExtra("isFromMain")) {
				buttonLayout1.setVisibility(View.GONE);
				buttonLayout2.setVisibility(View.GONE);
				link.setVisibility(View.VISIBLE);

			}
			
			if (link.getVisibility() != View.VISIBLE) {
				businessNameET.setFocusable(false);
				businessAddressET.setFocusable(false);
				businessNameET.setFocusableInTouchMode(false);
				businessAddressET.setFocusableInTouchMode(false);
			}
			if (businessNameET.isFocusable() && businessAddressET.isFocusable())
				((LinearLayout) findViewById(R.id.add_event_and_gateway_LL))
						.setOnTouchListener(new OnTouchListener() {
							@Override
							public boolean onTouch(View v, MotionEvent event) {
								InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
								if (imm != null && businessNameET.isFocusable())
									imm.hideSoftInputFromWindow(
											getCurrentFocus().getWindowToken(),
											0);
								return false;
							}
						});

		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.overflow_options_menu, menu);
//		SpannableString delinkstr = new SpannableString(menu.findItem(R.id.menu_delink).getTitle());
//		delinkstr.setSpan(typeface, 0, delinkstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		menu.findItem(R.id.menu_delink).setTitle(delinkstr);
		SpannableString logoutstr = new SpannableString(menu.findItem(R.id.menu_logout).getTitle());
		logoutstr.setSpan(typeface, 0, logoutstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		menu.findItem(R.id.menu_logout).setTitle(logoutstr);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
//		MenuItem item = menu.findItem(R.id.menu_delink);
		MenuItem logouItem = menu.findItem(R.id.menu_logout);
//		if (globalVariable.getSelectedBusiness() != null) {
//			item.setEnabled(true);
//
//		} else {
//			item.setEnabled(false);
//		}
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
//		case R.id.menu_delink:
//			SchedulerCount scheduledTask = new SchedulerCount(this);
//			Timer timer = new Timer();
//			timer.scheduleAtFixedRate(scheduledTask, 1000, 10000);
//			scheduledTask.run();
//			SchedulerCount.event = globalVariable.getSelectedEvent();
//			timer.cancel();
//			globalVariable.setSelectedBusiness(null);
//			globalVariable.setSelectedEvent(null);
//			globalVariable.setMenIn(0);
//			globalVariable.setMenOut(0);
//			globalVariable.setWomenIn(0);
//			globalVariable.setWomenOut(0);
//			globalVariable.saveSharedPreferences();
//
//			alertDialogBuilder = createDialog
//					.createAlertDialog(
//							"Delink Successful",
//							"Your device has been delinked. Redirecting search as per current location.",
//							false);
//			singleOKButton(alertDialogBuilder);
//			alertDialog = alertDialogBuilder.create();
//			alertDialog.show();
//			return true;
		case R.id.menu_logout:
			if (LoginFacebookActivity.timer != null)
				LoginFacebookActivity.timer.cancel();
			globalVariable.getCustomer().setPages(null);
			globalVariable.setFb_access_expire(0);
			globalVariable.setFb_access_token(null);
			globalVariable.saveSharedPreferences();
			Toast.makeText(this, "Logged out from FB.", Toast.LENGTH_SHORT).show();
			nextIntent = new Intent(BusinessHomePageActivity.this,SpalshFirstActivity.class);
			nextIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(nextIntent);
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		int titleId = getResources().getIdentifier("action_bar_title", "id",
				"android");
		TextView yourTextView = (TextView) findViewById(titleId);
		yourTextView.setTextSize(19);
		yourTextView.setTextColor(Color.parseColor("#016AB2"));
		yourTextView.setTypeface(Typeface.createFromAsset(getAssets(), "Fonts/OpenSans-Light.ttf"));
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		System.out.println(">>>>>>> BusinessHomePage::onPause");
		super.onPause();
		globalVariable.saveSharedPreferences();
	}

	private void findThings() {
		roundPhotoIV = (ImageView) findViewById(R.id.photo);
		businessNameET = (EditText) findViewById(R.id.customer_name);
		businessAddressET = (EditText) findViewById(R.id.customer_address);
		addressLabel = (TextView) findViewById(R.id.addressLabel);
		buttonLayout1 = (RelativeLayout) findViewById(R.id.buttonLayout1);
		buttonLayout2 = (RelativeLayout) findViewById(R.id.buttonLayout2);
		link = (TextView) findViewById(R.id.link);
	}

	private void initializeThings() {
		previousIntent = getIntent();
		globalVariable = (GlobalVariable) getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View spinnerView = inflater.inflate(R.layout.spinner_item, null);
		TextView spinnerText = (TextView) spinnerView
				.findViewById(R.id.spinnerText);
		typeface = Typeface
				.createFromAsset(getAssets(), "Fonts/OpenSans-Bold.ttf");
		typeface2 = Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Light.ttf");

		spinnerText.setTypeface(typeface2);
		deviceId = Secure.getString(this.getContentResolver(),
				Secure.ANDROID_ID);
		businessNameET.setTypeface(typeface);
		addressLabel.setTypeface(typeface);
		businessAddressET.setTypeface(typeface2);
		link.setTypeface(typeface);
		createDialog = new CreateDialog(this);
		Pagelist = new ArrayList<String>();
		System.out.println(">>>>>>> customer Name:"+globalVariable.getCustomer().getName());
		System.out.println(">>>>>>> Page size:"+globalVariable.getCustomer().getPages().size());
			for (int i = 0; i < globalVariable.getCustomer().getPages().size(); i++) {
				Pagelist.add(globalVariable.getCustomer().getPages().get(i)
						.getName());
			}
	}

	private class SaveToParseAndPreferencesAsync extends
			AsyncTask<String, Void, Business> {
		boolean wasNewObjectCreated = true;
		String businessID;
		String businessAddress;
		String completeResult;

		@Override
		protected Business doInBackground(String... params) {
			jParser = new JSONParser();
			System.out.println(">>>>>>> inside save business");
			if (previousIntent != null) {
				businessAddress = previousIntent
						.getStringExtra("complete_address");
				businessID = previousIntent.getStringExtra("business_id");
				completeResult = previousIntent
						.getStringExtra("complete_result");
			} else {
				businessAddress = businessAddressET.getText().toString();
				businessID = "custom_id";
				completeResult = "custom_result";
			}
			System.out.println(">>>>>>> business id " + businessID);
			System.out.println(">>>>>>> selected customer id:"
					+ globalVariable.getCustomer().getId().get$oid());

			try {
				String url = ServerURLs.URL + ServerURLs.BUSINESS;
				System.out.println(">>>>>>> url is   : " + url);
				JSONObject jsonObject2 = null;
				try {

					JSONObject jsonObject = new JSONObject()
							.put("name", businessNameET.getText().toString())
							.put("google_place_id", businessID)
							.put("google_api_result", completeResult)
							.put("face_book_page", fbPage.getId())
							.put("customer_id",
									globalVariable.getCustomer().getId()
											.get$oid())
							.put("address",
									businessAddressET.getText().toString());
					jsonObject2 = new JSONObject().put("business", jsonObject);

				} catch (JSONException e) {
					e.printStackTrace();
				}

				jsonFromServer = jParser.getJSONFromUrlAfterHttpPost(url,
						jsonObject2);
				System.out.println(">>>>>>> json from server for post business"
						+ jsonFromServer);
				if (!jsonFromServer.has("status")) {
					Gson gson = new Gson();
					String businessJson = jsonFromServer.toString();
					System.out.println(businessJson);
					selectedBusiness = gson.fromJson(businessJson.toString(),
							Business.class);
					System.out.println(">>>>>>> is null"
							+ (selectedBusiness == null));
					 ByteArrayOutputStream baos = new ByteArrayOutputStream();
					 ((BitmapDrawable)roundPhotoIV.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos);
					 byte[] b = baos.toByteArray();
					 String encodedImage = Base64.encodeToString(b,
					 Base64.DEFAULT);
					 selectedBusiness.setImageEncoded(encodedImage);
					globalVariable.getCustomer().getBusinesses()
							.add(selectedBusiness);
					globalVariable.setSelectedBusiness(selectedBusiness);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return selectedBusiness;
		}

		@Override
		protected void onPostExecute(Business selectedBusiness) {
			progressDialog.dismiss();
			businessNameET.setFocusable(false);
			businessNameET.setFocusableInTouchMode(false);
			businessAddressET.setFocusable(false);
			businessAddressET.setFocusableInTouchMode(false);
			link.setVisibility(View.GONE);
			buttonLayout1.setVisibility(View.VISIBLE);
			buttonLayout2.setVisibility(View.VISIBLE);
		}
	}

	public void singleOKButton(final AlertDialog.Builder alertDialogBuilder) {
		alertDialogBuilder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						if(globalVariable.getFb_access_token()!=null && globalVariable.getFb_access_expire() > 0)
						{
						nextIntent = new Intent(BusinessHomePageActivity.this,
								IdentifyingBusinessActivity.class);
						}
						else
						{
							Bitmap bitmap = ((BitmapDrawable)roundPhotoIV.getDrawable()).getBitmap();
							bitmap.recycle();
							bitmap=null;
							Runtime.getRuntime().gc();
							nextIntent = new Intent(BusinessHomePageActivity.this,
									SpalshFirstActivity.class);
						}
						startActivity(nextIntent);
						overridePendingTransition(R.anim.anim_out,
								R.anim.anim_in);
						System.out.println("hello");
					}
				});
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	public void onSpecial(View v) {
		if (!validate())
			return;
		if (globalVariable.getSelectedBusiness() == null) {
			Toast.makeText(getApplicationContext(),
					"business is not registered with device",
					Toast.LENGTH_SHORT).show();
			return;
		}
		nextIntent = new Intent(this, SpecialActivity.class);
		startActivity(nextIntent);
	}

	public void onReports(View view) {
		if (!validate())
			return;
		// nextIntent = new Intent(this, ReportEventActivity.class);
		if (globalVariable.getSelectedBusiness() == null) {
			Toast.makeText(getApplicationContext(),
					"business is not registered with device",
					Toast.LENGTH_SHORT).show();
			return;
		}
		nextIntent = new Intent(this, ReportActualActvivity.class);
		nextIntent.putExtra("business_name", businessNameET.getText()
				.toString());
		startActivity(nextIntent);
	}

	private boolean validate() {
		boolean bool = true;
		if (businessNameET.getText().toString().trim().length() == 0) {
			System.out.println(">>>>>>> You must enter business name.");
			if (!businessNameET.isFocused())
				businessNameET.requestFocus();
			businessNameET.setError("You must enter business name.");
			bool = false;
			return bool;
		}
		if (businessAddressET.getText().toString().trim().length() == 0) {
			System.out.println(">>>>>>> you must enter address");
			businessAddressET.requestFocus();
			businessAddressET.setError("You must enter Address.");
			bool = false;
		}

		return bool;
	}

	public void onCount(View v) {
		if (!validate())
			return;

		nextIntent = new Intent(BusinessHomePageActivity.this,
				AddEventActivity.class);
		startActivity(nextIntent);
		overridePendingTransition(R.anim.anim_out, R.anim.anim_in);

	}
	public void onEvent(View v) {
		if (!validate())
			return;

		nextIntent = new Intent(BusinessHomePageActivity.this,
				EventActivity.class);
		startActivity(nextIntent);
		overridePendingTransition(R.anim.anim_out, R.anim.anim_in);

	}

	public void link(View v) {
		String[] items = Pagelist.toArray(new String[Pagelist.size()]);
		alertDialogBuilder = createDialog.createAlertDialog(
				"Select Facebook Page", null, false);
		alertDialogBuilder.setItems(items,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int pos) {
						// TODO Auto-generated method stub
						fbPage = globalVariable.getCustomer().getPages()
								.get(pos);
						globalVariable.setSelectedFBPage(fbPage);
						alertDialog.dismiss();
						if (!validate()) {
							alertDialog.dismiss();
							return;
						}
						globalVariable.setSelectedBusiness(null);
						globalVariable.saveSharedPreferences();
						List<Business> businessList = globalVariable
								.getCustomer().getBusinesses();
						System.out.println(">>>>>>> business list size:"
								+ businessList.size());
						System.out.println(">>>>>>> business to be added:"
								+ businessNameET.getText().toString());
						boolean isExist = false;
						for (int i = 0; i < businessList.size(); i++) {
							if (businessList
									.get(i)
									.getName()
									.equalsIgnoreCase(
											businessNameET.getText().toString())
									&& businessList
											.get(i)
											.getAddress()
											.equalsIgnoreCase(
													businessAddressET.getText()
															.toString())) {
								Business business = businessList.get(i);

								Gson gson = new Gson();
								String json = gson.toJson(business);
								System.out.println(">>>>>>> business existing"
										+ json);
								ByteArrayOutputStream baos = new ByteArrayOutputStream();

								((BitmapDrawable)roundPhotoIV.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.JPEG,
										100, baos);
								byte[] b = baos.toByteArray();
								String encodedImage = Base64.encodeToString(b,
										Base64.DEFAULT);
								business.setImageEncoded(encodedImage);
								globalVariable.setSelectedBusiness(business);
								System.out
										.println(">>>>>>> before navigation to addEvent");
								System.out
										.println(">>>>>>> selectedBusinessId:"
												+ globalVariable
														.getSelectedBusiness()
														.getId().get$oid());
								isExist = true;
								link.setVisibility(View.GONE);
								buttonLayout1.setVisibility(View.VISIBLE);
								buttonLayout2.setVisibility(View.VISIBLE);
								businessNameET.setFocusable(false);
								alertDialog.dismiss();
								businessNameET.setFocusable(false);
								businessAddressET.setFocusable(false);
								businessNameET.setFocusableInTouchMode(false);
								businessAddressET
										.setFocusableInTouchMode(false);
								break;
							}
						}
						if (!isExist) {
							alertDialog.dismiss();
							progressDialog = createDialog.createProgressDialog(
									"Saving", "Please wait for a while.", true,
									null);
							progressDialog.show();
							SaveToParseAndPreferencesAsync async = new SaveToParseAndPreferencesAsync();
							async.execute(new String[] { "Hello World" });
						}
					}
				});
		alertDialogBuilder.setPositiveButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.dismiss();
					}
				});
		alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	} // end of Link

}
