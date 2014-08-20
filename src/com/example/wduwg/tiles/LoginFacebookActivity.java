package com.example.wduwg.tiles;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract.Constants;
import android.text.Editable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wduwg.tiles.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.LoginActivity;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mw.wduwg.model.Business;
import com.mw.wduwg.model.BusinessFBPage;
import com.mw.wduwg.model.Customer;
import com.mw.wduwg.model.Event;
import com.mw.wduwg.model.Special;
import com.mw.wduwg.services.CreateDialog;
import com.mw.wduwg.services.GlobalVariable;
import com.mw.wduwg.services.JSONParser;
import com.mw.wduwg.services.SchedulerFBPosts;
import com.parse.entity.mime.HttpMultipartMode;
import com.parse.entity.mime.MultipartEntity;
import com.parse.entity.mime.content.ByteArrayBody;
import com.parse.entity.mime.content.ContentBody;
import com.parse.entity.mime.content.StringBody;

public class LoginFacebookActivity extends Activity {
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		globalVariable.saveSharedPreferences();
	}

	ImageView headerIV;
	AlertDialog.Builder alertDialogBuilder;
	Button postButton;
	Button profileButton;
	Button logoutButton;
	TextView actionBarTextView;		
	CreateDialog createDialog;
	ProgressDialog progressDialgog;
	private static String APP_ID = "743382039036135"; // Replace your facebook app ID here

	boolean fromContext;
	boolean isCustomer ;
	GlobalVariable globalVariable;
	
	// Instance of Facebook Class
	private Facebook facebook;
	
	private AsyncFacebookRunner mAsyncRunner;
	String FILENAME = "AndroidSSO_data";
	
	String userID , user_access_token,page_access_token;
	AlertDialog alertDialog;
	SharedPreferences preferences;
	SharedPreferences.Editor editor;

	Typeface typeface;

	EditText messageFbEt;
	int[] drawableArray = {R.drawable.bar1,R.drawable.bar2,R.drawable.bar3,R.drawable.bar4,R.drawable.bar5,R.drawable.bar6,
			R.drawable.bar7,R.drawable.bar8,R.drawable.bar10};

	SchedulerFBPosts scheduledTask;
	public static Timer timer;
	
	private void findThings() {
		messageFbEt = (EditText) findViewById(R.id.message_fb_ET);

		typeface = Typeface.createFromAsset(getAssets(),
				"Fonts/OpenSans-Light.ttf");
		headerIV = (ImageView) findViewById(R.id.header_IV);
		postButton = (Button) findViewById(R.id.post_button);
		postButton.setTypeface(typeface);
		profileButton = (Button) findViewById(R.id.profile_button);
		logoutButton = (Button) findViewById(R.id.logout_button);

	}

	private void actionBarAndKeyboardAndListener() {
		((RelativeLayout) findViewById(R.id.facebook_login_RL))
				.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(getCurrentFocus()
								.getWindowToken(), 0);
						return false;
					}
				});

		int titleId = getResources().getIdentifier("action_bar_title", "id",
				"android");
		actionBarTextView = (TextView) findViewById(titleId);
		actionBarTextView.setTextColor(Color.parseColor("#016AB2"));
		actionBarTextView.setTypeface(typeface);

		messageFbEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				messageFbEt.setError(null);
			}
		});

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facebook_login);
        fromContext = getIntent().getBooleanExtra("fromContext", false);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
		findThings();
		if(fromContext == false)
		{
			findViewById(R.id.facebook_login_RL).setVisibility(View.GONE);
		}
		else
		{
			findViewById(R.id.facebook_login_RL).setVisibility(View.VISIBLE);
		}
		actionBarAndKeyboardAndListener();

		facebook = new Facebook(APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(facebook);

		globalVariable = (GlobalVariable) getApplicationContext();

		loginToFacebook();

	}

	public void loginToFacebook() {
		

		if (globalVariable.getFb_access_token() != null) {
			System.out.println(">>>> access token is not null");
			facebook.setAccessToken(globalVariable.getFb_access_token());
		}

		if (globalVariable.getFb_access_expire() != 0) {
			System.out.println("if2");
			facebook.setAccessExpires(globalVariable.getFb_access_expire());
		}

		if (!facebook.isSessionValid()) {
			System.out.println("if3");
			facebook.authorize(this,
					new String[] { "email", "publish_stream" ,"manage_pages","publish_actions","status_update","photo_upload","offline_access"},Facebook.FORCE_DIALOG_AUTH,
					new DialogListener() {

						@Override
						public void onCancel() {

							// Function to handle cancel event
							LoginFacebookActivity.this.setResult(RESULT_OK);
							finish();
						}

						@Override
						public void onComplete(Bundle values) {
							
							
							System.out.println(">>>>>>> Access expiry:"+facebook.getAccessExpires());
							globalVariable.setFb_access_expire(facebook.getAccessExpires());
							globalVariable.setFb_access_token(facebook.getAccessToken());
							if(globalVariable.isfacebookOn() == true)
							{
//								timer = new Timer();
//								scheduledTask = new SchedulerFBPosts(LoginFacebookActivity.this);
//								if(globalVariable.facebookFrequency().equalsIgnoreCase("Men and Women"))
//									scheduledTask.setMenwomen(true);
//								timer.scheduleAtFixedRate(scheduledTask, 1000, 60 * 60 * 1000);
							}else
							{
								Log.d(">>>>>>> facebook switch off","------------");
							}  
							try{
								createDialog = new CreateDialog(LoginFacebookActivity.this);
								progressDialgog = createDialog.createProgressDialog("Validation", "Please wait for a while", true, null);
								progressDialgog.show();
								getProfileInformation();
							}catch(Exception e)
							{
								e.printStackTrace();
							}
							postButton.setEnabled(true);
							profileButton.setEnabled(true);       
							logoutButton.setEnabled(true);
						}

						@Override
						public void onError(DialogError error) {
							// Function to handle error

						}

						@Override
						public void onFacebookError(FacebookError fberror) {
							// Function to handle Facebook errors
						}

					});
		}
	}

	
	private boolean validate() {
		boolean bool = true;
		if (messageFbEt.getText().toString().trim().length() == 0) {
			messageFbEt.setError("Enter Something");
			bool = false;
		}
		return bool;
	}


//	private class FacebookPostAsyncExample extends AsyncTask<String, Void, Boolean> {
//
//		@SuppressWarnings("deprecation")
//		@Override
//		protected Boolean doInBackground(String... params) {
////			System.out.println(">>>>>>> in post async");
//			boolean returnBool = false;
//			String postMessage="";
//			if(globalVariable.getSelectedEvent()!= null)
//			{
//			Event tempEvent = globalVariable.getSelectedEvent();
//			postMessage = postMessage + "\n  Event:\t\t\t\t\t\t\t\t\t\t\t\t\t\t"+ tempEvent.getName();
//				
//					postMessage = postMessage 
//							+ "\n  Start Time:\t\t\t\t\t\t\t\t\t\t"+ convertDate(tempEvent.getStartDate().replace('T', ',').substring(0, (tempEvent.getStartDate().length()-13))) + "\n  End Time:\t\t\t\t\t\t\t\t\t\t\t" + convertDate(tempEvent.getEndDate().replace('T', ',').substring(0, (tempEvent.getEndDate().length()-13)));
//				
//			}else
//			{
//				Special tempSpecial = globalVariable.getSelectedBusiness().getSpecials().get(globalVariable.getSelectedBusiness().getSpecials().size() - 1);
//				postMessage = postMessage + "\n  Event:\t\t\t\t\t\t\t\t\t\t\t\t\t\t"+ tempSpecial.getName();
//				
//				postMessage = postMessage 
//						+ "\n  Start Time:\t\t\t\t\t\t\t\t\t\t"+ convertDate(tempSpecial.getStartDate().replace('T', ',').substring(0, (tempSpecial.getStartDate().length()-13))) + "\n  End Time:\t\t\t\t\t\t\t\t\t\t\t" + convertDate(tempSpecial.getEndDate().replace('T', ',').substring(0, (tempSpecial.getEndDate().length()-13)));
//			}
//			
//			
//		
//		System.out.println(">>>>>>> Message"+postMessage);
//			// ********************************Convert String to Image **************************
//			try {
//		// =================== image append ===================	
//				 int lower = 0;
//				 int upper = 8;
//				 int r =Integer.valueOf((int) ((Math.random() * (upper - lower)) + lower)) ;
//				 
//				 Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), drawableArray[r]);
//				 
//				 
//				 final Rect bounds = new Rect();
//					TextPaint textPaint = new TextPaint() {
//					    {
//					        setColor(Color.parseColor("#ffffff"));
//					        setTextAlign(Paint.Align.LEFT);
//					        setTypeface(Typeface.createFromAsset(LoginFacebookActivity.this.getAssets(),
//					    			"Fonts/OpenSans-Light.ttf"));
//					        setTextSize(35f);
//					        setAntiAlias(true);
//					    }
//					};
//					textPaint.getTextBounds(postMessage, 0, postMessage.length(), bounds);
//					StaticLayout mTextLayout = new StaticLayout(postMessage, textPaint,
//							myBitmap.getWidth(), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
//					int maxWidth = -1;
//					for (int i = 0; i < mTextLayout.getLineCount(); i++) {
//					    if (maxWidth < mTextLayout.getLineWidth(i)) {
//					        maxWidth = (int) mTextLayout.getLineWidth(i);
//					    }
//					}
//					final Bitmap bmp = Bitmap.createBitmap(myBitmap.getWidth() , mTextLayout.getHeight(),
//					            Bitmap.Config.ARGB_8888);
//					
//					bmp.eraseColor(Color.parseColor("#3c8383"));// just adding black background
//					final Canvas canvas = new Canvas(bmp);
//					mTextLayout.draw(canvas);
//				 
//				 
//				 
//				 Bitmap bmOverlay = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight()+bmp.getHeight(),  Bitmap.Config.ARGB_8888);
//				 Canvas canvasAppend = new Canvas(bmOverlay);
//				 canvasAppend.drawBitmap(myBitmap, 0.f, 0.f, null);
//				 canvasAppend.drawBitmap(bmp, 0.f, myBitmap.getHeight(), null);
//				 OutputStream os = null; 
//				 byte[] data = null;
//				    	
//				      ByteArrayOutputStream baos = new ByteArrayOutputStream();
//				      bmp.recycle();
//				      bmOverlay.compress(CompressFormat.JPEG, 100, baos); 
//				      data = baos.toByteArray();
//				   // *********************************end conversion ***********************
//				      // posting to page wall
//				      ByteArrayBody bab = new ByteArrayBody(data, "test.png");
//					 try{
//						 // create new Session with page access_token
//						 Session.openActiveSessionWithAccessToken(getApplicationContext(),AccessToken.createFromExistingAccessToken(globalVariable.getSelectedFBPage().getAccess_token(), new Date(facebook.getAccessExpires()), new Date( facebook.getLastAccessUpdate()), AccessTokenSource.FACEBOOK_APPLICATION_NATIVE, Arrays.asList("manage_pages","publish_stream","photo_upload")) , new Session.StatusCallback() {
//								@Override
//								public void call(Session session, SessionState state, Exception exception) {
//									System.out.println(">>>>>>> session status callback");
//									// TODO Auto-generated method stub
//									if(session != null && session.isOpened()) {
//						                Session.setActiveSession(session);
//						                Session session1  = Session.getActiveSession();
//						                System.out.println(">>>>>>> is Manage"+session1.isPublishPermission("manage_pages"));
//						            }
//								}
//							});// session open closed
//							System.out.println(">>>>>>> new session open");
//						 
//						 
//					String url = "https://graph.facebook.com/"+globalVariable.getSelectedFBPage().getId()+"/photos";
//					HttpPost postRequest = new HttpPost(url);
//					HttpParams http_parameters = new BasicHttpParams();
//				    HttpConnectionParams.setConnectionTimeout(http_parameters, 3000);
//				    HttpClient httpClient = new DefaultHttpClient();
//				    MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//				    reqEntity.addPart("access_token", new StringBody(Session.getActiveSession().getAccessToken()));
//				    reqEntity.addPart("message", new StringBody(messageFbEt.getEditableText().toString()));
//				    reqEntity.addPart("picture", bab);
//				    postRequest.setEntity(reqEntity);
//				    HttpResponse response1 = httpClient.execute(postRequest);
//				    System.out.println(">>>>>>> response"+response1);
//				    if (response1 == null || response1.equals("")
//							|| response1.equals("false")) {
//						System.out.println(">>>>>>> Blank response.");
//					} else {
//						System.out.println(">>>>>>> Message posted to your facebook wall!");
//						returnBool = true;
//					}
//					 }catch(Exception e)
//					 {
//						 
//					 }
//				LoginFacebookActivity.this.setResult(RESULT_OK);
//				finish();
//			} catch (Exception e) {
//				System.out.println("Failed to post to wall!");
//				e.printStackTrace();
//				LoginFacebookActivity.this.setResult(RESULT_OK);
//				finish();
//			}
//			
//			return returnBool;
//		}
//
//		@Override
//		protected void onPostExecute(Boolean result) {
//			if (result)
////				progressDialgog.dismiss();
//				Toast.makeText(LoginFacebookActivity.this,
//						"Posted on your selected page.", Toast.LENGTH_SHORT).show();
//		}
//		
//		public String convertDate(String datestr)
//		{
//			String formatedDate="";
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//			try{
//				int hh = Integer.parseInt(datestr.split(",")[1].split(":")[0]);
//				DateFormat df = new SimpleDateFormat("dd MMM yyyy");
//				if(hh<13)
//				{
//				formatedDate=formatedDate+df.format(sdf.parse(datestr.split(",")[0])) + " ,  "+datestr.split(",")[1]+"  AM";
//				}else
//				{
//					formatedDate=formatedDate+df.format(sdf.parse(datestr.split(",")[0])) + " ,  "+datestr.split(",")[1]+"  PM";
//				}
//			}catch(Exception e)
//			{
//				e.printStackTrace();
//				
//			}
//			return formatedDate;
//		}
//
//	}

	public void getProfileInformation() {
		mAsyncRunner.request("me", new RequestListener() {
			@Override
			public void onComplete(String response, Object state) {
				System.out.println(">>>>>>> Profile"+ response);
				String json = response;
				
				try {
					JSONObject profile = new JSONObject(json);
					// getting name of the user
//					final String name = profile.getString("name");
					// getting email of the user
					final String email = profile.getString("email");
					System.out.println(">>>>>>> email:"+email);
					userID = profile.getString("id");
					System.out.println(">>>>>>> User id:"+userID);
					System.out.println(">>>>>>> access Token while login:"+facebook.getAccessToken());
					user_access_token = facebook.getAccessToken();
					JSONParser jsonparser = new JSONParser(LoginFacebookActivity.this);
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("email", email));
					JSONObject jsonobject = jsonparser.getJSONObjectFromUrlAfterHttpGet("http://dcounter.herokuapp.com/customers.json", params);
                    Log.d(">>>>>>> user validation json=======","jsonobject"+jsonobject);
                    if(jsonobject.getString("status").equals("ok"))
                    {
                    	Gson gson = new Gson();
                    	String customerJsonString = jsonobject.getString("customer");
                    	System.out.println("json customr"+customerJsonString);
                    	Customer customer = gson.fromJson(customerJsonString.toString(), Customer.class);
                    	String businesses = jsonobject.getString("businesses");
                    	
                    	List<Business> businessList=new ArrayList<Business>();;
                    	JSONArray jsonArray = new JSONArray(businesses.toString());
                    	for (int i=0; i<jsonArray.length(); i++) {
                    		businessList.add( gson.fromJson(jsonArray.getString(i), Business.class));
                    	}
                    	
                    	customer.setBusinesses(businessList);
                    	System.out.println("customer business"+customer.getBusinesses().size());
                    	List<Business> bnessList = customer.getBusinesses();
                    	System.out.println(">>>>>>> Businesses :"+bnessList.size());
                    	isCustomer = true;
                    	globalVariable.setCustomer(customer);
                    	System.out.println("global customer's id:"+globalVariable.getCustomer().getId().get$oid());
                    	
                    	// ************ pages
                    	mAsyncRunner.request("me/accounts", new RequestListener() {
							
							@Override
							public void onMalformedURLException(MalformedURLException e, Object state) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onIOException(IOException e, Object state) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onFileNotFoundException(FileNotFoundException e, Object state) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onFacebookError(FacebookError e, Object state) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onComplete(String response, Object state) {
								// TODO Auto-generated method stub
								
								try{
								JSONObject jsonForPage = new JSONObject(response);
								JSONArray data = jsonForPage.getJSONArray("data");
								Gson gson = new Gson();
								List<BusinessFBPage> pages = new ArrayList<BusinessFBPage>();
								for(int i=0;i<data.length();i++)
								{
									BusinessFBPage page = gson.fromJson(data.get(i).toString(), BusinessFBPage.class);
									pages.add(page);
									for(int j=0;j<globalVariable.getCustomer().getBusinesses().size();j++)
									{
										if(globalVariable.getCustomer().getBusinesses().get(j).getFace_book_page().equals(page.getId()))
										{
											globalVariable.getCustomer().getBusinesses().get(j).setSelectedFBPage(page);
										}
									}
								}
								globalVariable.getCustomer().setPages(pages);
								System.out.println(">>>>>>> get pageAccessToken complete");
								page_access_token = globalVariable.getCustomer().getPages().get(0).getAccess_token();
								}catch(Exception e)
								{
									e.printStackTrace();
								}
							}
						});
                    }
                    else
                    {
                    	isCustomer = false;
                    }
                    
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							try{
							 Thread.sleep(500);
							}catch(Exception e)
							{
								e.printStackTrace();
							}
							progressDialgog.dismiss();
							if(isCustomer)
							{
							     Intent intent = new Intent(LoginFacebookActivity.this,BusinessOfUserActivity.class);
							     startActivity(intent);
							}
//							     else if (isCustomer)
//							{
//								Intent intent = new Intent(LoginFacebookActivity.this,MainActivity.class);
//								startActivity(intent);
//							}
							else
							{
								Toast.makeText(getApplicationContext(),
										 "\nEmail: " + email + "Does not exist",
										Toast.LENGTH_LONG).show();
								globalVariable.setFb_access_token(null);
								globalVariable.setFb_access_expire(0);
								LoginFacebookActivity.this.finish();
//								Intent intent = new Intent(LoginFacebookActivity.this,SpalshFirstActivity.class);
//							     startActivity(intent);
							}
						}

					});

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onIOException(IOException e, Object state) {
			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
			}

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
			}
		});
	}

	public void logoutFromFacebook() {

		editor = preferences.edit();
		editor.remove("access_token");

		editor.remove("access_expires");

		editor.commit();

		finish();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		facebook.authorizeCallback(requestCode, resultCode, data);
	}

	public void onLogin(View view) {
		loginToFacebook();
	}

	public void onPost(View view) {
//		postToWall();
	}

	public void onProfile(View view) {
		getProfileInformation();
	}

	public void onLogout(View view) {
		logoutFromFacebook();
	}

	@Override
	public void onBackPressed() {
		this.setResult(RESULT_OK);
		finish();
	}

	
	
}
