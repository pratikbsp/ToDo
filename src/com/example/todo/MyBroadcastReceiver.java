package com.example.todo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

public class MyBroadcastReceiver extends BroadcastReceiver {
	
	
	SharedPreferences data;
	int i=0;
	@Override
	public void onReceive(Context context, Intent intent) {
		/**
		 * Toast.makeText(context, "Time is up!!!!.", Toast.LENGTH_LONG).show();
		 * // Vibrate the mobile phone Vibrator vibrator = (Vibrator) context
		 * .getSystemService(Context.VIBRATOR_SERVICE); vibrator.vibrate(2000);
		 **/

		String detail = intent.getStringExtra("details");
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.customnotification);
 
		
		
		remoteViews.setTextViewText(R.id.message, detail);
		//data = getSharedPreferences("newPreference",Context.MODE_PRIVATE);
		Uri alarmSound = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context)
				// Set Icon
				.setSmallIcon(R.drawable.plusgreen)
				// Set Ticker Message
				.setTicker("ticker")

				.setSound(alarmSound).setLights(Color.BLUE, 1000, 500)
				.setVibrate(new long[] { 1000, 1000 })
				// Set Title
				//.setContentTitle("new Notification")
				// Set Text
				//.setContentText("new message")
				.setContent(remoteViews)
				// Add an Action Button below Notification
				// .addAction(R.drawable.ic_launcher, "Action Button", pIntent)
				// Set PendingIntent into Notification
				// .setContentIntent(pIntent)
				// Dismiss Notification
				.setAutoCancel(false);
		
		Intent buttonIntent = new Intent(context, ButtonReceiver.class);
		Intent buttonIntent2 = new Intent(context, Snooze.class);
		

		int notificationId = (int) (System.currentTimeMillis()%10000);
		buttonIntent.putExtra("notificationId",notificationId);
		buttonIntent2.putExtra("notificationId",notificationId);
		buttonIntent2.putExtra("details",detail);
		
		
		PendingIntent btPendingIntent = PendingIntent.getBroadcast(context, notificationId, buttonIntent,0);
		PendingIntent btPendingIntent2 = PendingIntent.getBroadcast(context, notificationId, buttonIntent2,0);
		remoteViews.setOnClickPendingIntent(R.id.button1,btPendingIntent );
		remoteViews.setOnClickPendingIntent(R.id.button2,btPendingIntent2 );
		
		NotificationManager notificationmanager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// Build Notification with Notification Manager
		notificationmanager.notify(notificationId, builder.build());
		i++;

	}
}