package com.wduwg.receiver;

import com.mw.wduwg.services.UpdateService;
import com.wduwg.watch.app.CountActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {

	private boolean screenOff;
	
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
	        screenOff = true;
	    } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
	        screenOff = false;
	    }
	    Intent i = new Intent(context, UpdateService.class);
	    i.putExtra("screen_state", screenOff);
	    context.startService(i);
	}
}
