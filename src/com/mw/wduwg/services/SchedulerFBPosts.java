package com.mw.wduwg.services;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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

import com.example.wduwg.CountActivity;
import com.example.wduwg.LoginFacebookActivity;
import com.example.wduwg.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.android.Facebook;
import com.mw.wduwg.model.Event;
import com.parse.entity.mime.HttpMultipartMode;
import com.parse.entity.mime.MultipartEntity;
import com.parse.entity.mime.content.ByteArrayBody;
import com.parse.entity.mime.content.StringBody;

public class SchedulerFBPosts extends TimerTask {

	Context context;
	GlobalVariable globalVariable;

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
		Log.d(">>>>>>>", "Scheduler");
		if(preferences.contains("fb_access_token") && preferences.getBoolean("facebookSwitch", false) == true && preferences.contains("prefFb_frequency"))
		{
			System.out.println(">>>>>>> interval in scheduler :"+preferences.getString("prefFb_frequency", null));
		FacebookPostAsyncExample asyncExample = new FacebookPostAsyncExample();
		asyncExample.execute(new String[] { "Helllo Worlds" });
		}
		else
		{
			if(LoginFacebookActivity.timer != null)
			{
				LoginFacebookActivity.timer.cancel();
			    SchedulerFBPosts.this.cancel();
			}
		}
	}

	public void postToWall() {
		FacebookPostAsyncExample asyncExample = new FacebookPostAsyncExample();
		asyncExample.execute(new String[] { "Helllo Worls" });
	}

	private class FacebookPostAsyncExample extends AsyncTask<String, Void, Boolean> {

		@SuppressWarnings("deprecation")
		@Override
		protected Boolean doInBackground(String... params) {
			boolean returnBool = false;
			String postMessage = "";
			Event tempEvent = globalVariable.getSelectedEvent();
			System.out.println(">>>>>>> gloabalVariable selected event:"+globalVariable.getSelectedEvent().getName());
			if (!tempEvent.getName().equals("defaultEvent")) {
				postMessage = postMessage + "\n  Event:\t\t\t\t\t\t\t\t\t\t\t\t\t\t"+ tempEvent.getName()
						+ "\n  Start Time:\t\t\t\t\t\t\t\t\t\t"+ convertDate(tempEvent.getStartDate().replace('T', ',').substring(0, (tempEvent.getStartDate().length()-13))) + "\n  End Time:\t\t\t\t\t\t\t\t\t\t\t" + convertDate(tempEvent.getEndDate().replace('T', ',').substring(0, (tempEvent.getEndDate().length()-13)));
			}else
			{
				postMessage = postMessage + "\n  Event:\t\t\t\t\t\t\t\t\t\t\t\t\t\t"+ tempEvent.getName();
				postMessage = postMessage 
						+ "\n  Start Time:\t\t\t\t\t\t\t\t\t\t"+ convertDate(tempEvent.getStartDate().replace('T', ',').substring(0, (tempEvent.getStartDate().length()-13))) + "\n  End Time:\t\t\t\t\t\t\t\t\t\t\t" + "daily";
			}
			
			int length = ((globalVariable.getMenIn() - globalVariable.getMenOut())+"").length() -1;
			postMessage = postMessage		+ "\n  Number of Patrons:\t\t\t"
					+ ((globalVariable.getMenIn() - globalVariable.getMenOut()) + (globalVariable.getWomenIn() - globalVariable.getWomenOut())) + "\n  Men: "
					+ (globalVariable.getMenIn() - globalVariable.getMenOut()) + "\t\t\t\t\t\t\t\t\t\t\t\t\tWomen: ".substring(length, 20)
					+ (globalVariable.getWomenIn() - globalVariable.getWomenOut())+"\n";
			
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
					        setColor(Color.parseColor("#ffffff"));
					        setTextAlign(Paint.Align.LEFT);
					        setTypeface(Typeface.createFromAsset(context.getAssets(),
					    			"Fonts/OpenSans-Light.ttf"));
					        setTextSize(35f);
					        setAntiAlias(true);
					    }
					};
					textPaint.getTextBounds(postMessage, 0, postMessage.length(), bounds);
					StaticLayout mTextLayout = new StaticLayout(postMessage, textPaint,
							myBitmap.getWidth(), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
					int maxWidth = -1;
					for (int i = 0; i < mTextLayout.getLineCount(); i++) {
					    if (maxWidth < mTextLayout.getLineWidth(i)) {
					        maxWidth = (int) mTextLayout.getLineWidth(i);
					    }
					}
					final Bitmap bmp = Bitmap.createBitmap(myBitmap.getWidth() , mTextLayout.getHeight(),
					            Bitmap.Config.ARGB_8888);
					
					bmp.eraseColor(Color.parseColor("#66AfD9"));// just adding black background
					final Canvas canvas = new Canvas(bmp);
					mTextLayout.draw(canvas);
				 
				 
				 
				 Bitmap bmOverlay = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight()+bmp.getHeight(),  Bitmap.Config.ARGB_8888);
				 Canvas canvasAppend = new Canvas(bmOverlay);
				 canvasAppend.drawBitmap(myBitmap, 0.f, 0.f, null);
				 canvasAppend.drawBitmap(bmp, 0.f, myBitmap.getHeight(), null);
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
						 // create new Session with page access_token
						 Session.openActiveSessionWithAccessToken(context,AccessToken.createFromExistingAccessToken(globalVariable.getSelectedFBPage().getAccess_token(), new Date(facebook.getAccessExpires()), new Date( facebook.getLastAccessUpdate()), AccessTokenSource.FACEBOOK_APPLICATION_NATIVE, Arrays.asList("manage_pages","publish_stream","photo_upload")) , new Session.StatusCallback() {
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
						 
						 
				    String url = "https://graph.facebook.com/"+globalVariable.getSelectedFBPage().getId()+"/photos";
					HttpPost postRequest = new HttpPost(url);
					HttpParams http_parameters = new BasicHttpParams();
				    HttpConnectionParams.setConnectionTimeout(http_parameters, 3000);
				    HttpClient httpClient = new DefaultHttpClient();
				    MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				    System.out.println(">>>>>>> Start post");
				    reqEntity.addPart("access_token", new StringBody(globalVariable.getSelectedFBPage().getAccess_token()));
				    System.out.println(">>>>>>> under  post1");
				    reqEntity.addPart("message", new StringBody("test ................."));
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
						System.out.println(">>>>>>> Message posted to your facebook wall!");
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
