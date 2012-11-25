package org.hearingthevoice.innerlife;


import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class BootReceiver extends BroadcastReceiver
{
	// This methods gets called at boot time
	@Override
	public void onReceive(Context context, Intent intent)
	{
		NotificationManager nm;
		nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder nb = new NotificationCompat.Builder(context);
		nb.setContentInfo("BootReceiver received boot message");
		nb.setContentText("BootReceiver received boot message");
		nb.setContentTitle("BOOT");
		nb.setSmallIcon(R.drawable.ic_action_search);
		nm.notify(0, nb.build());
		Intent startServiceIntent = new Intent(context, BootService.class);
        context.startService(startServiceIntent);
	}
}
