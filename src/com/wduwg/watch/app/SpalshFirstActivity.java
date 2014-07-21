package com.wduwg.watch.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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

import com.apphance.android.Apphance;
import com.apphance.android.Apphance.Mode;
import com.apphance.android.common.Configuration;
import com.mw.wduwg.services.GlobalVariable;
import com.parse.Parse;

public class SpalshFirstActivity extends Activity {

	TextView appNameTextView;
	TextView welcomeTextView;

	Typeface typeface;
	Typeface typeface2;

	GlobalVariable globalVariable;

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
//		setContentView(R.layout.splash_first);
		globalVariable = (GlobalVariable) getApplicationContext();
		if (globalVariable.getFb_access_token() != null) {
			System.out.println(">>>>>>> inside splash ");
			Intent intent = new Intent(SpalshFirstActivity.this,
					MainActivity.class);
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
							0, -220);
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

							Animation rotateimage = AnimationUtils
									.loadAnimation(SpalshFirstActivity.this,
											R.anim.custom_anim);
							logo1.startAnimation(rotateimage);

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
		Intent intent = new Intent(SpalshFirstActivity.this,
				LoginFacebookActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("fromContext", false);
		startActivity(intent);
	}

}
