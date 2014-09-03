package com.example.wduwg.tiles;

import java.util.Timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.transition.ChangeBounds;
import android.util.DisplayMetrics;
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
import com.mw.wduwg.services.CreateDialog;
import com.mw.wduwg.services.GlobalVariable;
import com.mw.wduwg.services.SchedulerCount;

public class MainActivity extends Activity
// implements OnClickListener
{
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		globalVariable.saveSharedPreferences();
	}

	Typeface typeface;
	TextView delinkTV;
	TextView continueText;
	CreateDialog createDialog;
	AlertDialog.Builder alertDialogBuilder;
	AlertDialog alertDialog;
	GlobalVariable globalVariable;
	SharedPreferences sharedPreferences;
	AlphaAnimation fadeIn;
	AlphaAnimation fadeOut1;
	public static final String MQA_APP_KEY = "c834ac33419d5cb72dea438d7c29217689d66c7a";
	TextView appNameTextView;
	TextView welcomeTextView;
	TextView messageText;

	private void findThings() {
		delinkTV = (TextView) findViewById(R.id.delinkTV);
		appNameTextView = (TextView) findViewById(R.id.app_name_text);
		welcomeTextView = (TextView) findViewById(R.id.welcome_text);
		continueText = (TextView) findViewById(R.id.continuetext);

	}

	private void initializeThings() {
		createDialog = new CreateDialog(this);
		typeface = Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Light.ttf");
		appNameTextView.setTypeface(Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Bold.ttf"));
		delinkTV.setTypeface(Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Bold.ttf"));
		continueText.setTypeface(Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Bold.ttf"));
		welcomeTextView.setTypeface(typeface);
		globalVariable =(GlobalVariable) getApplicationContext();
		messageText = (TextView) findViewById(R.id.message_text);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_main);
		DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

//        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
//        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
//        Toast.makeText(this, "height:"+dpHeight+"dp\n Width:"+dpWidth+"dp", Toast.LENGTH_LONG).show();
		findThings();
		initializeThings();

		// IBM MQA
		Configuration configuration = new Configuration.Builder(this)
				.withAPIKey(MQA_APP_KEY) // Provides IBM Mobile Quality
											// Assurance
				.withMode(Mode.QA) // Selects IBM Mobile Quality Assurance mode
				.withReportOnShakeEnabled(true) // Enables shake report trigger
				.build();

		Apphance.startNewSession(MainActivity.this, configuration);

		animation();
	}
	
	private void animation()
	{
		final ImageView logo = (ImageView) findViewById(R.id.splash_logo);

		int titleId = getResources().getIdentifier("action_bar_title", "id",
				"android");
		TextView yourTextView = (TextView) findViewById(titleId);
		yourTextView.setTextColor(Color.parseColor("#016AB2"));
		yourTextView.setTextSize(19);
		yourTextView.setTypeface(Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Bold.ttf"));

		fadeIn = new AlphaAnimation(0.0f, 1.0f);
		fadeIn.setDuration(3000);
		fadeIn.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				if(globalVariable.getSelectedBusiness()!=null)
				delinkTV.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
			}
		});

		fadeOut1 = new AlphaAnimation(1.0f, 0.0f);
		fadeOut1.setDuration(1200);
		fadeOut1.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				delinkTV.setVisibility(View.GONE);
			}
		});
		
		messageText.setTypeface(Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Light.ttf"));
		final LinearLayout textLayout = (LinearLayout) findViewById(R.id.textLayout);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				TranslateAnimation animation = new TranslateAnimation(0, 0, 0,
						-165);
				animation.setDuration(800);
				animation.setFillAfter(true);
				// animation.setFillEnabled(true);
				animation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						int[] location = new int[2];
						logo.getLocationInWindow(location);
						logo.clearAnimation();
						LayoutParams lp = new LayoutParams(logo
								.getLayoutParams());
						lp.leftMargin = location[0];
						lp.topMargin = 60;
						logo.setLayoutParams(lp);
						textLayout.setVisibility(View.VISIBLE);
                        messageText.setText("Please wait while we determine if the device is already registered");
						LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
						buttonLayout.setVisibility(View.VISIBLE);

						AlphaAnimation fadeInForMessage = new AlphaAnimation(0.0f, 1.0f);
						fadeInForMessage.setDuration(2000);
						fadeInForMessage.setFillAfter(true);
						fadeInForMessage.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
							}

							@Override
							public void onAnimationRepeat(Animation animation) {
							}

							@Override
							public void onAnimationEnd(Animation animation) {

								new Handler().postDelayed(new Runnable() {
									@Override
									public void run() {
										AlphaAnimation MessagefadeOut = new AlphaAnimation(
												1.0f, 0.0f);
										MessagefadeOut.setDuration(1200);
										MessagefadeOut.setFillAfter(true);
										messageText.startAnimation(MessagefadeOut);
										MessagefadeOut.setAnimationListener(new AnimationListener() {

											@Override
											public void onAnimationStart(
													Animation animation) {
											}

											@Override
											public void onAnimationRepeat(
													Animation animation) {
											}

											@Override
											public void onAnimationEnd(
													Animation animation) {
												
												Animation flip_inAnimation = AnimationUtils
														.loadAnimation(
																MainActivity.this,
																R.anim.flip_in);
												logo.startAnimation(flip_inAnimation); 
												AlphaAnimation fadeInMessageAgain = new AlphaAnimation(
														0.0f, 1.0f);
												fadeInMessageAgain.setDuration(3000);
												fadeInMessageAgain.setFillAfter(true);
												fadeInMessageAgain.setAnimationListener(new AnimationListener() {

													@Override
													public void onAnimationStart(
															Animation animation) {
													}

													@Override
													public void onAnimationRepeat(
															Animation animation) {
													}

													@Override
													public void onAnimationEnd(
															Animation animation) {
														logo.clearAnimation();
													}
												});

												
												if (globalVariable.getSelectedBusiness()!= null) {
													

													SpannableString spannableStr = SpannableString.valueOf(globalVariable.getSelectedBusiness().getName());
													spannableStr.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 
															spannableStr.length(), 0);
													SpannableStringBuilder msg = new SpannableStringBuilder("This device is linked to ");
													msg.append(spannableStr);
													msg.append("  Please hit continue to start using the app or relink the device.");
													messageText.setText(msg);
													messageText
															.startAnimation(fadeInMessageAgain);
													continueText
															.setVisibility(View.VISIBLE);
													continueText
															.startAnimation(fadeIn);
													delinkTV.setVisibility(View.VISIBLE);
													delinkTV.startAnimation(fadeIn);
												} else {
													messageText
															.setText("This device is not linked to any business. Please hit continue to register the device with a business.");
													messageText
															.startAnimation(fadeInMessageAgain);
													delinkTV.startAnimation(fadeOut1);
													
													continueText
															.setVisibility(View.VISIBLE);
													continueText
															.startAnimation(fadeIn);
												}

											}
										});

									}
								}, 1000);
							}
						});
						textLayout.startAnimation(fadeInForMessage);
					}
				});
				logo.startAnimation(animation);
			}
		}, 2000);
	}
	
	

	public void oncontinue(View v) {
		Intent nextIntent = null;
		if (globalVariable.getSelectedBusiness()!= null) {
//			nextIntent = new Intent(this, BusinessHomePageActivity.class);
			nextIntent = new Intent(this, BusinessDashboardActivity.class);
			nextIntent.putExtra("isFromMain", true);
		} else {
//			nextIntent = new Intent(this, IdentifyingBusinessActivity.class);
			nextIntent = new Intent(this, BusinessOfUserActivity.class);
		}
		startActivity(nextIntent);
		overridePendingTransition(R.anim.anim_out, R.anim.anim_in);
	}

public void onDelink(View v) {
		

		alertDialogBuilder = createDialog
				.createAlertDialog(
						"Delink",
						"Do you wish to delink business with device ?",
						false);
		alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
				SchedulerCount scheduledTask = new SchedulerCount(MainActivity.this);
				Timer timer = new Timer();
				timer.scheduleAtFixedRate(scheduledTask, 1000, 10000);
				scheduledTask.run();
				SchedulerCount.event = globalVariable.getSelectedEvent();
				timer.cancel();
				globalVariable.setSelectedBusiness(null);
				globalVariable.setSelectedEvent(null);
				globalVariable.setMenIn(0);
				globalVariable.setMenOut(0);
				globalVariable.setWomenIn(0);
				globalVariable.setWomenOut(0);
				globalVariable.fbPostOff();
				globalVariable.saveSharedPreferences();
				Intent nextIntent = new Intent(MainActivity.this, BusinessOfUserActivity.class);
				startActivity(nextIntent);
				overridePendingTransition(R.anim.anim_out, R.anim.anim_in);
			}
		});
		alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
			}
		});
		alertDialog = alertDialogBuilder.create();
		alertDialog.show();

	}

	public void singleOKButton(final AlertDialog.Builder alertDialogBuilder) {
		alertDialogBuilder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
//						Intent nextIntent = new Intent(MainActivity.this,
//								IdentifyingBusinessActivity.class);
						Intent nextIntent = new Intent(MainActivity.this,
								BusinessOfUserActivity.class);
						startActivity(nextIntent);
						overridePendingTransition(R.anim.anim_out,
								R.anim.anim_in);
						System.out.println("hello");
					}
				});
	}

	@Override
	protected void onResume() {
		super.onResume();
		animation();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

}