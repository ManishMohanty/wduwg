package com.example.wduwg.tiles;

import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings.TextSize;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.apphance.android.Apphance;
import com.example.wduwg.tiles.R;
import com.mw.wduwg.adapter.TabsPagerAdapter;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ReportActualActvivity extends FragmentActivity implements
		ActionBar.TabListener {
	

	public static String[] tabs = new String[]{"Today", "Day of Week", "Last Week", "Last Month"};

	private ViewPager viewPager;
	private TabsPagerAdapter adapter;
	private ActionBar actionBar;
//	String eventName;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reports);
//		Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/ufonts.com_segoe_ui_semibold.ttf");
		Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/OpenSans-Light.ttf");
		
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		adapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(adapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		for (int i = 0; i < tabs.length; i++) {
			TextView t = new TextView(ReportActualActvivity.this);
			t.setTypeface(typeface);
			t.setText(tabs[i]);
			actionBar.addTab(actionBar.newTab()
                    .setCustomView(t)
                    .setTabListener(this),i);
		}

		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	protected void onStart() {
		super.onStart();
		Apphance.onStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Apphance.onStop(this);
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
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		this.setResult(RESULT_OK);
		finish();
	}
}
