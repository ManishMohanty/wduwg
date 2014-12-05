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
    
    private final static String[] mLabels = { "03", "04", "05","06", "07", "08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","01", "02"};
//	Integer womenValues[] = new Integer[]{25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25};
//	Integer menValues[] = new Integer[]{17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17};
    Integer[] womenValues;
    Integer[] menValues;
    ShinobiChart shinobiChart;
    
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
		
		chart = (ChartFragment) getFragmentManager().findFragmentById(R.id.chart);
		shinobiChart = chart.getShinobiChart();
        shinobiChart.setLicenseKey("JCgqbYIgJvuFuvcMjAxNDEyMjhiYW5raW1AbW90aWZ3b3Jrcy5jb20=OQAe04eNlju90AAKRomRnTx9FzI++4G0iIMuJphAiK0mKpJ4UpOBwPdltaVxNFwB9sdJe/7Q1zh3dHOuwf8NTa5QjnBxR9+21JoglPFZ2gSd3GiHiP3BQjCMQG1nDeCc7ikkH3WTgbSqdDVm+UuDIuB3exmA=BQxSUisl3BaWf/7myRmmlIjRnMU2cA7q+/03ZX9wdj30RzapYANf51ee3Pi8m2rVW6aD7t6Hi4Qy5vv9xpaQYXF5T7XzsafhzS3hbBokp36BoJZg8IrceBj742nQajYyV7trx5GIw9jy/V6r0bvctKYwTim7Kzq+YPWGMtqtQoU=PFJTQUtleVZhbHVlPjxNb2R1bHVzPnh6YlRrc2dYWWJvQUh5VGR6dkNzQXUrUVAxQnM5b2VrZUxxZVdacnRFbUx3OHZlWStBK3pteXg4NGpJbFkzT2hGdlNYbHZDSjlKVGZQTTF4S2ZweWZBVXBGeXgxRnVBMThOcDNETUxXR1JJbTJ6WXA3a1YyMEdYZGU3RnJyTHZjdGhIbW1BZ21PTTdwMFBsNWlSKzNVMDg5M1N4b2hCZlJ5RHdEeE9vdDNlMD08L01vZHVsdXM+PEV4cG9uZW50PkFRQUI8L0V4cG9uZW50PjwvUlNBS2V5VmFsdWU+");
		
		if(getIntent().hasExtra("url")&& getIntent().hasExtra("Webview"))
		{
			url = getIntent().getStringExtra("url");
			webview.setVisibility(View.VISIBLE);
			webview.setWebViewClient(new myWebClient());
			 webview.loadUrl(url);
		}else if(getIntent().hasExtra("url"))
		{
			url = getIntent().getStringExtra("url");
			webview.setVisibility(View.GONE);
			
	        LoadStringsAsync2 asyncTask = new LoadStringsAsync2();
			asyncTask.execute();
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
				url = "http://dcounter.herokuapp.com/counters/custom_graph?from="+startDate+"&to="+endDate+"&business_id="+globalVariable.getSelectedBusiness().getId().get$oid();
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
	
	
	public class LoadStringsAsync2 extends AsyncTask<Void, Void, Void> {
		public LoadStringsAsync2() {

		}
		@Override
		protected Void doInBackground(Void... params) {
			try {
				JSONParser jsonparser = new JSONParser(GraphActivity.this);
				List<NameValuePair> params1 = new ArrayList<NameValuePair>();
				params1.add(new BasicNameValuePair("business_id",globalVariable.getSelectedBusiness().getId().get$oid() ));
				System.out.println(">>>>>> url:"+url);
				JSONArray specialsjsonarr = jsonparser.getJSONArrayFromUrlAfterHttpGet(url,params1);
                if(specialsjsonarr.length() > 0)
                {
                	menValues = new Integer[specialsjsonarr.length()];
                	womenValues = new Integer[specialsjsonarr.length()];
                	System.out.println(">>>>>> graph data response length:"+specialsjsonarr.length());
                	for(int i=0;i<specialsjsonarr.length();i++)
                	{
                		
                			if(specialsjsonarr.getJSONObject(i).getString("menin").equalsIgnoreCase("null") )
                			{
                				System.out.println(">>>>>> null hai");
                				menValues[i] = 0;
                			}
                			else
                			{
                				menValues[i] = Integer.parseInt(specialsjsonarr.getJSONObject(i).getString("menin"));
                			}
                			if(specialsjsonarr.getJSONObject(i).getString("menout").equalsIgnoreCase("null") )
                			{
                				System.out.println(">>>>>> null hai");
                				menValues[i] = 0;
                			}
                			else
                			{
                				menValues[i] -= Integer.parseInt(specialsjsonarr.getJSONObject(i).getString("menout"));
                			}
                			if(specialsjsonarr.getJSONObject(i).getString("womenin").equalsIgnoreCase("null") )
                			{
                				System.out.println(">>>>>> null hai");
                				womenValues[i] = 0;
                			}
                			else
                			{
                				womenValues[i] = Integer.parseInt(specialsjsonarr.getJSONObject(i).getString("womenin"));
                			}
                			if(specialsjsonarr.getJSONObject(i).getString("womenout").equalsIgnoreCase("null") )
                			{
                				System.out.println(">>>>>> null hai");
                				womenValues[i] = 0;
                			}
                			else
                			{
                				womenValues[i] -= Integer.parseInt(specialsjsonarr.getJSONObject(i).getString("womenout"));
                			}
                	}
                }
			
			
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void arg) {
			System.out.println(">>>>>>> inside counter postexecute");
			progressDialog.dismiss();
			System.out.println(">>>>>>> progress dialog dismiss");
			shinobiChart.getCrosshair().enableTooltip(true);
			CategoryAxis categoryAxis = new CategoryAxis();
			categoryAxis.getStyle().getGridlineStyle().setGridlinesShown(true);
			categoryAxis.setRangePaddingHigh(0.5);
			categoryAxis.setRangePaddingLow(0.5);
	        shinobiChart.setXAxis(categoryAxis);
	        shinobiChart.getStyle().setPlotAreaBackgroundColor(Color.WHITE);
	        AnnotationsManager annotationsManager = shinobiChart.getAnnotationsManager();
	        
	        
	        shinobiChart.setOnCrosshairActivationStateChangedListener(new ShinobiChart.OnCrosshairActivationStateChangedListener() {
				
				@Override
				public void onCrosshairActivationStateChanged(ShinobiChart chart) {
					// TODO Auto-generated method stub
					
				}
			});
	        shinobiChart.setOnTrackingInfoChangedForTooltipListener (new ShinobiChart.OnTrackingInfoChangedForTooltipListener() {

				@Override
				public void onTrackingInfoChanged(Tooltip arg0,
						DataPoint<?, ?> arg1, DataPoint<?, ?> arg2,
						DataPoint<?, ?> arg3) {
					// TODO Auto-generated method stub
					arg0.setCenter(arg3);
					DefaultTooltipView tooltipView = (DefaultTooltipView) arg0.getView();
					CartesianSeries<?> trackedSeries = arg0.getTrackedSeries();
					NumberAxis yAxis = (NumberAxis)trackedSeries.getYAxis();
					tooltipView.setText("men:"+yAxis.getFormattedString((Double) arg3.getY()));
				} } );
	        NumberAxis yAxis = new NumberAxis() ;
//	        yAxis.setDefaultRange(new NumberRange(min, max));
	        yAxis.getStyle().getGridlineStyle().setGridlinesShown(true);
	        shinobiChart.setYAxis(yAxis);
	        SimpleDataAdapter<String, Integer> dataAdapter1 = new SimpleDataAdapter<String, Integer>();
	        SimpleDataAdapter<String, Integer> dataAdapter2 = new SimpleDataAdapter<String, Integer>();
	        SimpleDataAdapter<String, Integer> dataAdapter3 = new SimpleDataAdapter<String, Integer>();
	        
	        
	        for(int i=0; i<menValues.length;i++)
	        {
	        	dataAdapter1.add(new DataPoint<String, Integer>(mLabels[i], menValues[i]));
	        	dataAdapter2.add(new DataPoint<String, Integer>(mLabels[i], womenValues[i]));
	        	dataAdapter3.add(new DataPoint<String, Integer>(mLabels[i], (menValues[i]+womenValues[i])));
	        	AnnotationStyle annotationstyleForMen = annotationsManager.addTextAnnotation(""+menValues[i], i, dataAdapter1.get(i).getY()/2, shinobiChart.getXAxis(), shinobiChart.getYAxis()).getStyle();
	        	annotationstyleForMen.setBackgroundColor(Color.TRANSPARENT);
	        	annotationstyleForMen.setTextColor(Color.WHITE);
	        	AnnotationStyle annotationstyleForWomen = annotationsManager.addTextAnnotation(""+womenValues[i], i, (dataAdapter1.get(i).getY()+dataAdapter2.get(i).getY() / 2), shinobiChart.getXAxis(), shinobiChart.getYAxis()).getStyle();
	        	annotationstyleForWomen.setBackgroundColor(Color.TRANSPARENT);
	        	annotationstyleForWomen.setTextColor(Color.WHITE);
	        	AnnotationStyle annotationstyleForTotal = annotationsManager.addTextAnnotation(""+(menValues[i]+womenValues[i]), i, dataAdapter1.get(i).getY()+dataAdapter2.get(i).getY() + (dataAdapter3.get(i).getY()/2), shinobiChart.getXAxis(), shinobiChart.getYAxis()).getStyle();
	        	annotationstyleForTotal.setBackgroundColor(Color.TRANSPARENT);
	        	annotationstyleForTotal.setTextColor(Color.WHITE);
	        }
	        
	        if(getIntent().hasExtra("case") && getIntent().getIntExtra("case", 0) == 5)
	        {
	        	shinobiChart.setTitle("Today");
	        	LineSeries series_shinobiLineSeries1 = new LineSeries();
		        series_shinobiLineSeries1.setCrosshairEnabled(true);
		        series_shinobiLineSeries1.setTitle("Men");
		        series_shinobiLineSeries1.setDataAdapter(dataAdapter1);
		        
		        shinobiChart.addSeries(series_shinobiLineSeries1);
	
		        LineSeries series_shinobiLineSeries2 = new LineSeries();
		        series_shinobiLineSeries2.setTitle("Women");
		        series_shinobiLineSeries2.setDataAdapter(dataAdapter2);
		        shinobiChart.addSeries(series_shinobiLineSeries2);
		        
		        LineSeries series_shinobiLineSeries3 = new LineSeries();
		        series_shinobiLineSeries3.setTitle("Total");
		        series_shinobiLineSeries3.setDataAdapter(dataAdapter3);
		        shinobiChart.addSeries(series_shinobiLineSeries3);
		        
		        
		        LineSeriesStyle style1 = series_shinobiLineSeries1.getStyle();
		        style1.setFillStyle(FillStyle.GRADIENT);
		        PointStyle pointStyle1 = new PointStyle();
		        pointStyle1.setPointsShown(true);
		        pointStyle1.setColor(Color.parseColor("#3366cc"));
		        pointStyle1.setInnerColor(Color.WHITE);
		        style1.setPointStyle(pointStyle1);
		        style1.setLineColor(Color.parseColor("#3366cc"));
		        style1.setLineWidth(2.0f);
		        
		       
		        
		        
		        LineSeriesStyle style2 = series_shinobiLineSeries2.getStyle();
		        style2.setFillStyle(FillStyle.NONE);
		        style2.setLineWidth(2.0f);
		        style2.setLineColor(Color.parseColor("#109618"));
		        PointStyle pointStyle2 = new PointStyle();
		        pointStyle2.setPointsShown(true);
		        pointStyle2.setColor(Color.parseColor("#109618"));
		        style2.setPointStyle(pointStyle2);
		        
		        LineSeriesStyle style3 = series_shinobiLineSeries3.getStyle();
		        style3.setFillStyle(FillStyle.NONE);
		        style3.setLineWidth(2.0f);
		        style3.setLineColor(Color.parseColor("#dc3912"));
		        PointStyle pointStyle3 = new PointStyle();
		        pointStyle3.setPointsShown(true);
		        pointStyle3.setColor(Color.parseColor("#dc3912"));
		        style3.setPointStyle(pointStyle3);
	        }
	        else{        
	        	switch(getIntent().getIntExtra("case", 0))
	        	{
	        	case 6: 
	        		 shinobiChart.setTitle("Day of Week");
	        		 break;
	        	case 7: 
	        		 shinobiChart.setTitle("Last Week");
	        		 break;
	        	case 8:
	        		 shinobiChart.setTitle("Last Month");
	        		 break;
	        	}
	        ColumnSeries barSeries1 = new ColumnSeries();
	        barSeries1.setDataAdapter(dataAdapter1);
	        barSeries1.setTitle("Men");
	        barSeries1.setShownInLegend(true);
	       
	        ColumnSeries barSeries2 = new ColumnSeries();
	        barSeries2.setDataAdapter(dataAdapter2);
	        barSeries2.setTitle("Women");
	        barSeries2.setShownInLegend(true);
	        
	        ColumnSeries barSeries3 = new ColumnSeries();
	        barSeries3.setDataAdapter(dataAdapter3);
	        barSeries3.setTitle("Total");
	        barSeries3.setShownInLegend(true);
	        
	        barSeries1.setStackId(1);
	        barSeries2.setStackId(1);
	        barSeries3.setStackId(1);
	        
	        shinobiChart.addSeries(barSeries1);
	        shinobiChart.addSeries(barSeries2);
	        shinobiChart.addSeries(barSeries3);
	        
	        ColumnSeriesStyle style1 = barSeries1.getStyle();
	        style1.setAreaColor(Color.parseColor("#3366cc"));
	        style1.setFillStyle(FillStyle.FLAT);
	        
	        ColumnSeriesStyle style2 = barSeries2.getStyle();
	        style2.setFillStyle(FillStyle.FLAT);
	        style2.setAreaColor(Color.parseColor("#109618"));
	        
	        ColumnSeriesStyle style3 = barSeries3.getStyle();
	        style3.setAreaColor(Color.parseColor("#dc3912"));
	        style3.setFillStyle(FillStyle.FLAT);
	        }
	        
	        Legend legend = shinobiChart.getLegend();
	        legend.setVisibility(View.VISIBLE);
	        legend.setPosition(Legend.Position.BOTTOM_CENTER);
	        legend.setMinimumHeight(5);
	        legend.setMaxSeriesPerRow(3);
//	        legend.setPlacement(Legend.Placement.OUTSIDE_PLOT_AREA);
	        
	        shinobiChart.redrawChart();
		}

	}

}
