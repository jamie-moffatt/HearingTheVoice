package org.hearingthevoice.innerlife.services;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.hearingthevoice.innerlife.AppManager;
import org.hearingthevoice.innerlife.R;
import org.hearingthevoice.innerlife.ui.activity.DashboardActivity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class AlarmReceiver extends BroadcastReceiver
{
	// This function is called when an alarm is received
	@Override
	public void onReceive(Context context, Intent intent)
	{
		// TODO check whether an addition notification needs to be created
		if (!AppManager.getStopNotifications(context))
		{
			NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			NotificationCompat.Builder nb = new NotificationCompat.Builder(context);
			nb.setContentTitle("New Questions Available");
			nb.setContentText("Click to participate. " + intent.getStringExtra("NOTIFICATION_TYPE"));
			nb.setSmallIcon(R.drawable.next_item);

			Intent clickIntent = new Intent(context, DashboardActivity.class);

			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
			stackBuilder.addParentStack(DashboardActivity.class);
			stackBuilder.addNextIntent(clickIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
			
			nb.setContentIntent(resultPendingIntent);
			nb.setAutoCancel(true);
			nb.setSound(Uri.parse("content://settings/system/notification_sound"));

			if (intent.getStringExtra("NOTIFICATION_TYPE").equals("AM")) nm.notify(0, nb.build());
			else if ((intent.getStringExtra("NOTIFICATION_TYPE").equals("PM"))) nm.notify(1, nb.build());

			AppManager.setGotNotification(context, true);
			Calendar now = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
			String notificationTime = sdf.format(now.getTime());
			AppManager.setNotificationTime(context, notificationTime);

			int notificationsSoFar = AppManager.getDebugSessionID(context);
			AppManager.setDebugSessionID(context, notificationsSoFar + 1);
		}
	}
}