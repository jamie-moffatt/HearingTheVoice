package org.hearingthevoice.innerlife.ui.activity;

import org.hearingthevoice.innerlife.AppManager;
import org.hearingthevoice.innerlife.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EndActivity extends Activity
{
	private Context context;
	
	private Button btnCloseApp;

	private TextView txtResponseTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_end);

		context = this;
		
		txtResponseTime = (TextView) findViewById(R.id.txtResponseTimeEnd);

		btnCloseApp = (Button) findViewById(R.id.btnCloseApp);
		
		String avgResponseTime = AppManager.getAverageResponseTime(context);
		txtResponseTime.setText("Your average response time was " + avgResponseTime + ".");
		
		btnCloseApp.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
	}
}
