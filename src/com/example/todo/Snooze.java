package com.example.todo;

import java.lang.reflect.Method;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Snooze extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		String detail = intent.getStringExtra("details");

		Intent intent1 = new Intent(context, MyBroadcastReceiver.class);
		intent1.putExtra("details", detail);
		Calendar c = Calendar.getInstance();
		int z = (int) (c.getTimeInMillis() / 10000);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, z,
				intent1, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP,
				c.getTimeInMillis() + 30 * 60000, pendingIntent);
		Toast.makeText(context, "Reminding again in " + 30 + " minutes",
				Toast.LENGTH_LONG).show();

		int notificationId = intent.getIntExtra("notificationId", 0);
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
