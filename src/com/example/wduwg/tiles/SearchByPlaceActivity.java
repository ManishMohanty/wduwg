package com.example.wduwg.tiles;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphance.android.activity.ApphanceActivity;

public class SearchByPlaceActivity extends ApphanceActivity {
	EditText placeNameET;
	EditText placeCityET;
	EditText placeStateET;
	Intent nextIntent;

	Typeface typeface;
	Typeface typeface2;
	String place = "";

	private void findThings() {
		placeNameET = (EditText) findViewById(R.id.place_name);
		placeCityET = (EditText) findViewById(R.id.place_city);
		placeStateET = (EditText) findViewById(R.id.place_state);
	}

	private void initializeThings() {
		typeface = Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Light.ttf");
		typeface2 = Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Bold.ttf");
		placeNameET.setTypeface(typeface2);
		placeCityET.setTypeface(typeface2);
		placeStateET.setTypeface(typeface2);
		Intent preveIntent = getIntent();
		if(preveIntent.hasExtra("city"))
		{
			placeCityET.setText(preveIntent.getStringExtra("city"));
		}
		if(preveIntent.hasExtra("state"))
		{
			placeStateET.setText(preveIntent.getStringExtra("state"));
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_by_place);

		findThings();
		initializeThings();

		((RelativeLayout) findViewById(R.id.search_place_LL))
				.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(getCurrentFocus()
								.getWindowToken(), 0);
						return false;
					}
				});

		// Action Bar font
		int titleId = getResources().getIdentifier("action_bar_title", "id",
				"android");
		TextView yourTextView = (TextView) findViewById(titleId);
		yourTextView.setTextColor(Color.parseColor("#016AB2"));
		yourTextView.setTextSize(19);
		yourTextView.setTypeface(typeface2);

	}

	private boolean validate() {
		boolean temp = true;
		if (placeNameET.getText().toString().trim().length() == 0) {
			placeNameET.setError("You must enter a business name.");
			temp = false;
		} else
			place = place.concat(placeNameET.getText().toString().trim());
		if (placeCityET.getText().toString().trim().length() > 0)
			place = place.concat(" " + placeCityET.getText().toString().trim());
		if (placeStateET.getText().toString().trim().length() > 0)
			place = place
					.concat(" " + placeStateET.getText().toString().trim());
		return temp;
	}

	public void searchByPlace(View v) {
		if (!validate())
			return;
		nextIntent = new Intent(SearchByPlaceActivity.this,
				IdentifyingBusinessActivity.class);
		nextIntent.putExtra("place", place);
		startActivity(nextIntent);
		overridePendingTransition(R.anim.anim_out, R.anim.anim_in);
	}

}
