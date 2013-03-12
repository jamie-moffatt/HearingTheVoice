package org.hearingthevoice.innerlife.ui.activity;

import org.hearingthevoice.innerlife.R;
import org.hearingthevoice.innerlife.ui.view.TimeSelector;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TimePicker;

public class SettingsActivity extends Activity
{
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);

		TimeSelector timeSelector = (TimeSelector) findViewById(R.id.timeSelector);
		timeSelector.setOnTimeChangedListener(new TimeSelector.OnTimeChangedListener()
		{	
			@Override
			public void onTimeChanged(int amTime, int pmTime)
			{
				Log.d("TIME_CHANGED", String.format("AM: %d\nPM: %d", amTime, pmTime));
			}
		});
		
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
