package org.hearingthevoice.innerlife;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver
{
	// This function is called when an alarm is received
	@Override
	public void onReceive(Context context, Intent intent)
	{
		NotificationManager nm;
		nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder nb = new NotificationCompat.Builder(context);
		nb.setContentTitle("New Questions Available");
		nb.setContentText("Click to participate.");
		nb.setSmallIcon(R.drawable.next_item);
		nm.notify(0, nb.build());
	}
}