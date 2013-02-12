package org.hearingthevoice.innerlife.services;

import java.util.Calendar;

import org.hearingthevoice.innerlife.R;

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
//		nm.notify(0, nb.build());
		
		Calendar amSession = Calendar.getInstance();
		amSession.set(Calendar.HOUR_OF_DAY, 22); // 10
		amSession.set(Calendar.MINUTE, 30); // 0
		amSession.set(Calendar.SECOND, 0);
		
		Calendar pmSession = Calendar.getInstance();
		pmSession.set(Calendar.HOUR_OF_DAY, 22); // 17
		pmSession.set(Calendar.MINUTE, 40); // 0
		pmSession.set(Calendar.SECOND, 0);
		
		// In reality, you would want to have a static variable for the request code instead of
		// 192837
		PendingIntent sender = PendingIntent.getBroadcast(context, 192837, new Intent(context,
				AlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);

		// Get the AlarmManager service
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, amSession.getTimeInMillis(), sender);
		am.set(AlarmManager.RTC_WAKEUP, pmSession.getTimeInMillis(), sender);
		return START_STICKY;
	}

}
