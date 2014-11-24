package com.wduwg.watch.app;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mw.wduwg.model.Business;
import com.mw.wduwg.services.CreateDialog;
import com.mw.wduwg.services.GlobalVariable;
import com.mw.wduwg.services.JSONParser;

public class SpalshFirstActivity extends Activity {

	TextView appNameTextView;
	TextView welcomeTextView;

	Typeface typeface;
	Typeface typeface2;
	String imeiNo;

	GlobalVariable globalVariable;
	CreateDialog createDialog;
	ProgressDialog progressDialgog;
	AlertDialog.Builder alertdialogbuilder;
	AlertDialog alertDialog;

	private void findThings() {
		appNameTextView = (TextView) findViewById(R.id.app_name_text);
		welcomeTextView = (TextView) findViewById(R.id.welcome_text);
	}

	private void initializeThings() {
		typeface = Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Bold.ttf");
		typeface2 = Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Light.ttf");
		appNameTextView.setTypeface(typeface);
		welcomeTextView.setTypeface(typeface);		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createDialog = new CreateDialog(SpalshFirstActivity.this);
		globalVariable = (GlobalVariable) getApplicationContext();
		imeiNo = globalVariable.getIMEINo();
		if (!globalVariable.isInternet()) {
			if(globalVariable.getSelectedBusiness() != null){
				alertdialogbuilder = createDialog.createAlertDialog("Network Error",
							"You are not connected to the network. Counts will be synced once network is available. Press OK to start counting",
							false);
			}
			else {
				alertdialogbuilder = createDialog.createAlertDialog("Network Error",
						"You are not connected to the network. Please establish a connection first for using WDUWG.",
						false);
			}
			
			alertdialogbuilder.setNegativeButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							alertDialog.dismiss();
							if(globalVariable.getSelectedBusiness()!= null) {
								Intent intent = new Intent(SpalshFirstActivity.this,
										CountActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
										| Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(intent);
							}
							else
							System.exit(0);
						}
					});
			
			alertDialog = alertdialogbuilder.create();
			alertDialog.show();
		} else {
			if (globalVariable.getSelectedBusiness() != null) {
				Intent intent = new Intent(SpalshFirstActivity.this, CountActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
			} else {
				setContentView(R.layout.splash_first);
				findThings();
				initializeThings();

				final ImageView logo1 = (ImageView) findViewById(R.id.splash_logo);
				final LinearLayout textLayout1 = (LinearLayout) findViewById(R.id.textLayout);

				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						TranslateAnimation animation = new TranslateAnimation(
								0, 0, 0, -165);
						animation.setDuration(800);
						animation.setFillAfter(true);
						animation.setAnimationListener(new AnimationListener() {

							public void onAnimationStart(Animation animation) {
								// TODO Auto-generated method stub

							}

							public void onAnimationRepeat(Animation animation) {
								// TODO Auto-generated method stub

							}

							public void onAnimationEnd(Animation animation) {
								// TODO Auto-generated method stub

								int[] location = new int[2];
								logo1.getLocationInWindow(location);
								logo1.clearAnimation();
								LayoutParams lp = new LayoutParams(logo1
										.getLayoutParams());
								lp.leftMargin = location[0];
								lp.topMargin = 60;
								logo1.setLayoutParams(lp);

								Animation flip_inAnimation = AnimationUtils
										.loadAnimation(
												SpalshFirstActivity.this,
												R.anim.flip_in);
								logo1.startAnimation(flip_inAnimation);

								AlphaAnimation fadeIn = new AlphaAnimation(
										0.0f, 1.0f);
								fadeIn.setDuration(3000);
								fadeIn.setFillAfter(true);
								textLayout1.setVisibility(View.VISIBLE);
								textLayout1.startAnimation(fadeIn);
								fadeIn.setAnimationListener(new AnimationListener() {

									@Override
									public void onAnimationStart(
											Animation animation) {
										// TODO Auto-generated method stub

									}

									@Override
									public void onAnimationRepeat(
											Animation animation) {
										// TODO Auto-generated method stub

									}

									@Override
									public void onAnimationEnd(
											Animation animation) {
										// TODO Auto-generated method stub
										logo1.clearAnimation();
										findViewById(R.id.buttonLayout)
												.setVisibility(View.VISIBLE);
										findViewById(R.id.connectFBIV)
												.setVisibility(View.VISIBLE);
									}
								});
							}
						});
						logo1.startAnimation(animation);
					}
				}, 1000);

			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onPause() {
		super.onPause();
		globalVariable.saveSharedPreferences();
	}
	

	public void verifyImeiNo(View v) {

		if (globalVariable.getSelectedBusiness() != null) {
			Intent intent = new Intent(SpalshFirstActivity.this,
					CountActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.anim_out, R.anim.anim_in);
		} else {

			progressDialgog = createDialog.createProgressDialog("Loading...",
					"Please wait while we initialize the watch", true, null);
			progressDialgog.show();
			BusinessAsyncTask asynctask = new BusinessAsyncTask();
			asynctask.execute();
		}

	}

	private class BusinessAsyncTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				JSONParser jsonparser = new JSONParser(SpalshFirstActivity.this);
				List<NameValuePair> param = new ArrayList<NameValuePair>();
				param.add(new BasicNameValuePair("imei_no", imeiNo));
				JSONObject jsonobject = jsonparser
						.getJSONObjectFromUrlAfterHttpGet(
								"http://dcounter.herokuapp.com/businesses/imei_business.json",
								param);
				if (jsonobject.getString("status").equals("ok")) {
					Gson gson = new Gson();
					String businessJsonString = jsonobject
							.getString("business");
					Business B = gson.fromJson(businessJsonString,
							Business.class);
					globalVariable.setMenIn(0);
					globalVariable.setMenOut(0);
					globalVariable.setWomenIn(0);
					globalVariable.setWomenOut(0);
					globalVariable.setSelectedBusiness(B);
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				//e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			progressDialgog.dismiss();
			if (result) {
				Intent intent = new Intent(SpalshFirstActivity.this,
						CountActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.anim_out, R.anim.anim_in);
			} else {
				Toast.makeText(SpalshFirstActivity.this,
						"Business for current Device does not exist",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
