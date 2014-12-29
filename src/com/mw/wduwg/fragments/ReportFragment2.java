package com.mw.wduwg.fragments;

import org.achartengine.GraphicalView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.wduwg.tiles.R;
import com.mw.wduwg.services.GlobalVariable;

@SuppressLint("ValidFragment")
public class ReportFragment2 extends Fragment {

	static int YAXIS_MAX = 0;

	Context context;

	LinearLayout chartLL1;
	LinearLayout chartLL2;


	private GraphicalView mChartView;
	GlobalVariable globalVariable;

	ImageView nextButton, previousButton;

	int c = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.reports_fragment2, container,
				false);
		context = getActivity();
		globalVariable = (GlobalVariable) context.getApplicationContext();

		WebView webView1 = (WebView) rootView.findViewById(R.id.webView1);
		webView1.getSettings().setLoadWithOverviewMode(true);
		webView1.getSettings().setUseWideViewPort(true);
		webView1.getSettings().setJavaScriptEnabled(true);
		String url = "http://dcounter.herokuapp.com/counters/day_of_week?business_id="+globalVariable.getSelectedBusiness().getId().get$oid() ;
		webView1.loadUrl(url);
		return rootView;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			Activity a = getActivity();
			if (a != null)
				a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}


}
