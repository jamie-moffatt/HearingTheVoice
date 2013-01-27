package org.hearingthevoice.innerlife;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardActivity extends Activity
{
	private Button btnAnswerTestQuestions;
	private TextView txtSamplesToday;
	private TextView txtNumResponses;
	private TextView txtResponseTime;
	private TextView txtQuestionsAvailable;
	private ProgressBar progDownloadQuestions;
	
	private boolean scheduleDownloaded = false;
	private boolean questionsDownloaded = false;

	private Activity activity;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);

		context = this;
		activity = this;

		btnAnswerTestQuestions = (Button) findViewById(R.id.btnAnswerTestQuestions);
		txtSamplesToday = (TextView) findViewById(R.id.txtSamplesToday);
		txtNumResponses = (TextView) findViewById(R.id.txtResponses);
		txtResponseTime = (TextView) findViewById(R.id.txtResponseTime);
		txtQuestionsAvailable = (TextView) findViewById(R.id.txtQuestionsAvailable);
		progDownloadQuestions = (ProgressBar) findViewById(R.id.progDownloadQuestions);
		
		txtQuestionsAvailable.setText("Downloading Questions.");
		txtQuestionsAvailable.setCompoundDrawablesWithIntrinsicBounds(R.drawable.action_download, 0, 0, 0);
		progDownloadQuestions.setMax(100);
		progDownloadQuestions.setProgress(0);

		btnAnswerTestQuestions.setEnabled(false);

		(new ScheduleDownloadTask()).execute();
		(new QuestionDownloadTask()).execute();
		
		int samplesCompletedToday = AppManager.getSamplesCompleteToday(context);
		txtSamplesToday.setText("You have submitted " + samplesCompletedToday + " samples today.");
		if (samplesCompletedToday == 1) txtSamplesToday.setText("You have submitted " + samplesCompletedToday + " sample today.");
		if (samplesCompletedToday < 1) txtSamplesToday.setCompoundDrawablesWithIntrinsicBounds(R.drawable.action_empty_star, 0, 0, 0);
		else if (samplesCompletedToday == 1) txtSamplesToday.setCompoundDrawablesWithIntrinsicBounds(R.drawable.action_half_star, 0, 0, 0);
		else txtSamplesToday.setCompoundDrawablesWithIntrinsicBounds(R.drawable.action_full_star, 0, 0, 0);

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

	private class ScheduleDownloadTask extends AsyncTask<String, Void, Schedule>
	{
		// The slow code that runs in the background
		@Override
		protected Schedule doInBackground(String... params)
		{
			try
			{
				Schedule schedule = QuestionAPI.downloadScheduleXML(context, "schedule.php");
				scheduleDownloaded = true;
				return schedule;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return null;
			}
		}

		// Run the result on the UI thread
		@Override
		protected void onPostExecute(Schedule schedule)
		{
			Toast.makeText(context, "Schedule Downloaded.", Toast.LENGTH_LONG).show();
			if (scheduleDownloaded && questionsDownloaded)
			{
				progDownloadQuestions.setProgress(100);
				txtQuestionsAvailable.setText("New Questions Available.");
				txtQuestionsAvailable.setCompoundDrawablesWithIntrinsicBounds(R.drawable.action_help, 0, 0, 0);
				btnAnswerTestQuestions.setEnabled(true);
			}
		}
	}
	
	private class QuestionDownloadTask extends AsyncTask<String, Void, List<Section>>
	{
		// The slow code that runs in the background
		@Override
		protected List<Section> doInBackground(String... params)
		{
			try
			{
				List<Section> sections = QuestionAPI.downloadQuestionXML(context, "questions.php");
				questionsDownloaded = true;
				return sections;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return null;
			}
		}

		// Run the result on the UI thread
		@Override
		protected void onPostExecute(List<Section> sections)
		{
			Toast.makeText(context, "Questions Downloaded.", Toast.LENGTH_LONG).show();
			if (scheduleDownloaded && questionsDownloaded)
			{
				progDownloadQuestions.setProgress(100);
				txtQuestionsAvailable.setText("New Questions Available.");
				txtQuestionsAvailable.setCompoundDrawablesWithIntrinsicBounds(R.drawable.action_help, 0, 0, 0);
				btnAnswerTestQuestions.setEnabled(true);
			}
		}
	}

}
