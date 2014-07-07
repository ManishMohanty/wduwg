package com.example.wduwg.tiles;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.example.wduwg.tiles.R;
import com.manateeworks.cameraDemo.ActivityCapture;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ScanerDetailActivity extends Activity {
	TextView fullNameTV, addressTV, licenceTV, genderTV, dobTV, issueTV,
			expiryTV, zipTV, ageTV, zipLabel, nameLabel, addressLabel,
			licenceLabel, genderLabel, dobLabel, issueLabel, expiryLabel,
			ageLabel, ageStatusTV, expiryStatusTV;
	LinearLayout ageCircle, expiryCircle;

	Button btnScan;
	
	SharedPreferences sharedPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scaner_detail_activity);
		int titleId = getResources().getIdentifier("action_bar_title", "id",
				"android");
		TextView yourTextView = (TextView) findViewById(titleId);
		yourTextView.setTextSize(19);
		yourTextView.setTextColor(Color.parseColor("#016AB2"));
		yourTextView.setTypeface(Typeface
				.createFromAsset(getAssets(), "Fonts/OpenSans-Light.ttf"));
		String rawData = getIntent().getStringExtra("rawData");
		
		sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);

		ageCircle = (LinearLayout) findViewById(R.id.age_circle);
		expiryCircle = (LinearLayout) findViewById(R.id.expiry_circle);

		
		genderTV = (TextView) findViewById(R.id.gender);
		
		zipTV = (TextView) findViewById(R.id.zip);
		ageTV = (TextView) findViewById(R.id.age);
		ageStatusTV = (TextView) findViewById(R.id.age_status);
		expiryStatusTV = (TextView) findViewById(R.id.expiry_status);

		Typeface typeFace = Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Light.ttf");
		genderLabel = (TextView) findViewById(R.id.genderLabel);
		genderLabel.setTypeface(typeFace);
		zipLabel = (TextView) findViewById(R.id.zipLabel);
		zipLabel.setTypeface(typeFace);
		ageLabel = (TextView) findViewById(R.id.ageLabel);
		ageLabel.setTypeface(typeFace);

		btnScan = (Button) findViewById(R.id.btnScan);
		Typeface typefacebtn = Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Light.ttf");
		btnScan.setTypeface(typefacebtn);

		convertBarCodeData(rawData);
	}

	enum Code {
		DAA, DAG, DAI, DAJ, DAK, DAQ, DAS, DAT, DAU, DAW, DAY, DBA, DBB, DBC, DBD, DCS, DCT;
	}

	public String convertBarCodeData(String scannedData) {
		String fullName = null, streetAddress = null, zip = "", city = null, state = null, driverLicence = null, restriction = null, endorsements = null, height = null, weight = null, eyeColor = null, expirationDate = null, dateOfBirth = null, gender = null, issueDate = null, lastName = null, givenName = null;
		if (scannedData.contains("DL")) {
			SharedPreferences msharedPreference = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
			Editor editor = msharedPreference.edit();

			String[] data = scannedData.split("\n");

			for (int i = 0; i < data.length; i++) {
				if (data[i].length() > 3) {
					Log.d("****", "data=====" + data[i]);
					try {
						switch (Code
								.valueOf(data[i].substring(0, 3).toString())) {
						case DAA:
							fullName = data[i].substring(3, data[i].length());
							break;
						case DAG:
							streetAddress = data[i].substring(3,
									data[i].length()).trim();
							break;
						case DAI:
							city = data[i].substring(3, data[i].length());
							break;
						case DAJ:
							state = data[i].substring(3, data[i].length());
							break;
						case DAQ:
							driverLicence = data[i].substring(3,
									data[i].length()).trim();
							break;
						case DAS:
							restriction = data[i]
									.substring(3, data[i].length());
							break;
						case DAT:
							endorsements = data[i].substring(3,
									data[i].length());
							break;
						case DAU:
							height = data[i].substring(3, data[i].length());
							break;
						case DAW:
							weight = data[i].substring(3, data[i].length());
							break;
						case DAY:
							eyeColor = data[i].substring(3, data[i].length());
							break;
						case DBA:
							expirationDate = data[i].substring(3, 5).toString()
									+ "/" + data[i].substring(5, 7) + "/"
									+ data[i].substring(7, data[i].length());
							break;
						case DBB:
							dateOfBirth = data[i].substring(3, 5).toString()
									+ "/" + data[i].substring(5, 7) + "/"
									+ data[i].substring(7, data[i].length());
							break;
						case DBC:

							if (data[i].substring(3, data[i].length())
									.equalsIgnoreCase("1")) {
								gender = "M";
								int men_in =  msharedPreference.getInt("men_in", 0) + 1;
								editor.putInt("men_in", men_in);
								int men_in_static = msharedPreference.getInt("men_in_static", 0) + 1;
								editor.putInt("men_in_static", men_in_static);
							} else {
								gender = "F";
								int women_in = msharedPreference.getInt("women_in", 0) + 1;
								editor.putInt("women_in", women_in);
								int women_in_static = msharedPreference.getInt("women_in_static", 0) + 1;
								editor.putInt("women_in_static", women_in_static);
							}
							break;
						case DBD:
							issueDate = data[i].substring(3, 5).toString()
									+ "/" + data[i].substring(5, 7) + "/"
									+ data[i].substring(7, data[i].length());

							break;
						case DCS:
							lastName = data[i].substring(3, data[i].length());
							break;
						case DCT:
							fullName = data[i].substring(3, data[i].length());
							break;
						case DAK:
							if(data[i].length() > 7)
							zip = data[i].substring(3, 8);
							else
							zip = data[i].substring(3, data[i].length());
						default:
							break;
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
            editor.commit();
			int birthYear = Integer.parseInt(dateOfBirth.split("/")[2]);
			int birthMonth = Integer.parseInt(dateOfBirth.split("/")[0]);
			int birthDay = Integer.parseInt(dateOfBirth.split("/")[1]);
			Calendar calendar = Calendar.getInstance();
			int currentYear = calendar.get(Calendar.YEAR);
			int currentMonth = calendar.get(Calendar.MONTH);
			int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

			int age = 0;

			if (currentMonth < birthMonth || currentDay < birthMonth) {
				age = (currentYear - birthYear) - 1;
			} else {
				age = currentYear - birthYear;
			}

			String sb = new String("FullName: " + fullName + "\n Address: "
					+ streetAddress + "\n City: " + city + "\n Height: "
					+ height + "\n weight:" + weight + "\n gender:" + gender
					+ "\n DOB: " + dateOfBirth + "\n Issue date: " + issueDate
					+ "\n expiration date: " + expirationDate);
			Typeface typeFaceBold = Typeface.createFromAsset(getAssets(),
					"Fonts/OpenSans-Light.ttf");

			findViewById(R.id.detailLayout).setVisibility(View.VISIBLE);
			genderTV.setText(gender);
			genderTV.setTypeface(typeFaceBold);
			zipTV.setTypeface(typeFaceBold);
			zipTV.setText(zip);
			ageStatusTV.setTypeface(typeFaceBold);
			expiryStatusTV.setTypeface(typeFaceBold);
			ageTV.setText("" + age);

			if (age > Integer.parseInt(sharedPrefs.getString("prefMinage","21"))) {
				ageStatusTV.setText("Age is Valid");
				ageCircle.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.green_circle));
			} else {
				ageStatusTV.setText("Age is not valid");
				ageCircle.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.red_circle));
			}
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				Date d1 = sdf.parse(expirationDate);
				Date d2 = sdf.parse(currentMonth + "/" + currentDay + "/"
						+ currentYear);
				if (d1.compareTo(d2) > 0) {
					expiryCircle.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.green_circle));
					expiryStatusTV.setText("Not expired");
				} else {
					expiryCircle.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.red_circle));
					expiryStatusTV.setText("expired");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			return sb;
		} else {
			findViewById(R.id.detailLayout).setVisibility(View.GONE);

			Toast.makeText(getApplicationContext(), "invalid Driving Licence.",
					Toast.LENGTH_SHORT).show();
			return null;
		}
	}

	public void done(View v) {
		this.setResult(RESULT_OK);
		finish();
	}

	public void btnScan_click(View v) {
		Intent scanerIntent = new Intent(this, ActivityCapture.class);
		startActivity(scanerIntent);
		this.setResult(RESULT_OK);
		this.finish();
	}
	
	@Override
	public void onBackPressed() {
		this.setResult(RESULT_OK);
		finish();
	}

}
