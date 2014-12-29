package com.mw.wduwg.services;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.example.wduwg.tiles.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.android.Facebook;
import com.mw.wduwg.model.Business;
import com.parse.entity.mime.HttpMultipartMode;
import com.parse.entity.mime.MultipartEntity;
import com.parse.entity.mime.content.ByteArrayBody;
import com.parse.entity.mime.content.StringBody;

public class SchedulerFBPosts extends TimerTask {

	Context context;
	GlobalVariable globalVariable;
	boolean menwomen;

	public boolean isMenwomen() {
		return menwomen;
	}

	public void setMenwomen(boolean menwomen) {
		this.menwomen = menwomen;
	}

	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	private static String APP_ID = "743382039036135";
	List <Integer> drawableList = new ArrayList<Integer>();
	int[] drawableArray = { R.drawable.bar1, R.drawable.bar2, R.drawable.bar3,
			R.drawable.bar4, R.drawable.bar5, R.drawable.bar6, R.drawable.bar7,
			R.drawable.bar8, R.drawable.bar10 };

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
			facebook.setAccessToken(access_token);
		} else
		if (expires != 0) {
			facebook.setAccessExpires(expires);
		} 
	}

	public void run() {
		
		Field[] drawables = R.drawable.class.getFields();
		for (Field f : drawables) {
		    try {
		        if(f.getName().toUpperCase().contains(globalVariable.getSelectedBusiness().getName().toUpperCase()) || f.getName().toUpperCase().contains(globalVariable.getSelectedBusiness().getName().replaceAll("\\s","").toUpperCase()))
		        {
		        	drawableList.add(f.getInt(null));
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
		
		
		Business currentBusiness = (Business) globalVariable
				.getSelectedBusiness();
		FacebookPostAsyncExample asyncExample = new FacebookPostAsyncExample();
		asyncExample.execute(currentBusiness);
	}

	public void postToWall() {
		FacebookPostAsyncExample asyncExample = new FacebookPostAsyncExample();
		asyncExample.execute(globalVariable.getSelectedBusiness());
	}

	private class FacebookPostAsyncExample extends
			AsyncTask<Business, Void, Boolean> {

		@SuppressWarnings("deprecation")
		@Override
		protected Boolean doInBackground(Business... params) {
			boolean returnBool = false;
			SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM, h:mm a");
			df.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
			String datetime = df.format(new Date());
			
			String men = (params[0].getMenIn() - params[0].getMenOut())+"";
		    String women = (params[0].getWomenIn() - params[0].getWomenOut())+"";
		    String total = (params[0].getMenIn() + params[0].getWomenIn() - (params[0]
					.getMenOut() + params[0].getWomenOut()))+"";
            men = four_digit(men);
            women = four_digit(women);
            total = four_digit(total);
			String postName = "\n\t\t\t "+datetime.substring(0, 11)+"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t"+datetime.substring(13, datetime.length());
			String postMessage;
			String attendanceLabel;
			if(menwomen == true)
			{
			 postMessage = "\t\t\t  "+men+"                 "+total+"                 "+women;
			 attendanceLabel = "\t\t\t   Men\t\t\t\t\t\t\t\t\t\t\t\tCurrent Attendance\t\t\t\t\t\t\t\t\t\t   Women\t\n";
			}
			else
			{
				postMessage = "\t\t\t\t\t\t\t  "+"                 "+total;
				attendanceLabel = "\t\t\t      \t\t\t\t\t\t\t\t\t\t\t\tCurrent Attendance\t\t\t\t\t\t\t\t\t\t        \t\n";
			}
			try {
				int lower = 0;
				int upper = 8;
				Bitmap myBitmap;
				if(drawableList.size() > 0)
				{
					upper = drawableList.size() -1;
				int r = Integer
						.valueOf((int) ((Math.random() * (upper - lower)) + lower));

				 myBitmap = BitmapFactory.decodeResource(
						context.getResources(), drawableList.get(r));
				}
				else
				{
					int r = Integer
							.valueOf((int) ((Math.random() * (upper - lower)) + lower));
					myBitmap = BitmapFactory.decodeResource(
							context.getResources(), drawableArray[r]);
				}
				int width = myBitmap.getWidth();
				int height = myBitmap.getHeight();
				height = (int) (height * 850) / width;
				myBitmap = Bitmap.createScaledBitmap(myBitmap, 850, height,
						true);

				final Rect bounds = new Rect();
				TextPaint textPaint = new TextPaint() {
					{
						setColor(Color.parseColor("#837777"));
						setTextAlign(Paint.Align.LEFT);
						setTypeface(Typeface.createFromAsset(
								context.getAssets(), "Fonts/ufonts.com_segoe_ui_semibold.ttf"));
						setTextSize(18f);
						setAntiAlias(true);
					}
				};
				textPaint.getTextBounds(postName, 0, postName.length(), bounds);
				StaticLayout mTextLayout = new StaticLayout(postName,
						textPaint, 850, Alignment.ALIGN_NORMAL, 1.0f, 0.0f,
						false);
				int maxWidth = -1;
				for (int i = 0; i < mTextLayout.getLineCount(); i++) {
					if (maxWidth < mTextLayout.getLineWidth(i)) {
						maxWidth = (int) mTextLayout.getLineWidth(i);
					}
				}
				final Bitmap bmp = Bitmap.createBitmap(850,
						mTextLayout.getHeight(), Bitmap.Config.ARGB_8888);

				bmp.eraseColor(Color.parseColor("#ffffff"));// just adding white
															// background
				final Canvas canvas = new Canvas(bmp);
				mTextLayout.draw(canvas);
				

				TextPaint textPaint1 = new TextPaint() {
					{
						setColor(Color.parseColor("#000000"));
						setTextAlign(Paint.Align.LEFT);
						setTypeface(Typeface.createFromAsset(
								context.getAssets(), "Fonts/SEGOEUIL.ttf"));
						setTextSize(48f);
						setAntiAlias(true);
					}
				};
				textPaint1.getTextBounds(postMessage, 0, postMessage.length(),
						bounds);
				StaticLayout mTextLayout1 = new StaticLayout(postMessage,
						textPaint1, myBitmap.getWidth(),
						Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
				int maxWidth1 = -1;
				for (int i = 0; i < mTextLayout1.getLineCount(); i++) {
					if (maxWidth1 < mTextLayout1.getLineWidth(i)) {
						maxWidth1 = (int) mTextLayout1.getLineWidth(i);
					}
				}
				final Bitmap bmp1 = Bitmap.createBitmap(850,
						mTextLayout1.getHeight(), Bitmap.Config.ARGB_8888);

				bmp1.eraseColor(Color.parseColor("#ffffff"));
				final Canvas canvas1 = new Canvas(bmp1);
				mTextLayout1.draw(canvas1);
				

				TextPaint textPaint2 = new TextPaint() {
					{
						setColor(Color.parseColor("#837777"));
						setTextAlign(Paint.Align.LEFT);
						setTypeface(Typeface.createFromAsset(
								context.getAssets(), "Fonts/ufonts.com_segoe_ui_semibold.ttf"));
						setTextSize(18f);
						setAntiAlias(true);
					}
				};
				textPaint2.getTextBounds(attendanceLabel, 0, attendanceLabel.length(),
						bounds);
				StaticLayout mTextLayout2 = new StaticLayout(attendanceLabel,
						textPaint2, myBitmap.getWidth(),
						Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
				int maxWidth2 = -1;
				for (int i = 0; i < mTextLayout2.getLineCount(); i++) {
					if (maxWidth2 < mTextLayout2.getLineWidth(i)) {
						maxWidth2 = (int) mTextLayout2.getLineWidth(i);
					}
				}
				final Bitmap bmp2 = Bitmap.createBitmap(850,
						mTextLayout2.getHeight(), Bitmap.Config.ARGB_8888);

				bmp2.eraseColor(Color.parseColor("#ffffff"));
				final Canvas canvas2 = new Canvas(bmp2);
				mTextLayout2.draw(canvas2);
				

				Bitmap bmOverlay = Bitmap.createBitmap(
						850 + 40,
						myBitmap.getHeight() + bmp.getHeight()
								+ bmp1.getHeight() + bmp2.getHeight()+40,
						Bitmap.Config.ARGB_8888);
				Canvas canvasAppend = new Canvas(bmOverlay);
				Paint paint = new Paint();
				paint.setStyle(Paint.Style.FILL);
				paint.setColor(Color.WHITE);
				canvasAppend.drawRect(0, 0, 850 + 40, myBitmap.getHeight()
						+ bmp.getHeight() + bmp1.getHeight()+ bmp2.getHeight() + 40, paint);
				canvasAppend.drawBitmap(myBitmap, 20, 20, null);
				Paint paint1 = new Paint();
				paint1.setColor(Color.LTGRAY);
				paint1.setStrokeWidth(1);
				canvasAppend.drawRect(
						19,
						myBitmap.getHeight() + 20,
						850 + 21,
						myBitmap.getHeight() + bmp.getHeight()
								+ bmp1.getHeight()+bmp2.getHeight() + 21, paint1);
				canvasAppend.drawBitmap(bmp, 20, myBitmap.getHeight() + 20,
						null);
				canvasAppend.drawBitmap(bmp1, 20,
						myBitmap.getHeight() + bmp.getHeight() + 20, null);
				canvasAppend.drawBitmap(bmp2, 20,
						myBitmap.getHeight() + bmp.getHeight()+bmp1.getHeight() + 20, null);
				OutputStream os = null;
				byte[] data = null;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bmp.recycle();
				bmOverlay.compress(CompressFormat.JPEG, 100, baos);
				data = baos.toByteArray();
				
				ByteArrayBody bab = new ByteArrayBody(data, "test.png");
				try {
					
					Session.openActiveSessionWithAccessToken(
							context,
							AccessToken
									.createFromExistingAccessToken(
											params[0].getSelectedFBPage()
													.getAccess_token(),
											new Date(facebook
													.getAccessExpires()),
											new Date(facebook
													.getLastAccessUpdate()),
											AccessTokenSource.FACEBOOK_APPLICATION_NATIVE,
											Arrays.asList("manage_pages",
													"publish_stream",
													"photo_upload")),
							new Session.StatusCallback() {
								@Override
								public void call(Session session,
										SessionState state, Exception exception) {
									// TODO Auto-generated method stub
									if (session != null && session.isOpened()) {
										Session.setActiveSession(session);
										Session session1 = Session
												.getActiveSession();
									}
								}
							});

					
					String url = "https://graph.facebook.com/"
							+ params[0].getSelectedFBPage().getId() + "/photos";
					HttpPost postRequest = new HttpPost(url);
					HttpParams http_parameters = new BasicHttpParams();
					HttpConnectionParams.setConnectionTimeout(http_parameters,
							3000);
					HttpClient httpClient = new DefaultHttpClient();
					MultipartEntity reqEntity = new MultipartEntity(
							HttpMultipartMode.BROWSER_COMPATIBLE);
					reqEntity.addPart("access_token", new StringBody(params[0]
							.getSelectedFBPage().getAccess_token()));
					reqEntity.addPart("picture", bab);
					postRequest.setEntity(reqEntity);
					HttpResponse response1 = httpClient.execute(postRequest);
					if (response1 == null || response1.equals("")
							|| response1.equals("false")) {
					} else {
						returnBool = true;
					}
				} catch (Exception e) {

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return returnBool;
		}

		@Override
		protected void onPostExecute(Boolean result) {
		}
		
		
		public String four_digit(String num)
		{
			int length = num.length();
			switch(length)
			{
			case 1:
				 num = num+"      ";
				 break;
			case 2: 
				  num = num + "    ";
			      break;
			case 3: 
				  num = num + "  ";
				  break;
			}
			
			return num;
		}
		

		public String convertDate(String datestr) {
			String formatedDate = "";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				int hh = Integer.parseInt(datestr.split(",")[1].split(":")[0]);
				DateFormat df = new SimpleDateFormat("dd MMM yyyy");
				if (hh < 13) {
					formatedDate = formatedDate
							+ df.format(sdf.parse(datestr.split(",")[0]))
							+ " ,  " + datestr.split(",")[1] + "  AM";
				} else {
					formatedDate = formatedDate
							+ df.format(sdf.parse(datestr.split(",")[0]))
							+ " ,  " + datestr.split(",")[1] + "  PM";
				}
			} catch (Exception e) {
				e.printStackTrace();

			}
			return formatedDate;
		}

	}
}
