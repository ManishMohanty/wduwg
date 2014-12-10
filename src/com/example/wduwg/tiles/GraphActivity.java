package com.example.wduwg.tiles;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mw.wduwg.services.CreateDialog;
import com.mw.wduwg.services.GlobalVariable;
import com.mw.wduwg.services.JSONParser;
import com.shinobicontrols.charts.Annotation;
import com.shinobicontrols.charts.AnnotationStyle;
import com.shinobicontrols.charts.AnnotationsManager;
import com.shinobicontrols.charts.BarSeriesStyle;
import com.shinobicontrols.charts.CartesianSeries;
import com.shinobicontrols.charts.CategoryAxis;
import com.shinobicontrols.charts.ChartFragment;
import com.shinobicontrols.charts.ColumnSeries;
import com.shinobicontrols.charts.ColumnSeriesStyle;
import com.shinobicontrols.charts.DataPoint;
import com.shinobicontrols.charts.DefaultTooltipView;
import com.shinobicontrols.charts.Legend;
import com.shinobicontrols.charts.LineSeries;
import com.shinobicontrols.charts.LineSeriesStyle;
import com.shinobicontrols.charts.NumberAxis;
import com.shinobicontrols.charts.NumberRange;
import com.shinobicontrols.charts.PointStyle;
import com.shinobicontrols.charts.SeriesStyle;
import com.shinobicontrols.charts.SeriesStyle.FillStyle;
import com.shinobicontrols.charts.ShinobiChart;
import com.shinobicontrols.charts.SimpleDataAdapter;
import com.shinobicontrols.charts.Tooltip;

public class GraphActivity extends Activity {
	WebView webview ;
	RelativeLayout custom_graph;
	Button changeStartBtn , changeEndDate;
	TextView endsDate,startsDate;
	String url ;
	ProgressDialog progressDialog;
	CreateDialog createDialog;
	AlertDialog.Builder alertDialogBuilder;
	AlertDialog alertDialog;
	Context context;
	GlobalVariable globalVariable;
	private ChartFragment chart;

	private int year;
    private int month;
    private int day;
	
    static final int START_DATE_PICKER_ID = 1111; 
    static final int END_DATE_PICKER_ID = 2222; 
    double min =0 ,max= 0;
    
   
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);
		
		createDialog = new CreateDialog(this);
		progressDialog = progressDialog = createDialog.createProgressDialog("Loading",
				"Please wait while we make your graphs.", true, null);
		webview = (WebView) findViewById(R.id.webView);
		custom_graph = (RelativeLayout)findViewById(R.id.custom_graph);
		globalVariable = (GlobalVariable)this.getApplicationContext();
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.getSettings().setUseWideViewPort(true);
		webview.getSettings().setJavaScriptEnabled(true);
		
		
		
		if(getIntent().hasExtra("url")&& getIntent().hasExtra("Webview"))
		{
			url = getIntent().getStringExtra("url");
			webview.setVisibility(View.VISIBLE);
			webview.setWebViewClient(new myWebClient());
			 webview.loadUrl(url);
		}
		else
		{
			webview.setVisibility(View.GONE);
			custom_graph.setVisibility(View.VISIBLE);
			changeStartBtn = (Button)findViewById(R.id.changeStartDate);
			changeEndDate = (Button) findViewById(R.id.changeEndDate);
			startsDate = (TextView) findViewById(R.id.startsDate);
			endsDate = (TextView) findViewById(R.id.endsDate);
			final Calendar c = Calendar.getInstance();
	        year  = c.get(Calendar.YEAR);
	        month = c.get(Calendar.MONTH);
	        day   = c.get(Calendar.DAY_OF_MONTH);
			
		}
       
	}
	
	
	 @Override
	    protected Dialog onCreateDialog(int id) {
	        switch (id) {
	        case START_DATE_PICKER_ID:
	             
	            // open datepicker dialog. 
	            // set date picker for current date 
	            // add pickerListener listner to date picker
	            return new DatePickerDialog(this, startDPListener, year, month,day);
	        case END_DATE_PICKER_ID:
	        	return new DatePickerDialog(this, endDPListener, year, month,day);
	        }
	        return null;
	    }
	
	 
	 private DatePickerDialog.OnDateSetListener startDPListener = new DatePickerDialog.OnDateSetListener() {
		 
	        // when dialog box is closed, below method will be called.
	        @Override
	        public void onDateSet(DatePicker view, int selectedYear,
	                int selectedMonth, int selectedDay) {
	             
	            year  = selectedYear;
	            month = selectedMonth;
	            day   = selectedDay;
	 
	            // Show selected date 
	            startsDate.setText(new StringBuilder().append(month + 1)
	                    .append("/").append(day).append("/").append(year)
	                    .append(" "));
	     
	           }
	        };
	
	        private DatePickerDialog.OnDateSetListener endDPListener = new DatePickerDialog.OnDateSetListener() {
	   		 
		        // when dialog box is closed, below method will be called.
		        @Override
		        public void onDateSet(DatePicker view, int selectedYear,
		                int selectedMonth, int selectedDay) {
		             
		            year  = selectedYear;
		            month = selectedMonth;
		            day   = selectedDay;
		 
		            // Show selected date 
		            endsDate.setText(new StringBuilder().append(month + 1)
		                    .append("/").append(day).append("/").append(year)
		                    .append(" "));
		     
		           }
		        };
	
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
	
	
	public void showDialog2(View v)
	{
		showDialog(END_DATE_PICKER_ID);
	}
	
	
	public void showDialog1(View v)
	{
		showDialog(START_DATE_PICKER_ID);
	}
	
	
	public void go(View v)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String startDate = startsDate.getText().toString();
		String endDate = endsDate.getText().toString();
		try{
		Date d1 = formatter.parse(startDate);
		Date d2 = formatter.parse(endDate);
		long diff = Math.abs(d2.getTime() - d1.getTime());
        long diffDays = diff / (24 * 60 * 60 * 1000);
        
		if(d2.after(d1) || d2.equals(d1) )
		{
			if(diffDays < 31)
			{
				
				custom_graph.setVisibility(View.GONE);
				webview.setVisibility(View.VISIBLE);
				url = " http://dcounter.herokuapp.com/count_totals_histories/custom_graph_with_webview?from="+startDate+"&to="+endDate+"&business_id="+globalVariable.getSelectedBusiness().getId().get$oid();
				webview.setWebViewClient(new myWebClient());
				System.out.println(">>>>>new url:"+url);
				webview.loadUrl(url);
			}
			else
			{
				alertDialogBuilder = createDialog
						.createAlertDialog(
								"Error",
								"Date Range can not Exceed 31 days",
								false);
				alertDialogBuilder.setPositiveButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						});
				alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		}else
		{
			alertDialogBuilder = createDialog
					.createAlertDialog(
							"Error",
							"End Date must be after Start Date",
							false);
			alertDialogBuilder.setPositiveButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					});
			alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}
		
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	

}
