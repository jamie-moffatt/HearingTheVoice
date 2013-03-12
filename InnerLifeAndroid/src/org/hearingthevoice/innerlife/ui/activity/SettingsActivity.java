package org.hearingthevoice.innerlife.ui.activity;

import org.hearingthevoice.innerlife.R;
import org.hearingthevoice.innerlife.ui.view.TimeSelector;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.TimePicker;

public class SettingsActivity extends Activity
{
	private Context context;
	private TextView txtTimeRangeDescription;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);

		txtTimeRangeDescription = (TextView) findViewById(R.id.txtSettingsTimeRangeDescription);

		TimeSelector timeSelector = (TimeSelector) findViewById(R.id.timeSelector);
		timeSelector.setOnTimeChangedListener(new TimeSelector.OnTimeChangedListener()
		{
			@Override
			public void onTimeChanged(int amTime, int pmTime)
			{
				txtTimeRangeDescription.setText(String.format(
						"You will be notified between %d:00 - %d:00 and %d:00 - %d:00.", amTime,
						(amTime + 3) % 24, pmTime, (pmTime + 3) % 24));
			}
		});

		//TODO store new notification times in the shared preferences and get the boot service to use them
		
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
