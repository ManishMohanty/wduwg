package com.example.wduwg;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.mw.wduwg.services.GlobalVariable;

public class BusinessesOfUserActivity extends Activity{

	ListView listView;
	GlobalVariable globalVariable;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_businesses_of_user);
		int titleId = getResources().getIdentifier("action_bar_title", "id",
				"android");
		TextView yourTextView = (TextView) findViewById(titleId);
		yourTextView.setTextSize(19);
		yourTextView.setTextColor(Color.parseColor("#016AB2"));
		yourTextView.setTypeface(Typeface.createFromAsset(getAssets(), "Fonts/OpenSans-Bold.ttf"));
		globalVariable = (GlobalVariable)getApplicationContext();
		ListView listView = (ListView) findViewById(R.id.listView);
		CustomAdapter adapter = new CustomAdapter(
				BusinessesOfUserActivity.this, globalVariable.getCustomer().getBusinesses());
		listView.setAdapter(adapter);
	}
	

}
