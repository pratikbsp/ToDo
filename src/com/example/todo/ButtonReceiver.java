package com.example.todo;

import java.lang.reflect.Method;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

public class ButtonReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		int notificationId = intent.getIntExtra("notificationId", 0);

		/**
		 * NotificationCompat.Builder builder = new NotificationCompat.Builder(
		 * context) // Set Icon .setSmallIcon(R.drawable.plusgreen) // Set
		 * Ticker Message .setTicker("ticker")
		 * 
		 * // .setSound(alarmSound).setLights(Color.BLUE, 1000, 500)
		 * .setVibrate(new long[] { 1000, 1000 }) // Set Title
		 * .setContentTitle("new Notification") // Set Text
		 * .setContentText("new message") //.setContent(remoteViews) // Add an
		 * Action Button below Notification //
		 * .addAction(R.drawable.ic_launcher, "Action Button", pIntent) // Set
		 * PendingIntent into Notification // .setContentIntent(pIntent) //
		 * Dismiss Notification .setAutoCancel(true);
		 **/

		// if you want cancel notification
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// manager.notify(1, builder.build());
		manager.cancel(notificationId);
		Object sbservice = context.getSystemService("statusbar");
		Class<?> statusbarManager;
		try {
			statusbarManager = Class.forName("android.app.StatusBarManager");
			Method hidesb = statusbarManager.getMethod("collapse");
			hidesb.invoke(sbservice);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
