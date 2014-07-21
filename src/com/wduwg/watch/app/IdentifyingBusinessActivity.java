package com.wduwg.watch.app;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.apphance.android.Log;
import com.mw.wduwg.model.Business;
import com.mw.wduwg.model.Customer;
import com.mw.wduwg.services.CreateDialog;
import com.mw.wduwg.services.GlobalVariable;
import com.mw.wduwg.services.JSONParser;

public class IdentifyingBusinessActivity extends Activity {
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		globalVariable.saveSharedPreferences();
	}

	HttpResponse response;
	ListView listview;
	private View mLoadingStatusView;
	// private TextView mLoadingStatusMessageTV;
	private ListView mListView;
	String place, latLongAddress;
	double lat1 = 0.0, lon1 = 0.0;
	Typeface typeface;
	Typeface typefaceBold;
	String deviceId;

	TextView header1TV;
	TextView header2TV;
	TextView header3TV;

	Intent previousIntent;
	Intent nextIntent;

	CreateDialog createDialog;
	ProgressDialog progressDialog;
	AlertDialog.Builder alertDialogBuilder;
	AlertDialog alertDialog;
	
	GlobalVariable globalVariable;
	
	JSONParser jsonParser;
	GPSTracker tracker;

	private void findThings() {
		header1TV = (TextView) findViewById(R.id.header1TV);
		header2TV = (TextView) findViewById(R.id.header2TV);
		header3TV = (TextView) findViewById(R.id.header3TV);
		mLoadingStatusView = findViewById(R.id.loading_status);
		mListView = (ListView) findViewById(R.id.list);
 
	}

	private void initializeThings() {
		previousIntent = getIntent();
		typeface = Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Light.ttf");
		typefaceBold = Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Bold.ttf");
		
		header1TV.setTypeface(typefaceBold);
		header2TV.setTypeface(typefaceBold);
		header3TV.setTypeface(typefaceBold);
		createDialog = new CreateDialog(this);

		deviceId = Secure.getString(this.getContentResolver(),
				Secure.ANDROID_ID);
		globalVariable = (GlobalVariable) getApplicationContext();
		tracker = new GPSTracker(IdentifyingBusinessActivity.this);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_identifying_business);

		findThings();
		initializeThings();

		// action bar font
		int titleId = getResources().getIdentifier("action_bar_title", "id",
				"android");
		TextView actionBarTextView = (TextView) findViewById(titleId);
		actionBarTextView.setTextColor(Color.parseColor("#016AB2"));
		actionBarTextView.setTextSize(19);
		actionBarTextView.setTypeface(typefaceBold);

		
		if (previousIntent.hasExtra("place")) {
			// search by keyword
			place = previousIntent.getStringExtra("place");
			Toast.makeText(this, "Searching for: " + place, Toast.LENGTH_SHORT)
					.show();
			showProgress(true, "Loading ...");
			new LoadStringsAsync(lat1, lon1, this, place).execute();

		} else if (tracker.isGPSEnabled) {
			lat1 = tracker.getLatitude();
			lon1 = tracker.getLongitude();

			latLongAddress = getAddress(lat1, lon1);
			showProgress(true, "You are at \n" + latLongAddress);
			new LoadStringsAsync(lat1, lon1, this, null).execute();
		} else {
			Toast.makeText(IdentifyingBusinessActivity.this, "gps not working",
					Toast.LENGTH_SHORT).show();
		}

		listview = (ListView) findViewById(R.id.list);

	}

	// async task
	public class LoadStringsAsync extends AsyncTask<Void, Void, List<Business>> {

		// new thread for imagedownloading res
		Bitmap bitmap;
		JSONArray photos, array;
		JSONObject photo;
		String name, formatedAddress;

		List<Business> businessList = new ArrayList<Business>();
		double lat, lon;
		String place;
		private IdentifyingBusinessActivity activity;

		public LoadStringsAsync(double lat, double lon,
				IdentifyingBusinessActivity activity, String place) {
			this.lat = lat;
			this.lon = lon;
			this.activity = activity;
			this.place = place;

		}

		@Override
		protected List<Business> doInBackground(Void... arg0) {
			try {
				String new_url;
				if (place == null) {

					new_url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
							+ lat
							+ ","
							+ lon
							+ "&radius=500&sensor=false&key=AIzaSyDWngmH16EcyItOCncqQmyZGNZDA8AFuGs";

					Log.d("url==", new_url);
				} else {

					place = place.replace(" ", "+");
					new_url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query="
							+ place
							+ "+&sensor=false&key=AIzaSyDWngmH16EcyItOCncqQmyZGNZDA8AFuGs";

					Log.d("url==", new_url);
				}

				HttpPost httppost = new HttpPost(new_url);
				HttpClient httpclient = new DefaultHttpClient();
				response = httpclient.execute(httppost);
				String data = EntityUtils.toString(response.getEntity());
				JSONObject json = new JSONObject(data);
				array = json.getJSONArray("results");
				Log.d("results length:===", "^^^^^^^^^^^^^^^^^^^length:"
						+ array.length());

				for (int i = 0; i < array.length(); i++) {
					Business tempBusiness = null;
					JSONObject result = (JSONObject) array.get(i);
					// name = result.getString("name");
					try {
						photos = result.getJSONArray("photos");
						photo = (JSONObject) photos.get(0);

						tempBusiness = new Business();
						tempBusiness.setGoogleAPIResult(result.toString());
						if (result.has("name"))
							tempBusiness.setName(result.getString("name"));
						if (result.has("id"))
							tempBusiness.setGooglePlaceID(result
									.getString("id"));
						if (result.has("vicinity"))
							tempBusiness.setAddress(result
									.getString("vicinity"));
						else if (result.has("formatted_address"))
							tempBusiness.setAddress(result
									.getString("formatted_address"));
						if (photo.has("photo_reference"))
							tempBusiness.setImageUrl(photo
									.getString("photo_reference"));

					} catch (JSONException ex) {
						Log.d("exception no photos:==", ex.getMessage());
						ex.printStackTrace();
					}
					if (tempBusiness != null) {
						System.out.println("if");
						businessList.add(tempBusiness);
					} else
						System.out.println("else");
				}
			} catch (Exception e) {
				Log.d("Response========", "inside catch");
				e.printStackTrace();
			}
			return businessList;
		}

		@Override
		protected void onPostExecute(final List<Business> businesses) {

			showProgress(false, "");

			if (businesses.size() == 0) {
				if (place == null)
					alertDialogBuilder = createDialog.createAlertDialog(
							"Oops!!",
							"No business found for current location.", false);
				else
					alertDialogBuilder = createDialog.createAlertDialog(
							"Oops!!", "No business found for keyword \""
									+ place + "\".", false);
				alertDialogBuilder.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
								lat1 = tracker.getLatitude();
								lon1 = tracker.getLongitude();
								latLongAddress = getAddress(lat1, lon1);
								showProgress(true, "You are at \n" + latLongAddress);
								new LoadStringsAsync(lat1, lon1, IdentifyingBusinessActivity.this, null).execute();
							}
						});

				alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}

			findViewById(R.id.skip_layout).setVisibility(View.VISIBLE);

			if (businesses.size() > 0) {
				CustomAdapter adapter = new CustomAdapter(
						IdentifyingBusinessActivity.this, businesses);
				listview.setAdapter(adapter);
				System.out.println(">>>>>>> before on click business");
				listview.setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Business selectedPlace = businesses.get(position);
                        System.out.println(">>>>>>> on click business");
						final ImageView image_view = (ImageView) view
								.findViewById(R.id.icon);
						final BitmapDrawable bitmapDrawable = (BitmapDrawable) image_view
								.getDrawable();
						final Bitmap bitmap = bitmapDrawable.getBitmap();
						ByteArrayOutputStream bs = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
						Customer customer = globalVariable.getCustomer();
							nextIntent = new Intent(
									IdentifyingBusinessActivity.this,
									BusinessHomePageActivity.class);

							nextIntent.putExtra("business_name",
									selectedPlace.getName());
							nextIntent.putExtra("business_id",
									selectedPlace.getGooglePlaceID());
							nextIntent.putExtra("complete_address",
									selectedPlace.getAddress()); // new
							nextIntent.putExtra("complete_result",
									selectedPlace.getGoogleAPIResult());
							nextIntent.putExtra("byteArray", bs.toByteArray());
							startActivity(nextIntent);
							overridePendingTransition(R.anim.anim_out,
									R.anim.anim_in);

					}
				});
			} else {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						activity);
				alertDialogBuilder.setTitle("Search");
				alertDialogBuilder
						.setMessage("No Business found")
						.setCancelable(false)
						.setPositiveButton("Search by Place",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										IdentifyingBusinessActivity.this
												.finish();
										Intent intent = new Intent(
												IdentifyingBusinessActivity.this,
												SearchByPlaceActivity.class);
										startActivity(intent);
										overridePendingTransition(
												R.anim.anim_out, R.anim.anim_in);

									}
								});

			}

		}

	}

	// **** async task end

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show, String msg) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);
			ImageView progress_bar = (ImageView) findViewById(R.id.rotate_logo);

			Animation rotateimage = AnimationUtils.loadAnimation(
					IdentifyingBusinessActivity.this, R.anim.custom_anim);
			progress_bar.startAnimation(rotateimage);

			mLoadingStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			TextView statusMsg = (TextView) mLoadingStatusView
					.findViewById(R.id.loading_status_message);
//			statusMsg.setTypeface(Typeface.createFromAsset(getAssets(),
//					"Fonts/SEGOEUIL.ttf"));
			statusMsg.setTypeface(Typeface.createFromAsset(getAssets(),
					"Fonts/OpenSans-Light.ttf"));
			statusMsg.setText(msg);
			//
			mLoadingStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoadingStatusView
									.setVisibility(show ? View.VISIBLE
											: View.GONE);
							findViewById(R.id.skip_layout).setVisibility(
									show ? View.GONE : View.VISIBLE);
						}
					});

			mListView.setVisibility(View.VISIBLE);
			mListView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mListView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			// mLoadingStatusView.setVisibility(show ? View.VISIBLE :
			// View.GONE);
			mLoadingStatusView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	public Bitmap getBitmap(String src) {
		Bitmap bitmap = null;
		try {
			String src1 = "CoQBdgAAAGs21qla1g2W332k3pa6T-RUKEOXXApt8N99XaIkE11MTlXvv7Uk00p3IkWlC1pDNS_OYIyPP1tlJP95p8Mgi4M4XB9aWIQpylHY0Ey9vLidfaN2uiTMjOFEj_zoHFVDQ32ETiN3YkN0fY2IN7nhzXAqcPoLllYThTE1b5XoXZvaEhCLWFtj44OueO8MssRAB0DSGhQFbpGXgz3si6KWAQ30LX9wazSuhw";
			URL url = new URL(
					"https://maps.googleapis.com/maps/api/place/photo?maxwidth=60&photoreference="
							+ src1
							+ "&sensor=true&key=AIzaSyDWngmH16EcyItOCncqQmyZGNZDA8AFuGs");
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			bitmap = BitmapFactory.decodeStream(input);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;

	}

	public String getAddress(double latitude, double longitude) {
		try {
			Geocoder geocoder = new Geocoder(this, Locale.getDefault());
			List<Address> addresses = geocoder.getFromLocation(latitude,
					longitude, 1);
			String str = addresses.get(0).getAddressLine(0)
					+ addresses.get(0).getAddressLine(1);
			String address = str.split(",")[0] + str.split(",")[1];
			Log.d("address====", address);
			return address;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

	public void onAddBusinessWithVenue(View v) {
		nextIntent = new Intent(IdentifyingBusinessActivity.this,
				BusinessHomePageActivity.class);

		nextIntent.putExtra("complete_address", latLongAddress);
		// nextIntent.putExtra("business_name", "");
		nextIntent.putExtra("defaultImage", true);
        nextIntent.putExtra("business_id", "custom Id");
        nextIntent.putExtra("complete_result", "custom business");
		startActivity(nextIntent);
		overridePendingTransition(R.anim.anim_out, R.anim.anim_in);

	}

	public void onSearch(View view) {
		Intent intent = new Intent(this, SearchByPlaceActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.anim_out, R.anim.anim_in);
	}
}