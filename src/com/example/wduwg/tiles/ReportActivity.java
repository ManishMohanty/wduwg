package com.example.wduwg.tiles;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mw.wduwg.services.GlobalVariable;

public class ReportActivity extends Activity {

	ListView listview;
	ArrayAdapter adapter;
	GlobalVariable globalVariable;
	public static String[] listitems = new String[]{"Today", "Day of Week", "Last Week", "Last Month","Custom Graph", "Today *", "Day of Week *", "Last Week *", "Last Month *"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		globalVariable = (GlobalVariable)this.getApplicationContext();
		listview = (ListView)findViewById(R.id.listview);
		adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listitems);
		
		
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

		      @Override
		      public void onItemClick(AdapterView<?> parent, final View view,
		          int position, long id) {
		        final String item = (String) parent.getItemAtPosition(position);
//		        Toast.makeText(ReportActivity.this, item+" clicked", Toast.LENGTH_SHORT).show();
		        
				
				Intent intent = new Intent(ReportActivity.this,GraphActivity.class);
				
				String url;
				switch(position)
				{
				case 0 :
					    url = "http://dcounter.herokuapp.com/counters/daily_graph?business_id=" + globalVariable.getSelectedBusiness().getId().get$oid();
//					    webView.loadUrl(url);
					    intent.putExtra("url", url);
					    intent.putExtra("Webview", true);
					    startActivity(intent);
					    break;
				case 1: 
					   url = "http://dcounter.herokuapp.com/counters/day_of_week?business_id=" + globalVariable.getSelectedBusiness().getId().get$oid();
//					    webView.loadUrl(url);
					    intent.putExtra("url", url);
					    intent.putExtra("Webview", true);
					    startActivity(intent);
					    break;
				case 2:
					  url = "http://dcounter.herokuapp.com/counters/weekly_graph?business_id=" + globalVariable.getSelectedBusiness().getId().get$oid();
//					    webView.loadUrl(url);
					     intent.putExtra("url", url);
					     intent.putExtra("Webview", true);
					    startActivity(intent);
					    break;
				case 3: 
					   url = "http://dcounter.herokuapp.com/counters/monthly_graph?business_id=" + globalVariable.getSelectedBusiness().getId().get$oid();
//					    webView.loadUrl(url);
					    intent.putExtra("url", url);
					    intent.putExtra("Webview", true);
					    startActivity(intent);
					    break;
				case 4: 
					   startActivity(intent);
					   break;
				case 5:
					 url = "http://dcounter.herokuapp.com/count_totals_histories/daily_graph.json";
					 intent.putExtra("url", url);
					 intent.putExtra("case", 5);
					 startActivity(intent);
					 break;
				
				case 6:
					 url = "http://dcounter.herokuapp.com/count_totals_histories/day_of_week.json";
					 intent.putExtra("url", url);
					 intent.putExtra("case", 6);
					 startActivity(intent);
					 break;
				case 7:
					 url ="http://dcounter.herokuapp.com/count_totals_histories/weekly_graph.json";
					 intent.putExtra("url", url);
					 intent.putExtra("case", 7);
					 startActivity(intent);
				     break;
				     
				case 8:
					url = "http://dcounter.herokuapp.com/count_totals_histories/monthly_garph.json";
					intent.putExtra("url", url);
					intent.putExtra("case", 8);
					startActivity(intent);
					break;
					
				}
				
				
		      }

		    });
	}
	
	

}
