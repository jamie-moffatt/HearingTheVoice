package org.hearingthevoice.innerlife;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardActivity extends Activity
{
	private Button btnAnswerTestQuestions;
	private TextView txtQuestionsAvailable;
	private ProgressBar progDownloadQuestions;
	
	private Runnable downloadQuestionsThread;
	private Runnable downloadScheduleThread;
	
	private Activity activity;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		
		context = getApplicationContext();
		activity = this;
		
		btnAnswerTestQuestions = (Button)findViewById(R.id.btnAnswerTestQuestions);
		txtQuestionsAvailable = (TextView)findViewById(R.id.txtQuestionsAvailable);
		progDownloadQuestions = (ProgressBar)findViewById(R.id.progDownloadQuestions);
		
		txtQuestionsAvailable.setText("Downloading Questions.");
		progDownloadQuestions.setMax(100);
		progDownloadQuestions.setProgress(0);
		
		btnAnswerTestQuestions.setEnabled(false);
		
		downloadSchedule();
		
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
	
	public void downloadQuestions()
	{
		final Runnable callback = getQuestionCallback();
		
		downloadQuestionsThread = new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Log.d("DOWNLOAD", "Downloading questions");
					List<Section> sections = QuestionAPI.downloadQuestionXML(context, "questions.php");
					if(sections != null) activity.runOnUiThread(callback);
					else Toast.makeText(context, String.format("No questions downloaded"), Toast.LENGTH_SHORT).show();	
				}
				catch (Exception e) { e.printStackTrace(); }
			}
		};
		
		Thread thread = new Thread(null, downloadQuestionsThread, "DownloadQuestionsThread");

		thread.start();
		
		Log.d("DOWNLOAD", "complete");
	}

	private Runnable getQuestionCallback()
	{
		Runnable callback = new Runnable()
		{
			@Override
			public void run()
			{
				txtQuestionsAvailable.setText("Questions are available.");
				progDownloadQuestions.setMax(100);
				btnAnswerTestQuestions.setEnabled(true);
			}
		};

		return callback;
	}
	
	public void downloadSchedule()
	{
		final Runnable callback = getScheduleCallback();
		
		downloadScheduleThread = new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Log.d("DOWNLOAD", "Downloading schedule");
					Schedule schedule = QuestionAPI.downloadScheduleXML(context, "schedule.php");
					if(schedule != null) activity.runOnUiThread(callback);
					else Toast.makeText(context, String.format("No schedule downloaded"), Toast.LENGTH_SHORT).show();
				}
				catch (Exception e) { e.printStackTrace(); }
			}
		};
		
		Thread thread = new Thread(null, downloadScheduleThread, "DownloadScheduleThread");

		thread.start();
		
		activity.runOnUiThread(callback);
	}

	private Runnable getScheduleCallback()
	{
		Runnable callback = new Runnable()
		{
			@Override
			public void run()
			{
				downloadQuestions();
			}
		};

		return callback;
	}

}
