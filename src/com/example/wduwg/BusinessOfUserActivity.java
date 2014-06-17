package com.example.wduwg;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
	List<String> Pagelist;
	GlobalVariable globalVariable;
	AlertDialog.Builder alertDialogBuilder;
	CreateDialog createDialog;
	AlertDialog alertDialog;
	BusinessFBPage fbPage;
	ProgressDialog progressDialog;
	JSONObject jsonFromServer;
	Business selectedBusiness;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_businesses_of_user);
		int titleId = getResources().getIdentifier("action_bar_title", "id",
				"android");
		TextView yourTextView = (TextView) findViewById(titleId);
		yourTextView.setTextSize(19);
		yourTextView.setTextColor(Color.parseColor("#016AB2"));
		yourTextView.setTypeface(Typeface.createFromAsset(getAssets(), "Fonts/OpenSans-Bold.ttf"));
		globalVariable = (GlobalVariable)getApplicationContext();
		createDialog = new CreateDialog(this);
		ListView listView = (ListView) findViewById(R.id.listView);
		CustomAdapter adapter = new CustomAdapter(
				BusinessOfUserActivity.this, globalVariable.getCustomer().getBusinesses());
		listView.setAdapter(adapter);
		Pagelist = new ArrayList<String>();
		
		for (int i = 0; i < globalVariable.getCustomer().getPages().size(); i++) {
			Pagelist.add(globalVariable.getCustomer().getPages().get(i)
					.getName());
		}
	     listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				final int positionFinal = position;
				final View viewFinal = (View)view;
				
				
				String[] items = Pagelist.toArray(new String[Pagelist.size()]);
				alertDialogBuilder = createDialog.createAlertDialog(
						"Select Facebook Page", null, false);
				alertDialogBuilder.setItems(items,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int pos) {
								// TODO Auto-generated method stub
								fbPage = globalVariable.getCustomer().getPages()
										.get(pos);
								globalVariable.setSelectedFBPage(fbPage);
								alertDialog.dismiss();
								globalVariable.setSelectedBusiness(null);
								globalVariable.saveSharedPreferences();
								List<Business> businessList = globalVariable
										.getCustomer().getBusinesses();
								System.out.println(">>>>>>> business list size:"
										+ businessList.size());
								boolean isExist = false;
								for (int i = 0; i < businessList.size(); i++) {
									if (businessList
											.get(i)
											.getName()
											.equalsIgnoreCase(
													businessList.get(positionFinal).getName())
											&& businessList
													.get(i)
													.getAddress()
													.equalsIgnoreCase(
															businessList.get(positionFinal).getAddress())) {
										Business business = businessList.get(i);

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
										globalVariable.setSelectedBusiness(business);
										System.out
												.println(">>>>>>> before navigation to addEvent");
										System.out
												.println(">>>>>>> selectedBusinessId:"
														+ globalVariable
																.getSelectedBusiness()
																.getId().get$oid());
//										isExist = true;
//										Intent nextIntent = new Intent(BusinessOfUserActivity.this,BusinessHomePageActivity.class);
//										nextIntent.putExtra("business_name",
//												businessList.get(positionFinal).getName());
//										nextIntent.putExtra("business_id",
//												businessList.get(positionFinal).getGooglePlaceID());
//										nextIntent.putExtra("complete_address",
//												businessList.get(positionFinal).getAddress()); // new
//										nextIntent.putExtra("complete_result",
//												businessList.get(positionFinal).getGoogleAPIResult());
//										nextIntent.putExtra("byteArray", bs.toByteArray());
										Intent nextIntent = new Intent(BusinessOfUserActivity.this,CountActivity.class);
										startActivity(nextIntent);
										overridePendingTransition(R.anim.anim_out,
												R.anim.anim_in);
									}
								}
							}
						});
				alertDialogBuilder.setPositiveButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								alertDialog.dismiss();
							}
						});
				alertDialog = alertDialogBuilder.create();
				alertDialog.show();
				
			}
		});
	}
	

}