package com.wduwg.watch.app;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
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

import com.apphance.android.Apphance;
import com.apphance.android.Apphance.Mode;
import com.apphance.android.common.Configuration;
import com.google.gson.Gson;
import com.mw.wduwg.model.Business;
import com.mw.wduwg.services.CreateDialog;
import com.mw.wduwg.services.GlobalVariable;
import com.mw.wduwg.services.JSONParser;
import com.parse.Parse;

public class SpalshFirstActivity extends Activity {

	TextView appNameTextView;
	TextView welcomeTextView;

	Typeface typeface;
	Typeface typeface2;
	String imeiNo;

	GlobalVariable globalVariable;
	CreateDialog createDialog;
	ProgressDialog progressDialgog;

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
		TelephonyManager  telephonyManager = (TelephonyManager)getSystemService(this.TELEPHONY_SERVICE);
		imeiNo = telephonyManager.getDeviceId();
		System.out.println(">>>>>>> IMEI NO:"+imeiNo);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.splash_first);
		globalVariable = (GlobalVariable) getApplicationContext();
		if (globalVariable.getSelectedBusiness()!= null) {
			System.out.println(">>>>>>> inside splash ");
			Intent intent = new Intent(SpalshFirstActivity.this,
					CountActivity.class);
			startActivity(intent);
		} else {
			setContentView(R.layout.splash_first);
			findThings();
			initializeThings();

			final ImageView logo1 = (ImageView) findViewById(R.id.splash_logo);
			final LinearLayout textLayout1 = (LinearLayout) findViewById(R.id.textLayout);

			int titleId = getResources().getIdentifier("action_bar_title",
					"id", "android");
			TextView yourTextView = (TextView) findViewById(titleId);
			yourTextView.setTextColor(Color.parseColor("#016AB2"));
            yourTextView.setTextSize(19);
			yourTextView.setTypeface(typeface);

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					TranslateAnimation animation = new TranslateAnimation(0, 0,
							0, -165);
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
									.loadAnimation(SpalshFirstActivity.this,
											R.anim.flip_in);
							logo1.startAnimation(flip_inAnimation);

							AlphaAnimation fadeIn = new AlphaAnimation(0.0f,
									1.0f);
							fadeIn.setDuration(3000);
							fadeIn.setFillAfter(true);
							textLayout1.setVisibility(View.VISIBLE);
							textLayout1.startAnimation(fadeIn);
							fadeIn.setAnimationListener(new AnimationListener() {

								@Override
								public void onAnimationStart(Animation animation) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onAnimationRepeat(
										Animation animation) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onAnimationEnd(Animation animation) {
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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
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

	public void connectFacebook(View v) {
//		Intent intent = new Intent(SpalshFirstActivity.this,
//				LoginFacebookActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		intent.putExtra("fromContext", false);
//		startActivity(intent);
		if(globalVariable.getSelectedBusiness() != null)
		{
			Intent intent = new Intent(SpalshFirstActivity.this,CountActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.anim_out,
					R.anim.anim_in);
		}
		else
		{
		 createDialog = new CreateDialog(SpalshFirstActivity.this);
		 progressDialgog = createDialog.createProgressDialog("Loading...", "Please wait for a while", true, null);
		 progressDialgog.show();
		 BusinessAsyncTask asynctask = new BusinessAsyncTask();
		 asynctask.execute();
		}
		
	}
	
	private class BusinessAsyncTask extends AsyncTask<Void, Void, Boolean>
	{

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try{
				System.out.println(">>>>>>> response");
				JSONParser jsonparser = new JSONParser(SpalshFirstActivity.this);
				List<NameValuePair> param = new ArrayList<NameValuePair>();
				param.add(new BasicNameValuePair("imei_no", imeiNo));
				JSONObject jsonobject = jsonparser.getJSONObjectFromUrlAfterHttpGet("http://dcounter.herokuapp.com/businesses/imei_business.json", param);
				System.out.println(">>>>>>> response"+jsonobject);
				if(jsonobject.getString("status").equals("ok"))
		        {
		        	Gson gson = new Gson();
		        	String businessJsonString = jsonobject.getString("business");
		        	System.out.println(">>>>>>> business:"+businessJsonString);
		        	Business B = gson.fromJson(businessJsonString, Business.class);
		        	globalVariable.setMenIn(0);
					globalVariable.setMenOut(0);
					globalVariable.setWomenIn(0);
					globalVariable.setWomenOut(0);
					globalVariable.setSelectedBusiness(B);
					return true;
		        }
				else
				{
					return false;
				}
			 }catch(Exception e)
			{
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			progressDialgog.dismiss();
			if(result)
			{
			Intent intent = new Intent(SpalshFirstActivity.this,CountActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.anim_out,
					R.anim.anim_in);
			}
			else
			{
				Toast.makeText(SpalshFirstActivity.this, "Business for current Device does not exist", Toast.LENGTH_SHORT).show();
			}
		}
		
	}

}
