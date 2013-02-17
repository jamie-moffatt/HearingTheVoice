package org.hearingthevoice.innerlife.ui.activity;

import org.hearingthevoice.innerlife.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class SummaryActivity extends Activity
{
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_run_form);

		context = this;
	}

}
