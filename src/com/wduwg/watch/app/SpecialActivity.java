package com.wduwg.watch.app;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.apphance.android.Log;
import com.google.gson.JsonArray;
import com.mw.wduwg.adapter.SpecialApater;
import com.mw.wduwg.model.Business;
import com.mw.wduwg.model.Special;
import com.mw.wduwg.services.CreateDialog;
import com.mw.wduwg.services.GlobalVariable;
import com.mw.wduwg.services.JSONParser;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class SpecialActivity extends Activity {
	ListView specialLV;
	
	CreateDialog createDialog;
	ProgressDialog progressDialog;
	GlobalVariable globalVariable;
	View customActionBar;
	LayoutInflater inflater;
	ActionBar actionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_special);
		inflater =(LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		actionBar = getActionBar();
		
		globalVariable = (GlobalVariable)getApplicationContext();
		specialLV = (ListView)findViewById(R.id.specialList);
		createDialog = new CreateDialog(this);
		progressDialog = createDialog.createProgressDialog("Loading", "wait for a while", true, null);
		progressDialog.show();
		LoadStringsAsync asyncTask = new LoadStringsAsync();
		asyncTask.execute();
		
	}
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		customActionBar = inflater.inflate(R.layout.custom_action_bar, null);
		TextView title = (TextView)customActionBar.findViewById(R.id.action_bar_TV);
		title.setText("Specials");
		title.setTextSize(19);
		Typeface font = Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Bold.ttf");
		title.setTypeface(font);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(customActionBar);
	}



	public class LoadStringsAsync extends AsyncTask<Void, Void, List<Special>> {

		// new thread for imagedownloading res
		Bitmap bitmap;
		JSONArray photos, array;
		JSONObject photo;
		String name, formatedAddress;

		List<Special> specialList = new ArrayList<Special>();

		public LoadStringsAsync() {

		}

		@Override
		protected List<Special> doInBackground(Void... arg0) {
			try {
				JSONParser jsonparser = new JSONParser(SpecialActivity.this);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("business_id",globalVariable.getSelectedBusiness().getId().get$oid() ));
				JSONArray specialsjsonarr = jsonparser.getJSONArrayFromUrlAfterHttpGet("http://dcounter.herokuapp.com/specials.json",params);
				if(specialsjsonarr.length()>0)
				{
					for(int i = 0; i< specialsjsonarr.length(); i++)
					{
						Special special = new Special();
						JSONObject jsonobject = specialsjsonarr.getJSONObject(i);
	//					specialList.add(jsonobject.getString("name"));
						special.setName(jsonobject.getString("name"));
						String starts_from = jsonobject.getString("start_date_time").replace('T', ',').substring(0, (jsonobject.getString("start_date_time").length()-13));
						String valid_upto =  jsonobject.getString("end_date_time").replace('T', ',').substring(0, jsonobject.getString("end_date_time").length()-13);
						special.setDescription("Starts from- "+starts_from + "\nValid upto- " + valid_upto);
						special.setImageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSg92ThBRn7ux2rLEWxZUIKLK-rmMbBBgr6x9ugUZsYUocytf4z");
						specialList.add(special);
					}
				}
				
			} catch (Exception e) {
				Log.d("Response========", "inside catch");
				e.printStackTrace();
			}
			return specialList;
		}

		@Override
		protected void onPostExecute(final List<Special> specials) {
			progressDialog.dismiss();
			if(specials != null && specials.size() > 0)
			{
            System.out.print(">>>>>>> list-size:"+specials.size()+ "===========data"+specials.get(0).getName());
            for(int i=0;i<specials.size();i++)
            	System.out.println(">>>>>>> "+specials.get(i).getName());
			globalVariable.getSelectedBusiness().setSpecials(specials);
            SpecialApater adapter = new SpecialApater(SpecialActivity.this, specials);
            specialLV.setAdapter(adapter);
			}
		}
	}

	
	public void newSpecial(View v)
	{
		Intent intent = new Intent(this,AddSpecialActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.anim_out, R.anim.anim_in);
	}
	
	public void onDone(View v)
	{
		Intent intent = new Intent(this,BusinessHomePageActivity.class);
		intent.putExtra("isFromMain", true);
		startActivity(intent);
		overridePendingTransition(R.anim.anim_out, R.anim.anim_in);
	}
	
}
