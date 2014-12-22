package com.wduwg.receiver;

import android.app.KeyguardManager;
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
	        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE); 
	    	final KeyguardManager.KeyguardLock kl = km .newKeyguardLock("MyKeyguardLock"); 
	    	kl.disableKeyguard(); 
	    }
//	    Intent i = new Intent(context, UpdateService.class);
//	    i.putExtra("screen_state", screenOff);
//	    context.startService(i);
	}
}
