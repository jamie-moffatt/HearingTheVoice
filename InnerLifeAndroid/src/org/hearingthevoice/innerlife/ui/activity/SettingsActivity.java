package org.hearingthevoice.innerlife.ui.activity;

import org.hearingthevoice.innerlife.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.os.Bundle;
import android.widget.TimePicker;

public class SettingsActivity extends Activity
{
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);

		context = this;
	}
	
	@Override
	protected Dialog onCreateDialog(int id)
	{
		switch (id)
		{
			case 1:
			{
				int hour = 0;
				int minute = 0;
	
				return new TimePickerDialog(this, timeSetListener, hour, minute, true);
			}
		}
		return null;
	}

	private OnTimeSetListener timeSetListener = new OnTimeSetListener()
	{
		@Override
		public void onTimeSet(TimePicker view, int hour, int minute)
		{	
			
		}
	};
}
