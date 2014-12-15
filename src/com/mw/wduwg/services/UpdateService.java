package com.mw.wduwg.services;


import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.wduwg.receiver.MyReceiver;


public class UpdateService extends Service {

    BroadcastReceiver mReceiver;

@Override
public void onCreate() {
    super.onCreate();
    // register receiver that handles screen on and screen off logic
    IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
    filter.addAction(Intent.ACTION_SCREEN_OFF);
    mReceiver = new MyReceiver();
    registerReceiver(mReceiver, filter);
}

@Override
public void onDestroy() {

    unregisterReceiver(mReceiver);
    Log.i("onDestroy Reciever", "Called");

    super.onDestroy();
}

@SuppressWarnings("deprecation")
@Override
public void onStart(Intent intent, int startId) {
    boolean screenOn = intent.getBooleanExtra("screen_state", false);
    if (!screenOn) {
    	KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE); 
    	final KeyguardManager.KeyguardLock kl = km .newKeyguardLock("MyKeyguardLock"); 
    	kl.disableKeyguard(); 

    	PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE); 
    	WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
    	                                 | PowerManager.ACQUIRE_CAUSES_WAKEUP
    	                                 | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");
    	wakeLock.acquire();
    } else {
        Log.i("screenOFF", "Called");
    }
}

@Override
public IBinder onBind(Intent intent) {
    // TODO Auto-generated method stub
    return null;
}
}
