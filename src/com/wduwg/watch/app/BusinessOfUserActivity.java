package com.wduwg.watch.app;

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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mw.wduwg.model.Business;
import com.mw.wduwg.model.BusinessFBPage;
import com.mw.wduwg.services.CreateDialog;
import com.mw.wduwg.services.GlobalVariable;

public class BusinessOfUserActivity extends Activity{
    Bitmap bitmap;
    byte[] b;
	ListView listView;
	GlobalVariable globalVariable;
	AlertDialog.Builder alertDialogBuilder;
	CreateDialog createDialog;
	AlertDialog alertDialog;
	ProgressDialog progressDialog;
	JSONObject jsonFromServer;
	Business selectedBusiness;
	TextView messageForUser;
	Typeface typeface , typefaceLight;
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_businesses_of_user);
		typefaceLight = Typeface.createFromAsset(getAssets(), "Fonts/OpenSans-Light.ttf");
		typeface = Typeface.createFromAsset(getAssets(), "Fonts/OpenSans-Bold.ttf");
		
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
		ListView listView = (ListView) findViewById(R.id.listView);
		CustomAdapter adapter = new CustomAdapter(
				BusinessOfUserActivity.this, globalVariable.getCustomer().getBusinesses());
		listView.setAdapter(adapter);
	     listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				final int positionFinal = position;
				final View viewFinal = (View)view;
								Business business = globalVariable.getCustomer().getBusinesses().get(positionFinal);
								int fbpagesize = globalVariable.getCustomer().getPages().size();
								for(int i=0;i<fbpagesize;i++)
								{
									if(globalVariable.getCustomer().getPages().get(i).getId().equals(business.getFace_book_page()))
									{
										globalVariable.setSelectedFBPage(globalVariable.getCustomer().getPages().get(i));
									}
								}
								if(globalVariable.getSelectedBusiness()!=null && globalVariable.getSelectedBusiness().getId().get$oid() != business.getId().get$oid())
								{
									globalVariable.setMenIn(0);
									globalVariable.setMenOut(0);
									globalVariable.setWomenIn(0);
									globalVariable.setWomenOut(0);
								}
								globalVariable.setSelectedBusiness(null);
								globalVariable.setSelectedBusiness(business);
								System.out.println(">>>>>>> Selected business's facebook page id:"+business.getFace_book_page());
								globalVariable.saveSharedPreferences();
										Gson gson = new Gson();
										String json = gson.toJson(business);
										System.out.println(">>>>>>> business existing"
												+ json);
										final ImageView image_view = (ImageView) viewFinal
												.findViewById(R.id.icon);
										final BitmapDrawable bitmapDrawable = (BitmapDrawable) image_view
												.getDrawable();
										 bitmap = bitmapDrawable.getBitmap();
										ByteArrayOutputStream bs = new ByteArrayOutputStream();
										bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
										
										Intent nextIntent = new Intent(BusinessOfUserActivity.this,CountActivity.class);
										startActivity(nextIntent);
										overridePendingTransition(R.anim.anim_out,
												R.anim.anim_in);
									}
		});
	     
	     
	     listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				System.out.println(">>>>>>> long click");
				// TODO Auto-generated method stub
				alertDialogBuilder = createDialog.createAlertDialog(
						"Logout", "Do you wish to logout", false);
				alertDialogBuilder.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
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
								dialog.dismiss();
								Intent nextIntent = new Intent(BusinessOfUserActivity.this,SpalshFirstActivity.class);
								nextIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(nextIntent);
							}
						});
				alertDialogBuilder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								alertDialog.dismiss();
							}
						});
				alertDialog = alertDialogBuilder.create();
				alertDialog.show();
				return false;
			}
		});
	}
	

}
