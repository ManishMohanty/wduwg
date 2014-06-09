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

import com.example.wduwg.R;
import com.mw.wduwg.services.GlobalVariable;

public class ReportFragment4 extends Fragment {

	GlobalVariable globalVariable;
	Context context;
	WebView webview;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
		
        View rootView = inflater.inflate(R.layout.reports_fragment4, container, false);
        context = getActivity();
        globalVariable = (GlobalVariable)context.getApplicationContext();
        webview = (WebView) rootView.findViewById(R.id.webView);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setJavaScriptEnabled(true);
        String url = "http://dcounter.herokuapp.com/counters/monthly_graph?business_id="+globalVariable.getSelectedBusiness().getId().get$oid();
        webview.loadUrl(url);
        
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
