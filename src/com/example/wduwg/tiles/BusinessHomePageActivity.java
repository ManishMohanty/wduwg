package com.example.wduwg.tiles;

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

import com.example.wduwg.tiles.R;
import com.google.gson.Gson;
import com.loopj.android.image.SmartImageView;
import com.mw.wduwg.model.Business;
import com.mw.wduwg.model.BusinessFBPage;
import com.mw.wduwg.services.CreateDialog;
import com.mw.wduwg.services.GlobalVariable;
import com.mw.wduwg.services.JSONParser;
import com.mw.wduwg.services.SchedulerCount;
import com.mw.wduwg.services.ServerURLs;

public class BusinessHomePageActivity extends Activity {

	EditText businessNameET;
	EditText businessAddressET;
    Bitmap bitmap;
	SmartImageView roundPhotoIV;
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
		             if(previousIntent.getStringExtra("imageUrl") != null)
				     roundPhotoIV.setImageUrl(previousIntent.getStringExtra("imageUrl"));
				     businessAddressET.setText(previousIntent.getStringExtra("complete_address"));
				     businessNameET.setText(previousIntent.getStringExtra("business_name"));
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.overflow_options_menu, menu);
		SpannableString delinkstr = new SpannableString(menu.findItem(R.id.menu_delink).getTitle());
		delinkstr.setSpan(typeface, 0, delinkstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		menu.findItem(R.id.menu_delink).setTitle(delinkstr);
		SpannableString logoutstr = new SpannableString(menu.findItem(R.id.menu_logout).getTitle());
		logoutstr.setSpan(typeface, 0, logoutstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		menu.findItem(R.id.menu_logout).setTitle(logoutstr);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuItem item = menu.findItem(R.id.menu_delink);
		MenuItem logouItem = menu.findItem(R.id.menu_logout);
		if (globalVariable.getSelectedBusiness() != null) {
			item.setEnabled(true);

		} else {
			item.setEnabled(false);
		}
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
		super.onPause();
		globalVariable.saveSharedPreferences();
	}

	private void findThings() {
		roundPhotoIV = (SmartImageView) findViewById(R.id.photo);
		businessNameET = (EditText) findViewById(R.id.customer_name);
		businessAddressET = (EditText) findViewById(R.id.customer_address);
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
		businessAddressET.setTypeface(typeface2);
		link.setTypeface(typeface);
		createDialog = new CreateDialog(this);
		Pagelist = new ArrayList<String>();
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

			try {
				String url = ServerURLs.URL + ServerURLs.BUSINESS;
				JSONObject jsonObject2 = null;
				try {

					JSONObject jsonObject = new JSONObject()
							.put("name", businessNameET.getText().toString())
							.put("google_place_id", businessID)
							.put("google_api_result", completeResult)
							.put("face_book_page", fbPage.getId())
							.put("facebook_page_name",fbPage.getName() )
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
				if (!jsonFromServer.has("status")) {
					Gson gson = new Gson();
					String businessJson = jsonFromServer.toString();
					selectedBusiness = gson.fromJson(businessJson.toString(),
							Business.class);
					 ByteArrayOutputStream baos = new ByteArrayOutputStream();
					 ((BitmapDrawable)roundPhotoIV.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos);
					 byte[] b = baos.toByteArray();
					 String encodedImage = Base64.encodeToString(b,
					 Base64.DEFAULT);
					 selectedBusiness.setImageEncoded(encodedImage);
					globalVariable.getCustomer().getBusinesses()
							.add(selectedBusiness);
					globalVariable.setSelectedBusiness(null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return selectedBusiness;
		}

		@Override
		protected void onPostExecute(Business selectedBusiness) {
			progressDialog.dismiss();
			alertDialogBuilder = createDialog.createAlertDialog(
					"Business added successfully", null, false);
			alertDialogBuilder.setCancelable(false);
			alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					alertDialog.dismiss();
					Intent intent = new Intent(BusinessHomePageActivity.this,BusinessOfUserActivity.class);
					startActivity(intent);
				}
			});
			alertDialog = alertDialogBuilder.create();
			alertDialog.show();
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
			if (!businessNameET.isFocused())
				businessNameET.requestFocus();
			businessNameET.setError("You must enter business name.");
			bool = false;
			return bool;
		}
		if (businessAddressET.getText().toString().trim().length() == 0) {
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
								break;
							}
						}
						if (!isExist) {
							alertDialog.dismiss();
							progressDialog = createDialog.createProgressDialog(
									"Saving", "Please wait while we add this business to your login.", true,
									null);
							progressDialog.show();
							SaveToParseAndPreferencesAsync async = new SaveToParseAndPreferencesAsync();
							async.execute(new String[] { "Hello World" });
						}else
						{
							alertDialogBuilder = createDialog.createAlertDialog(
									"Warning", null, false);
							alertDialogBuilder.setMessage("This Business Already exist. Do You wish to create New Business?");
							alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									alertDialog.dismiss();
									businessNameET.setText("");
									businessAddressET.setText("");
								}
							});
							alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									alertDialog.dismiss();
									Intent intent = new Intent(BusinessHomePageActivity.this,BusinessOfUserActivity.class);
									startActivity(intent);
								}
							});
							alertDialog = alertDialogBuilder.create();
							alertDialog.show();
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
	} 

}
