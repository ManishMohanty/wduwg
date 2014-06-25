/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * A Derivative Work, changed by Manatee Works, Inc.
 *
 */

package com.manateeworks.cameraDemo;

import com.manateeworks.BarcodeScanner;
import com.manateeworks.camera.CameraManager;
import com.manateeworks.cameraDemo.Intents.Scan;
import com.mw.wduwg.services.GlobalVariable;
import com.wduwg.owner.app.R;
import com.wduwg.owner.app.ScanerDetailActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.ClipboardManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;

/**
 * The barcode reader activity itself. This is loosely based on the
 * CameraPreview example included in the Android SDK.
 */
public final class ActivityCapture extends Activity implements
		SurfaceHolder.Callback {

	public static final boolean PDF_OPTIMIZED = false;

	// !!! Rects are in format: x, y, width, height !!!
	public static final Rect RECT_LANDSCAPE_1D = new Rect(3, 20, 94, 60);
	public static final Rect RECT_LANDSCAPE_2D = new Rect(20, 5, 60, 90);
	public static final Rect RECT_PORTRAIT_1D = new Rect(20, 3, 60, 94);
	public static final Rect RECT_PORTRAIT_2D = new Rect(20, 5, 60, 90);
	public static final Rect RECT_FULL_1D = new Rect(3, 3, 94, 94);
	public static final Rect RECT_FULL_2D = new Rect(20, 5, 60, 90);

	private static final int ABOUT_ID = Menu.FIRST;
	private ActivityCaptureHandler handler;

	private View statusView;
	private View resultView;
	private byte[] lastResult;
	private boolean hasSurface;
	private InactivityTimer inactivityTimer;
	private String versionName;
	public static String lastStringResult;

	public Handler getHandler() {
		return handler;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.capture);

		// register your copy of the mobiScan SDK with the given user name / key
		BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_25,
				"username", "key");
		BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_39,
				"username", "key");
		BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_93,
				"username", "key");
		BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_128,
				"username", "key");
		BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_AZTEC,
				"username", "key");
		BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_DM,
				"username", "key");
		BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_EANUPC,
				"username", "key");
		BarcodeScanner
				.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_PDF,
						"Motifworks.PDF.Android.20140701",
						"EBA344F766788F968C0C98E1130D1F396CD899B1B6EF401D9A7604DBA3EFF555");
		BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_QR,
				"username", "key");
		BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_RSS,
				"username", "key");
		BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_CODABAR,
				"username", "key");

		// choose code type or types you want to search for

		if (PDF_OPTIMIZED) {
			BarcodeScanner
					.MWBsetDirection(BarcodeScanner.MWB_SCANDIRECTION_HORIZONTAL);
			BarcodeScanner.MWBsetActiveCodes(BarcodeScanner.MWB_CODE_MASK_PDF);
			BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_PDF,
					RECT_LANDSCAPE_1D);
		} else {

			// Our sample app is configured by default to search both
			// directions...
			BarcodeScanner
					.MWBsetDirection(BarcodeScanner.MWB_SCANDIRECTION_HORIZONTAL
							| BarcodeScanner.MWB_SCANDIRECTION_VERTICAL);
			// Our sample app is configured by default to search all supported
			// barcodes...
			BarcodeScanner.MWBsetActiveCodes(BarcodeScanner.MWB_CODE_MASK_25
					| BarcodeScanner.MWB_CODE_MASK_39
					| BarcodeScanner.MWB_CODE_MASK_93
					| BarcodeScanner.MWB_CODE_MASK_128
					| BarcodeScanner.MWB_CODE_MASK_AZTEC
					| BarcodeScanner.MWB_CODE_MASK_DM
					| BarcodeScanner.MWB_CODE_MASK_EANUPC
					| BarcodeScanner.MWB_CODE_MASK_PDF
					| BarcodeScanner.MWB_CODE_MASK_QR
					| BarcodeScanner.MWB_CODE_MASK_CODABAR
					| BarcodeScanner.MWB_CODE_MASK_RSS);

			// set the scanning rectangle based on scan direction(format in pct:
			// x, y, width, height)
			BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_25,
					RECT_FULL_1D);
			BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_39,
					RECT_FULL_1D);
			BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_93,
					RECT_FULL_1D);
			BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_128,
					RECT_FULL_1D);
			BarcodeScanner.MWBsetScanningRect(
					BarcodeScanner.MWB_CODE_MASK_AZTEC, RECT_FULL_2D);
			BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_DM,
					RECT_FULL_2D);
			BarcodeScanner.MWBsetScanningRect(
					BarcodeScanner.MWB_CODE_MASK_EANUPC, RECT_FULL_1D);
			BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_PDF,
					RECT_FULL_1D);
			BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_QR,
					RECT_FULL_2D);
			BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_RSS,
					RECT_FULL_1D);
			BarcodeScanner.MWBsetScanningRect(
					BarcodeScanner.MWB_CODE_MASK_CODABAR, RECT_FULL_1D);

		}

		// But for better performance, only activate the symbologies your
		// application requires...
		// BarcodeScanner.MWBsetActiveCodes( BarcodeScanner.MWB_CODE_MASK_25 );
		// BarcodeScanner.MWBsetActiveCodes( BarcodeScanner.MWB_CODE_MASK_39 );
		// BarcodeScanner.MWBsetActiveCodes( BarcodeScanner.MWB_CODE_MASK_93 );
		// BarcodeScanner.MWBsetActiveCodes( BarcodeScanner.MWB_CODE_MASK_128 );
		// BarcodeScanner.MWBsetActiveCodes( BarcodeScanner.MWB_CODE_MASK_AZTEC
		// );
		// BarcodeScanner.MWBsetActiveCodes( BarcodeScanner.MWB_CODE_MASK_DM );
		// BarcodeScanner.MWBsetActiveCodes( BarcodeScanner.MWB_CODE_MASK_EANUPC
		// );
		// BarcodeScanner.MWBsetActiveCodes( BarcodeScanner.MWB_CODE_MASK_PDF );
		// BarcodeScanner.MWBsetActiveCodes( BarcodeScanner.MWB_CODE_MASK_QR );
		// BarcodeScanner.MWBsetActiveCodes( BarcodeScanner.MWB_CODE_MASK_RSS );
		// BarcodeScanner.MWBsetActiveCodes(
		// BarcodeScanner.MWB_CODE_MASK_CODABAR );

		// But for better performance, set like this for PORTRAIT scanning...
		// BarcodeScanner.MWBsetDirection(BarcodeScanner.MWB_SCANDIRECTION_VERTICAL);
		// set the scanning rectangle based on scan direction(format in pct: x,
		// y, width, height)
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_25,
		// RECT_PORTRAIT_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_39,
		// RECT_PORTRAIT_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_93,
		// RECT_PORTRAIT_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_128,
		// RECT_PORTRAIT_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_AZTEC,
		// RECT_PORTRAIT_2D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_DM,
		// RECT_PORTRAIT_2D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_EANUPC,
		// RECT_PORTRAIT_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_PDF,
		// RECT_PORTRAIT_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_QR,
		// RECT_PORTRAIT_2D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_RSS,
		// RECT_PORTRAIT_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_CODABAR,RECT_PORTRAIT_1D);

		// or like this for LANDSCAPE scanning - Preferred for dense or wide
		// codes...
		// BarcodeScanner.MWBsetDirection(BarcodeScanner.MWB_SCANDIRECTION_HORIZONTAL);
		// set the scanning rectangle based on scan direction(format in pct: x,
		// y, width, height)
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_25,
		// RECT_LANDSCAPE_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_39,
		// RECT_LANDSCAPE_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_93,
		// RECT_LANDSCAPE_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_128,
		// RECT_LANDSCAPE_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_AZTEC,
		// RECT_LANDSCAPE_2D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_DM,
		// RECT_LANDSCAPE_2D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_EANUPC,
		// RECT_LANDSCAPE_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_PDF,
		// RECT_LANDSCAPE_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_QR,
		// RECT_LANDSCAPE_2D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_RSS,
		// RECT_LANDSCAPE_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_CODABAR,RECT_LANDSCAPE_1D);

		// set decoder effort level (1 - 5)
		// for live scanning scenarios, a setting between 1 to 3 will suffice
		// levels 4 and 5 are typically reserved for batch scanning
		BarcodeScanner.MWBsetLevel(2);

		CameraManager.init(getApplication());

		handler = null;
		lastResult = null;
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

	}

	@Override
	protected void onResume() {
		super.onResume();

		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);

		MWOverlay.addOverlay(this, surfaceView);

		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			// The activity was paused but not stopped, so the surface still
			// exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(surfaceHolder);
		} else {
			// Install the callback and wait for surfaceCreated() to init the
			// camera.
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		int ver = BarcodeScanner.MWBgetLibVersion();
		int v1 = (ver >> 16);
		int v2 = (ver >> 8) & 0xff;
		int v3 = (ver & 0xff);
		String libVersion = "Lib version: " + String.valueOf(v1) + "."
				+ String.valueOf(v2) + "." + String.valueOf(v3);
		Toast.makeText(this, libVersion, Toast.LENGTH_LONG).show();

	}

	@Override
	protected void onPause() {
		super.onPause();

		MWOverlay.removeOverlay();

		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_HOME) {
			if (lastResult != null) {

				if (handler != null) {
					handler.sendEmptyMessage(R.id.restart_preview);
				}
				return true;
			}
		} else if (keyCode == KeyEvent.KEYCODE_FOCUS
				|| keyCode == KeyEvent.KEYCODE_CAMERA) {
			// Handle these events so they don't launch the Camera app
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, ABOUT_ID, 0, R.string.menu_about).setIcon(
				android.R.drawable.ic_menu_info_details);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case ABOUT_ID:
			PackageInfo info = null;
			try {
				info = getPackageManager().getPackageInfo(getPackageName(), 0);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.versionName = info.versionName;

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.title_about));
			builder.setMessage(getString(R.string.msg_about));
			builder.setIcon(R.drawable.ic_launcher);
			builder.setPositiveButton(R.string.button_open_license,
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri
									.parse(getString(R.string.license_url)));
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
							startActivity(intent);
							finish();

						}
					});
			builder.setNeutralButton(R.string.button_open_mobi,
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri
									.parse(getString(R.string.mobi_url)));
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
							startActivity(intent);
							finish();

						}
					});
			builder.setNegativeButton(R.string.button_cancel, null);
			builder.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		// Do nothing, this is to prevent the activity from being restarted when
		// the keyboard opens.
		super.onConfigurationChanged(config);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	/**
	 * A valid barcode has been found, so give an indication of success and show
	 * the results.
	 * 
	 * @param rawResult
	 *            The contents of the barcode.
	 */

	public void handleDecode(byte[] rawResult) {
		inactivityTimer.onActivity();
		lastResult = rawResult;

		String s = "";

		try {
			s = new String(rawResult, "UTF-8");
		} catch (UnsupportedEncodingException e) {

			s = "";
			for (int i = 0; i < rawResult.length; i++)
				s = s + (char) rawResult[i];
			e.printStackTrace();
		}

		int bcType = BarcodeScanner.MWBgetLastType();
		String typeName = "";
		switch (bcType) {
		case BarcodeScanner.FOUND_25_INTERLEAVED:
			typeName = "Code 25";
			break;
		case BarcodeScanner.FOUND_25_STANDARD:
			typeName = "Code 25 Standard";
			break;
		case BarcodeScanner.FOUND_128:
			typeName = "Code 128";
			break;
		case BarcodeScanner.FOUND_39:
			typeName = "Code 39";
			break;
		case BarcodeScanner.FOUND_93:
			typeName = "Code 93";
			break;
		case BarcodeScanner.FOUND_AZTEC:
			typeName = "AZTEC";
			break;
		case BarcodeScanner.FOUND_DM:
			typeName = "Datamatrix";
			break;
		case BarcodeScanner.FOUND_EAN_13:
			typeName = "EAN 13";
			break;
		case BarcodeScanner.FOUND_EAN_8:
			typeName = "EAN 8";
			break;
		case BarcodeScanner.FOUND_NONE:
			typeName = "None";
			break;
		case BarcodeScanner.FOUND_RSS_14:
			typeName = "Databar 14";
			break;
		case BarcodeScanner.FOUND_RSS_14_STACK:
			typeName = "Databar 14 Stacked";
			break;
		case BarcodeScanner.FOUND_RSS_EXP:
			typeName = "Databar Expanded";
			break;
		case BarcodeScanner.FOUND_RSS_LIM:
			typeName = "Databar Limited";
			break;
		case BarcodeScanner.FOUND_UPC_A:
			typeName = "UPC A";
			break;
		case BarcodeScanner.FOUND_UPC_E:
			typeName = "UPC E";
			break;
		case BarcodeScanner.FOUND_PDF:
			typeName = "PDF417";
			break;
		case BarcodeScanner.FOUND_QR:
			typeName = "QR";
			break;
		case BarcodeScanner.FOUND_CODABAR:
			typeName = "Codabar";
			break;
		}
		if (bcType >= 0)

		// new AlertDialog.Builder(this)
		// .setTitle(typeName)
		// .setMessage(s)
		// .setNegativeButton("Close", new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int which) {
		// if (handler != null)
		// {
		// lastResult = null;
		// handler.sendEmptyMessage(R.id.restart_preview);
		// }
		//
		// }
		// })
		// .show();
		{
			Intent intent1 = new Intent(ActivityCapture.this,
					ScanerDetailActivity.class);
			intent1.putExtra("rawData", s);
			// startActivity(intent1);
			startActivityForResult(intent1, 1);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK ) {
			System.out.println("**************************************************" + (requestCode == 1));
			this.setResult(RESULT_OK);
			finish();
		}

	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			// Select desired camera resoloution. Not all devices supports all
			// resolutions, closest available will be chosen
			// If not selected, closest match to screen resolution will be
			// chosen
			// High resolutions will slow down scanning proccess on slower
			// devices

			if (PDF_OPTIMIZED) {
				CameraManager.setDesiredPreviewSize(1280, 720);
			} else {
				CameraManager.setDesiredPreviewSize(800, 480);
			}

			CameraManager
					.get()
					.openDriver(
							surfaceHolder,
							(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT));
		} catch (IOException ioe) {
			displayFrameworkBugMessageAndExit();
			return;
		} catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			displayFrameworkBugMessageAndExit();
			return;
		}
		if (handler == null) {
			handler = new ActivityCaptureHandler(this);
		}
	}

	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton(R.string.button_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						finish();
					}
				});
		builder.show();
	}

	@Override
	public void onBackPressed() {
		this.setResult(RESULT_OK);
		finish();
	}

}
