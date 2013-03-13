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

		// TODO determine whether the app is done with and stop doing things

		NotificationManager nm;
		nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder nb = new NotificationCompat.Builder(context);
		nb.setContentInfo("BootService started.");
		nb.setContentText("BootService started.");
		nb.setContentTitle("BOOT SERVICE");
		nb.setSmallIcon(R.drawable.next_item);
		// nm.notify(784, nb.build());

		// Get the AlarmManager service
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

		Calendar now = Calendar.getInstance();

		if (now.get(Calendar.HOUR_OF_DAY) < 10)
		{
			Calendar amSession = Calendar.getInstance();
			amSession.set(Calendar.HOUR_OF_DAY, 10); // 10
			amSession.set(Calendar.MINUTE, 0); // 0
			amSession.set(Calendar.SECOND, 0);

			Intent amIntent = new Intent(context, AlarmReceiver.class);
			amIntent.putExtra("NOTIFICATION_TYPE", "AM");
			PendingIntent amSender = PendingIntent.getBroadcast(context, 117, amIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			am.set(AlarmManager.RTC_WAKEUP, amSession.getTimeInMillis(), amSender);
		}

		if (now.get(Calendar.HOUR_OF_DAY) < 17)
		{
			Calendar pmSession = Calendar.getInstance();
			pmSession.set(Calendar.HOUR_OF_DAY, 17); // 17
			pmSession.set(Calendar.MINUTE, 0); // 0
			pmSession.set(Calendar.SECOND, 0);

			Intent pmIntent = new Intent(context, AlarmReceiver.class);
			pmIntent.putExtra("NOTIFICATION_TYPE", "PM");
			PendingIntent pmSender = PendingIntent.getBroadcast(context, 118, pmIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			am.set(AlarmManager.RTC_WAKEUP, pmSession.getTimeInMillis(), pmSender);
		}

		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DAY_OF_YEAR, 1);
		tomorrow.set(Calendar.HOUR_OF_DAY, 0);
		tomorrow.set(Calendar.MINUTE, 0);
		tomorrow.set(Calendar.SECOND, 1);

		am.set(AlarmManager.RTC, tomorrow.getTimeInMillis(), PendingIntent.getBroadcast(context,
				1234, new Intent(context, BootReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT));

		return Service.START_NOT_STICKY;
	}

}
