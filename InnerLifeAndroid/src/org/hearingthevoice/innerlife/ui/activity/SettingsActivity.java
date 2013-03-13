package org.hearingthevoice.innerlife.ui.activity;

import org.hearingthevoice.innerlife.AppManager;
import org.hearingthevoice.innerlife.R;
import org.hearingthevoice.innerlife.ui.view.TimeSelector;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SettingsActivity extends Activity
{
	private Context context;
	private TextView txtTimeRangeDescription;

	private int mAMTime;
	private int mPMTime;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);

		context = this;

		txtTimeRangeDescription = (TextView) findViewById(R.id.txtSettingsTimeRangeDescription);

		mAMTime = AppManager.getAMNotificationTime(context);
		mPMTime = AppManager.getPMNotificationTime(context);

		TimeSelector timeSelector = (TimeSelector) findViewById(R.id.timeSelector);
		timeSelector.setAMTime(mAMTime);
		timeSelector.setPMTime(mPMTime);
		txtTimeRangeDescription.setText(String.format(
				"You will be notified between %d:00 - %d:00 and %d:00 - %d:00.", mAMTime,
				(mAMTime + 3) % 24, mPMTime, (mPMTime + 3) % 24));
		timeSelector.setOnTimeChangedListener(new TimeSelector.OnTimeChangedListener()
		{
			@Override
			public void onTimeChanged(int amTime, int pmTime)
			{
				txtTimeRangeDescription.setText(String.format(
						"You will be notified between %d:00 - %d:00 and %d:00 - %d:00.", amTime,
						(amTime + 3) % 24, pmTime, (pmTime + 3) % 24));
				mAMTime = amTime;
				mPMTime = pmTime;
				AppManager.setAMNotificationTime(context, amTime);
				AppManager.setPMNotificationTime(context, pmTime);
			}
		});

		// TODO store new notification times in the shared preferences and get the boot service to
		// use them
	}

	public void applyChanges(View v)
	{
		AppManager.setAMNotificationTime(context, mAMTime);
		AppManager.setPMNotificationTime(context, mPMTime);
		finish();
	}
}
