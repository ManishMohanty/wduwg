package com.mw.wduwg.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wduwg.counter.R;
import com.mw.wduwg.services.CreateDialog;
import com.mw.wduwg.services.GlobalVariable;

public class ReportFragment1 extends Fragment {

	Context context;
	GlobalVariable globalVariable;
	
	ProgressDialog progressDialog;
	CreateDialog createDialog;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.reports_fragment1, container,
				false);
		createDialog = new CreateDialog(getActivity());
        progressDialog = progressDialog = createDialog.createProgressDialog("Loading",
				"Please wait for a while.", true, null);
		context = getActivity();
		globalVariable = (GlobalVariable)context.getApplicationContext();
		WebView webView = (WebView) rootView.findViewById(R.id.webView);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new myWebClient());
		String url = "http://dcounter.herokuapp.com/counters/daily_graph?business_id=" + globalVariable.getSelectedBusiness().getId().get$oid();
		webView.loadUrl(url);
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


	public class myWebClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
            progressDialog.show();
        }
 
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
 
            view.loadUrl(url);
            return true;
 
        }
 
        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
        	progressDialog.dismiss();
            super.onPageFinished(view, url);
        }
    }
	
}
