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
//		amSession.set(Calendar.HOUR_OF_DAY, 10); // 10
		amSession.set(Calendar.MINUTE, 0); // 0
		amSession.set(Calendar.SECOND, amSession.get(Calendar.SECOND)+10);
		
		Calendar pmSession = Calendar.getInstance();
		pmSession.set(Calendar.HOUR_OF_DAY, 20); // 17
		pmSession.set(Calendar.MINUTE, 3); // 0
		pmSession.set(Calendar.SECOND, 0);
		
		Intent amIntent = new Intent(context, AlarmReceiver.class);
		amIntent.putExtra("NOTIFICATION_TYPE", "AM");
		PendingIntent amSender = PendingIntent.getBroadcast(context, 117, amIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Intent pmIntent = new Intent(context, AlarmReceiver.class);
		pmIntent.putExtra("NOTIFICATION_TYPE", "PM");
		PendingIntent pmSender = PendingIntent.getBroadcast(context, 118, pmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// Get the AlarmManager service
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, amSession.getTimeInMillis(), amSender);
		am.set(AlarmManager.RTC_WAKEUP, pmSession.getTimeInMillis(), pmSender);
		return START_STICKY;
	}

}
