package org.hearingthevoice.innerlife;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class BootService extends Service
{
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startID)
	{
		final Context context = this;

		NotificationManager nm;
		nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder nb = new NotificationCompat.Builder(context);
		nb.setContentInfo("BootService started.");
		nb.setContentText("BootService started.");
		nb.setContentTitle("BOOT SERVICE");
		nb.setSmallIcon(R.drawable.next_item);
		nm.notify(0, nb.build());
		
		Calendar cal = Calendar.getInstance();
		
		cal.add(Calendar.MINUTE, 5);
		
		// In reality, you would want to have a static variable for the request code instead of
		// 192837
		PendingIntent sender = PendingIntent.getBroadcast(context, 192837, new Intent(context,
				AlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);

		// Get the AlarmManager service
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
		return START_STICKY;
	}

}
