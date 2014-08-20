package com.example.wduwg.tiles;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wduwg.tiles.R;
import com.google.gson.Gson;
import com.mw.wduwg.model.Business;
import com.mw.wduwg.model.BusinessFBPage;
import com.mw.wduwg.services.CreateDialog;
import com.mw.wduwg.services.GlobalVariable;

public class BusinessOfUserActivity extends Activity{
    Bitmap bitmap;
    byte[] b;
	ListView listView;
	GridView gridView ;
	List<String> Pagelist;
	GlobalVariable globalVariable;
	AlertDialog.Builder alertDialogBuilder;
	CreateDialog createDialog;
	AlertDialog alertDialog;
	BusinessFBPage fbPage;
	ProgressDialog progressDialog;
	JSONObject jsonFromServer;
	Business selectedBusiness;
	Typeface typeface, typefaceLight;
	TextView messageForUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_businesses_of_user);
		typeface = Typeface.createFromAsset(getAssets(), "Fonts/OpenSans-Bold.ttf");
		typefaceLight= Typeface.createFromAsset(getAssets(), "Fonts/OpenSans-Light.ttf");
		int titleId = getResources().getIdentifier("action_bar_title", "id",
				"android");
		TextView yourTextView = (TextView) findViewById(titleId);
		yourTextView.setTextSize(19);
		yourTextView.setTextColor(Color.parseColor("#016AB2"));
		yourTextView.setTypeface(typeface);
		globalVariable = (GlobalVariable)getApplicationContext();
		
		 LayoutInflater inflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
	       View v= (View) inflater.inflate(R.layout.custom_action_bar, null);
	       TextView title = (TextView)v.findViewById(R.id.action_bar_TV);
	       title.setTypeface(typeface);
	       title.setTextSize(19);
	       title.setText("Welcome "+globalVariable.getCustomer().getName());
	       ImageButton ib =(ImageButton) v.findViewById(R.id.doneButton);
	       ib.setVisibility(View.GONE);
	       ActionBar ab = getActionBar();
	       ab.setDisplayShowCustomEnabled(true);
	       ab.setCustomView(v);
	       messageForUser = (TextView)findViewById(R.id.messageForUser);
	       messageForUser.setTypeface(typefaceLight);
		
		createDialog = new CreateDialog(this);
		gridView = (GridView)findViewById(R.id.gridView1);
		Pagelist = new ArrayList<String>();
		for (int i = 0; i < globalVariable.getCustomer().getPages().size(); i++) {
			Pagelist.add(globalVariable.getCustomer().getPages().get(i)
					.getName());
		}
		eventListener();
}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.overflow_options_menu, menu);
		CharSequence rawTitle = "Logout";
		menu.findItem(R.id.menu_logout).setTitleCondensed(rawTitle);

		SpannableString logoutstr = new SpannableString(rawTitle);
		logoutstr.setSpan(typeface, 0, logoutstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		menu.findItem(R.id.menu_logout).setTitle(logoutstr);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuItem logouItem = menu.findItem(R.id.menu_logout);
		MenuItem delinkItem = menu.findItem(R.id.menu_delink);
		MenuItem deleteItem = menu.findItem(R.id.menu_delete);
		MenuItem settingsItem = menu.findItem(R.id.menu_settings);
		if(globalVariable.getFb_access_token() !=null)
		{
			logouItem.setEnabled(true);
		}else
		{
			logouItem.setEnabled(false);
		}
		if(globalVariable.getSelectedBusiness() != null)
		{
			delinkItem.setEnabled(true);
			deleteItem.setEnabled(true);
			settingsItem.setEnabled(true);
		}else
		{
			delinkItem.setEnabled(false);
			deleteItem.setEnabled(false);
			settingsItem.setEnabled(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_logout:
			if (LoginFacebookActivity.timer != null)
				LoginFacebookActivity.timer.cancel();
			globalVariable.getCustomer().setPages(null);
			globalVariable.setFb_access_expire(0);
			globalVariable.setFb_access_token(null);
			globalVariable.setSelectedBusiness(null);
			globalVariable.setSelectedEvent(null);
			globalVariable.setMenIn(0);
			globalVariable.setMenOut(0);
			globalVariable.setWomenIn(0);
			globalVariable.setWomenOut(0);
			globalVariable.saveSharedPreferences();
			globalVariable.saveSharedPreferences();
			Toast.makeText(this, "Logged out from FB.", Toast.LENGTH_SHORT).show();
			Intent nextIntent = new Intent(BusinessOfUserActivity.this,SpalshFirstActivity.class);
			nextIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(nextIntent);
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	
	private void eventListener()
	{
		
		List<Business> businessList= new ArrayList<Business>();
		if(globalVariable.getCustomer().getBusinesses().size()>0 && !globalVariable.getCustomer().getBusinesses().get(globalVariable.getCustomer().getBusinesses().size()-1).getName().equalsIgnoreCase("Add Business"))
		{
		businessList = globalVariable.getCustomer().getBusinesses();
		System.out.println(">>>>>>>before new business list size :"+businessList.size());
		Business newBusiness = new Business();
		newBusiness.setName("Add Business");
		newBusiness.setImageUrl("http://us.123rf.com/400wm/400/400/nicemonkey/nicemonkey0703/nicemonkey070300014/782266-8-silhouette-business-people-in-line-in-black-and-white.jpg");
		newBusiness.setAddress("");
		businessList.add(newBusiness);
		}else if(globalVariable.getCustomer().getBusinesses().size() < 1)
		{
			 Business business = new Business();
			 business.setName("Add Business");
			 business.setAddress("");
			 business.setImageUrl("http://us.123rf.com/400wm/400/400/nicemonkey/nicemonkey0703/nicemonkey070300014/782266-8-silhouette-business-people-in-line-in-black-and-white.jpg");
		     businessList.add(business);
		}else
		{
			businessList = globalVariable.getCustomer().getBusinesses();
		}
		System.out.println(">>>>>>>After new business list size :"+businessList.size());
		GridAdapter adapter = new GridAdapter(BusinessOfUserActivity.this, businessList);
		gridView.setAdapter(adapter);
		
		
		// listener for gridView
				gridView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						final int positionFinal = position;
//						int fbpageSize = globalVariable.getCustomer().getPages().size();
//						Business business = globalVariable.getCustomer().getBusinesses().get(positionFinal);
//
//						for(int i=0;i<fbpageSize;i++)
//						{
//							if(globalVariable.getCustomer().getPages().get(i).getId().equals(business.getFace_book_page()))
//							{
//								globalVariable.setSelectedFBPage(globalVariable.getCustomer().getPages().get(i));
//							}
//						}
						
						
						if(globalVariable.getCustomer().getBusinesses().size() == 0 || position == globalVariable.getCustomer().getBusinesses().size() - 1)
						{
							System.out.println(">>>>>>> sending to ident");
					        if(globalVariable.getCustomer().getBusinesses().size() > 0)
							globalVariable.getCustomer().getBusinesses().remove(position);
							Intent intent = new Intent(BusinessOfUserActivity.this,IdentifyingBusinessActivity.class);
							startActivity(intent);
						}else{
						final View viewFinal = (View)view;
										Business business = globalVariable.getCustomer().getBusinesses().get(positionFinal);
										if(globalVariable.getSelectedBusiness() != null && globalVariable.getSelectedBusiness().getId().get$oid() != business.getId().get$oid())
										{
											globalVariable.setMenIn(0);
											globalVariable.setMenOut(0);
											globalVariable.setWomenIn(0);
											globalVariable.setWomenOut(0);
										}
										globalVariable.getCustomer().getBusinesses().remove(globalVariable.getCustomer().getBusinesses().size()-1);
										globalVariable.setSelectedBusiness(null);
										globalVariable.setSelectedBusiness(business);
										globalVariable.saveSharedPreferences();
												Gson gson = new Gson();
												String json = gson.toJson(business);
												System.out.println(">>>>>>> business existing"
														+ json);
												Intent nextIntent = new Intent(BusinessOfUserActivity.this,BusinessDashboardActivity.class);
												System.out.println(">>>>>>> start new Activity");
												startActivity(nextIntent);
												overridePendingTransition(R.anim.anim_out,
														R.anim.anim_in);
												
					} 
						}// end else
					
				});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		eventListener();
		super.onResume();
	}


}