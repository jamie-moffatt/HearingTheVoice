package org.hearingthevoice.innerlife.ui.activity;

import org.hearingthevoice.innerlife.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class SummaryActivity extends Activity
{
	private Context context;
	private ListView questionListView;
	private Button btnConfirm;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.summary_layout);
		
		questionListView = (ListView) findViewById(R.id.questionListView);
		btnConfirm = (Button) findViewById(R.id.btnConfirmSubmission);
		
		btnConfirm.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				
			}
		});

		context = this;
	}

}
