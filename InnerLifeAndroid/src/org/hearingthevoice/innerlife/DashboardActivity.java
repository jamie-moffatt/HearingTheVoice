package org.hearingthevoice.innerlife;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DashboardActivity extends Activity
{
	private Button btnAnswerTestQuestions;
	private TextView txtQuestionsAvailable;
	private ProgressBar progDownloadQuestions;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		
		btnAnswerTestQuestions = (Button)findViewById(R.id.btnAnswerTestQuestions);
		txtQuestionsAvailable = (TextView)findViewById(R.id.txtQuestionsAvailable);
		progDownloadQuestions = (ProgressBar)findViewById(R.id.progDownloadQuestions);
		
		txtQuestionsAvailable.setText("Downloading Questions.");
		progDownloadQuestions.setMax(100);
		progDownloadQuestions.setProgress(0);
		
		try {Thread.sleep(2000);} catch (Exception e) {}
		
		progDownloadQuestions.setProgress(100);
		txtQuestionsAvailable.setText("There are questions available.");
		
		btnAnswerTestQuestions.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(v.getContext(), MainActivity.class);
				startActivity(i);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_dashboard, menu);
		return true;
	}

}
