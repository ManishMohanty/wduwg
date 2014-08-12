package com.wduwg.receiver;

import com.wduwg.watch.app.CountActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent intent1 = new Intent(context,CountActivity.class);
		intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent1);
	}
}
