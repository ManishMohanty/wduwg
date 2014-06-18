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
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
	Typeface typeface;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_businesses_of_user);
		typeface = Typeface.createFromAsset(getAssets(), "Fonts/OpenSans-Bold.ttf");
		int titleId = getResources().getIdentifier("action_bar_title", "id",
				"android");
		TextView yourTextView = (TextView) findViewById(titleId);
		yourTextView.setTextSize(19);
		yourTextView.setTextColor(Color.parseColor("#016AB2"));
		yourTextView.setTypeface(typeface);
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
		if(globalVariable.getFb_access_token() !=null)
		{
			logouItem.setEnabled(true);
		}else
		{
			logouItem.setEnabled(false);
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


}