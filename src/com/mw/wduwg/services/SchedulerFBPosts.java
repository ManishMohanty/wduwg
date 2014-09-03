package com.mw.wduwg.services;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout.Alignment;
import android.util.Log;
import android.widget.Toast;

import com.example.wduwg.tiles.R;
import com.example.wduwg.tiles.CountActivity;
import com.example.wduwg.tiles.LoginFacebookActivity;
import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.android.Facebook;
import com.mw.wduwg.model.Business;
import com.mw.wduwg.model.Event;
import com.parse.entity.mime.HttpMultipartMode;
import com.parse.entity.mime.MultipartEntity;
import com.parse.entity.mime.content.ByteArrayBody;
import com.parse.entity.mime.content.StringBody;

public class SchedulerFBPosts extends TimerTask {

	Context context;
	GlobalVariable globalVariable;
	boolean menwomen ;

	public boolean isMenwomen() {
		return menwomen;
	}

	public void setMenwomen(boolean menwomen) {
		this.menwomen = menwomen;
	}

	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	private static String APP_ID = "743382039036135";
	int[] drawableArray = {R.drawable.bar1,R.drawable.bar2,R.drawable.bar3,R.drawable.bar4,R.drawable.bar5,R.drawable.bar6,
			R.drawable.bar7,R.drawable.bar8,R.drawable.bar10};

	private Facebook facebook;

	public SchedulerFBPosts(Context context) {
		super();
		this.context = context;
		globalVariable = (GlobalVariable) this.context.getApplicationContext();
		facebook = new Facebook(APP_ID);
		preferences = PreferenceManager.getDefaultSharedPreferences(context
				.getApplicationContext());
		String access_token = preferences.getString("fb_access_token", null);
		long expires = preferences.getLong("fb_access_expire", 0);

		if (access_token != null) {
			System.out.println("scheduler  if1");
			facebook.setAccessToken(access_token);
		}
		else	System.out.println("scheduler  else1");
		if (expires != 0) {
			System.out.println("scheduler  if2");
			facebook.setAccessExpires(expires);
		}
		else	System.out.println("scheduler  else2");
	}

	public void run() {
		System.out.println(">>>>>>> Scheduler");
		  Business currentBusiness = (Business)globalVariable.getSelectedBusiness();
		    FacebookPostAsyncExample asyncExample = new FacebookPostAsyncExample();
			asyncExample.execute(currentBusiness);
	}

	public void postToWall() {
		FacebookPostAsyncExample asyncExample = new FacebookPostAsyncExample();
//		asyncExample.execute(new String[] { "Helllo Worls" });
		asyncExample.execute(globalVariable.getSelectedBusiness());
	}

	private class FacebookPostAsyncExample extends AsyncTask<Business, Void, Boolean> {

		@SuppressWarnings("deprecation")
		@Override
		protected Boolean doInBackground(Business... params) {
			boolean returnBool = false;
			SimpleDateFormat df = new SimpleDateFormat("d MMM yyyy, h:mm a");
			df.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
			String postMessage = "";
			String postName = " Current Attendance:\t\t\t"+ (params[0].getMenIn()+params[0].getWomenIn() - (params[0].getMenOut()+params[0].getWomenOut()));
//			System.out.println(">>>>>>> page  token:" + params[0].getSelectedFBPage().getName());
			if(menwomen == true)
			{
				postMessage = " Men: \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t"
						+(params[0].getMenIn()-params[0].getMenOut())+"\n Women: \t\t\t\t\t\t\t\t\t\t\t\t\t\t"+(params[0].getWomenIn()-params[0].getWomenOut()) + "\n Time: \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t"+df.format(new Date())+"\n";	
			}else
			{
				postMessage = " Time: \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t"+df.format(new Date())+"\n";
			}
			
			System.out.println(">>>>>>> Message"+postMessage);
			// ********************************Convert String to Image **************************
			try {
		// =================== image append ===================	
				 int lower = 0;
				 int upper = 8;
				 int r =Integer.valueOf((int) ((Math.random() * (upper - lower)) + lower)) ;
				 
				 Bitmap myBitmap = BitmapFactory.decodeResource(context.getResources(), drawableArray[r]);
				 
				 
				 final Rect bounds = new Rect();
					TextPaint textPaint = new TextPaint() {
					    {
					        setColor(Color.parseColor("#000000"));
					        setTextAlign(Paint.Align.LEFT);
					        setTypeface(Typeface.createFromAsset(context.getAssets(),
					    			"Fonts/OpenSans-Bold.ttf"));
					        setTextSize(25f);
					        setAntiAlias(true);
					    }
					};
					textPaint.getTextBounds(postName, 0, postName.length(), bounds);
//					StaticLayout mTextLayout = new StaticLayout(postName, textPaint,
//							myBitmap.getWidth(), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
					StaticLayout mTextLayout = new StaticLayout(postName, textPaint,
							730, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
					int maxWidth = -1;
					for (int i = 0; i < mTextLayout.getLineCount(); i++) {
					    if (maxWidth < mTextLayout.getLineWidth(i)) {
					        maxWidth = (int) mTextLayout.getLineWidth(i);
					    }
					}
//					final Bitmap bmp = Bitmap.createBitmap(myBitmap.getWidth() , mTextLayout.getHeight(),
//					            Bitmap.Config.ARGB_8888);
					final Bitmap bmp = Bitmap.createBitmap(730 , mTextLayout.getHeight(),
				            Bitmap.Config.ARGB_8888);
					
					bmp.eraseColor(Color.parseColor("#ffffff"));// just adding black background
					final Canvas canvas = new Canvas(bmp);
					mTextLayout.draw(canvas);
					
					
					TextPaint textPaint1 = new TextPaint() {
					    {
					        setColor(Color.parseColor("#837777"));
					        setTextAlign(Paint.Align.LEFT);
					        setTypeface(Typeface.createFromAsset(context.getAssets(),
					    			"Fonts/OpenSans-Bold.ttf"));
					        setTextSize(25f);
					        setAntiAlias(true);
					    }
					};
					textPaint.getTextBounds(postMessage, 0, postMessage.length(), bounds);
					StaticLayout mTextLayout1 = new StaticLayout(postMessage, textPaint1,
							myBitmap.getWidth(), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
					int maxWidth1 = -1;
					for (int i = 0; i < mTextLayout1.getLineCount(); i++) {
					    if (maxWidth1 < mTextLayout1.getLineWidth(i)) {
					        maxWidth1 = (int) mTextLayout1.getLineWidth(i);
					    }
					}
//					final Bitmap bmp1 = Bitmap.createBitmap(myBitmap.getWidth() , mTextLayout1.getHeight(),
//					            Bitmap.Config.ARGB_8888);
					final Bitmap bmp1 = Bitmap.createBitmap(730 , mTextLayout1.getHeight(),
				            Bitmap.Config.ARGB_8888);
					
					bmp1.eraseColor(Color.parseColor("#ffffff"));// just adding black background
					final Canvas canvas1 = new Canvas(bmp1);
					mTextLayout1.draw(canvas1);

//					Bitmap bmOverlay = Bitmap.createBitmap(myBitmap.getWidth(),
//							myBitmap.getHeight() + bmp.getHeight()+bmp1.getHeight(),
//							Bitmap.Config.ARGB_8888);
					Bitmap bmOverlay = Bitmap.createBitmap(730,
							myBitmap.getHeight() + bmp.getHeight()+bmp1.getHeight(),
							Bitmap.Config.ARGB_8888);
					Canvas canvasAppend = new Canvas(bmOverlay);
					canvasAppend.drawBitmap(myBitmap, 0.f, 0.f, null);
					canvasAppend.drawBitmap(bmp, 0.f, myBitmap.getHeight(), null);
					canvasAppend.drawBitmap(bmp1, 0.f, myBitmap.getHeight()+bmp.getHeight(), null);
					OutputStream os = null;
					byte[] data = null;
				    	
				      ByteArrayOutputStream baos = new ByteArrayOutputStream();
				      bmp.recycle();
				      bmOverlay.compress(CompressFormat.JPEG, 100, baos); 
				      data = baos.toByteArray();
				   // *********************************end conversion ***********************
				      // posting to page wall
				      ByteArrayBody bab = new ByteArrayBody(data, "test.png");
					 try{
						 System.out.println(">>>>>>> string after conversion");
						 System.out.println(">>>>>>> page token:"+params[0].getSelectedFBPage().getAccess_token());
						 // create new Session with page access_token
//						 Session.openActiveSessionWithAccessToken(context,AccessToken.createFromExistingAccessToken(globalVariable.getSelectedFBPage().getAccess_token(), new Date(facebook.getAccessExpires()), new Date( facebook.getLastAccessUpdate()), AccessTokenSource.FACEBOOK_APPLICATION_NATIVE, Arrays.asList("manage_pages","publish_stream","photo_upload")) , new Session.StatusCallback() {
						 Session.openActiveSessionWithAccessToken(context,AccessToken.createFromExistingAccessToken(params[0].getSelectedFBPage().getAccess_token(), new Date(facebook.getAccessExpires()), new Date( facebook.getLastAccessUpdate()), AccessTokenSource.FACEBOOK_APPLICATION_NATIVE, Arrays.asList("manage_pages","publish_stream","photo_upload")) , new Session.StatusCallback() {
								@Override
								public void call(Session session, SessionState state, Exception exception) {
									System.out.println(">>>>>>> session status callback");
									// TODO Auto-generated method stub
									if(session != null && session.isOpened()) {
						                Session.setActiveSession(session);
						                Session session1  = Session.getActiveSession();
						                System.out.println(">>>>>>> is Manage"+session1.isPublishPermission("manage_pages"));
						            }
								}
							});// session open closed
							System.out.println(">>>>>>> new session open");
						 
						 
//				    String url = "https://graph.facebook.com/"+globalVariable.getSelectedFBPage().getId()+"/photos";
							String url = "https://graph.facebook.com/"+params[0].getSelectedFBPage().getId()+"/photos";
					HttpPost postRequest = new HttpPost(url);
					HttpParams http_parameters = new BasicHttpParams();
				    HttpConnectionParams.setConnectionTimeout(http_parameters, 3000);
				    HttpClient httpClient = new DefaultHttpClient();
				    MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				    System.out.println(">>>>>>> Start post");
				    reqEntity.addPart("access_token", new StringBody(params[0].getSelectedFBPage().getAccess_token()));
				    System.out.println(">>>>>>> under  post1");
//				    reqEntity.addPart("message", new StringBody(""));
				    System.out.println(">>>>>>> under  post2");
				    reqEntity.addPart("picture", bab);
				    System.out.println(">>>>>>> under  post3");
				    postRequest.setEntity(reqEntity);
				    System.out.println(">>>>>>> under  post4");
				    HttpResponse response1 = httpClient.execute(postRequest);
				    System.out.println(">>>>>>> under  post5");
				    System.out.println(">>>>>>> response"+response1);
				    if (response1 == null || response1.equals("")
							|| response1.equals("false")) {
						System.out.println(">>>>>>> Blank response.");
					} else {
						System.out.println(">>>>>>> Message posted to your facebook wall! -->"+ url);
						returnBool = true;
					}
					 }catch(Exception e)
					 {
						 
					 }
			} catch (Exception e) {
				System.out.println("Failed to post to wall!");
				e.printStackTrace();
			}
			
			return returnBool;
		}

		@Override
		protected void onPostExecute(Boolean result) {
		}
		public String convertDate(String datestr)
		{
			String formatedDate="";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try{
				int hh = Integer.parseInt(datestr.split(",")[1].split(":")[0]);
				DateFormat df = new SimpleDateFormat("dd MMM yyyy");
				if(hh<13)
				{
				formatedDate=formatedDate+df.format(sdf.parse(datestr.split(",")[0])) + " ,  "+datestr.split(",")[1]+"  AM";
				}else
				{
					formatedDate=formatedDate+df.format(sdf.parse(datestr.split(",")[0])) + " ,  "+datestr.split(",")[1]+"  PM";
				}
			}catch(Exception e)
			{
				e.printStackTrace();
				
			}
			return formatedDate;
		}

	}
}
