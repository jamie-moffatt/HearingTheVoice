package org.hearingthevoice.innerlife.services;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver
{
	// This method gets called at boot time
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Intent startServiceIntent = new Intent(context, BootService.class);
        context.startService(startServiceIntent);
	}
}
