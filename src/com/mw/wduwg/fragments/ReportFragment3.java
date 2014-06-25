package com.mw.wduwg.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.mw.wduwg.services.GlobalVariable;
import com.wduwg.owner.app.R;

public class ReportFragment3 extends Fragment {

	GlobalVariable globalVariable;
	Context context;
	WebView webview1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.reports_fragment3, container,
				false);
		context = getActivity();
        globalVariable = (GlobalVariable)context.getApplicationContext();
        webview1 = (WebView)rootView.findViewById(R.id.webView);
        webview1.getSettings().setLoadWithOverviewMode(true);
        webview1.getSettings().setUseWideViewPort(true);
        webview1.getSettings().setJavaScriptEnabled(true);
        String url = "http://dcounter.herokuapp.com/counters/weekly_graph?business_id="+globalVariable.getSelectedBusiness().getId().get$oid();
        System.out.println("URL:" +url);
        webview1.loadUrl(url);
		return rootView;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
	    super.setUserVisibleHint(isVisibleToUser);
	    if(isVisibleToUser) {
	        Activity a = getActivity();
	        if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	    }
	}
	

	
	

}