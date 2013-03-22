package org.hearingthevoice.innerlife.ui.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.hearingthevoice.innerlife.AppManager;
import org.hearingthevoice.innerlife.R;
import org.hearingthevoice.innerlife.io.web.QuestionAPI;
import org.hearingthevoice.innerlife.model.Schedule;
import org.hearingthevoice.innerlife.model.Section;
import org.hearingthevoice.innerlife.services.BootService;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardActivity extends Activity
{
	private Button btnAnswerTestQuestions;
	private Button btnTestNotification;
	private TextView txtSamplesToday;
	private TextView txtNumResponses;
	private TextView txtResponseTime;
	private TextView txtQuestionsAvailable;

	private ProgressDialog progressDialog;

	private boolean scheduleDownloaded = false;
	private boolean questionsDownloaded = false;

	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);

		context = this;
		btnAnswerTestQuestions = (Button) findViewById(R.id.btnAnswerTestQuestions);
		btnTestNotification = (Button) findViewById(R.id.btnTestNotification);
		txtSamplesToday = (TextView) findViewById(R.id.txtSamplesToday);
		txtNumResponses = (TextView) findViewById(R.id.txtResponses);
		txtResponseTime = (TextView) findViewById(R.id.txtResponseTime);
		txtQuestionsAvailable = (TextView) findViewById(R.id.txtQuestionsAvailable);

		txtQuestionsAvailable.setText("Downloading Questions.");
		txtQuestionsAvailable.setCompoundDrawablesWithIntrinsicBounds(R.drawable.action_download,
				0, 0, 0);

		btnAnswerTestQuestions.setEnabled(false);

		progressDialog = ProgressDialog.show(context, "Downloading Questions",
				"Currently downloading questions. Please wait...", true);

		String[] files = context.fileList();
		boolean questionsCached = false;
		boolean scheduleCached = false;

		for (String file : files)
		{
			if (file.contains("questions"))
			{
				questionsCached = true;
				break;
			}
		}

		// determine whether the schedule is cached in the file system
		for (String file : files)
		{
			if (file.contains("schedule"))
			{
				scheduleCached = true;
				break;
			}
		}

		if (scheduleCached && questionsCached)
		{
			if (progressDialog != null) progressDialog.dismiss();
			if (AppManager.getSamplesCompleteToday(context) < 2
					&& AppManager.getGotNotification(context))
			{
				txtQuestionsAvailable.setText("New Questions Available.");
				txtQuestionsAvailable.setCompoundDrawablesWithIntrinsicBounds(
						R.drawable.action_help, 0, 0, 0);
				btnAnswerTestQuestions.setEnabled(true);
			}
		}

		if (!scheduleCached) (new ScheduleDownloadTask()).execute();
		if (!questionsCached) (new QuestionDownloadTask()).execute();

		int samplesCompletedToday = AppManager.getSamplesCompleteToday(context);
		txtSamplesToday.setText("You have submitted " + samplesCompletedToday + " samples today.");
		if (samplesCompletedToday == 1)
			txtSamplesToday.setText("You have submitted " + samplesCompletedToday
					+ " sample today.");
		if (samplesCompletedToday < 1) txtSamplesToday.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.action_empty_star, 0, 0, 0);
		else if (samplesCompletedToday == 1) txtSamplesToday
				.setCompoundDrawablesWithIntrinsicBounds(R.drawable.action_half_star, 0, 0, 0);
		else txtSamplesToday.setCompoundDrawablesWithIntrinsicBounds(R.drawable.action_full_star,
				0, 0, 0);

		int numResponses = AppManager.getSamplesComplete(context);
		txtNumResponses.setText("You have made " + numResponses + " responses so far.");
		if (numResponses == 1)
			txtNumResponses.setText("You have made " + numResponses + " response so far.");

		String avgResponseTime = AppManager.getAverageResponseTime(context);
		txtResponseTime.setText("Your average response time is " + avgResponseTime + ".");

		if (samplesCompletedToday > 1 && !AppManager.getGotNotification(context))
		{
			txtQuestionsAvailable.setText("Today's Questions Have Been Answered");
			if (!AppManager.getGotNotification(context))
				txtQuestionsAvailable.setText("Wait for the next notification.");
			btnAnswerTestQuestions.setEnabled(false);
		}

		btnAnswerTestQuestions.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(v.getContext(), MainActivity.class);
				startActivity(i);
				finish();
			}
		});

		btnTestNotification.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				NotificationManager nm;
				nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				NotificationCompat.Builder nb = new NotificationCompat.Builder(context);
				nb.setContentTitle("New Questions Available");
				nb.setContentText("Click to participate.");
				nb.setSmallIcon(R.drawable.next_item);

				Intent clickIntent = new Intent(context, DashboardActivity.class);

				// The stack builder object will contain an artificial back stack for the
				// started Activity.
				// This ensures that navigating backward from the Activity leads out of
				// your application to the Home screen.
				TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
				// Adds the back stack for the Intent (but not the Intent itself)
				stackBuilder.addParentStack(DashboardActivity.class);
				// Adds the Intent that starts the Activity to the top of the stack
				stackBuilder.addNextIntent(clickIntent);
				PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
						PendingIntent.FLAG_UPDATE_CURRENT);
				nb.setContentIntent(resultPendingIntent);
				nb.setAutoCancel(true);

				nb.setSound(Uri.parse("content://settings/system/notification_sound"));

				nm.notify(0, nb.build());

				AppManager.setGotNotification(context, true);
				Calendar now = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String notificationTime = sdf.format(now.getTime());
				AppManager.setNotificationTime(context, notificationTime);
				
				int notificationsSoFar = AppManager.getPossibleSamplesSoFar(context);
				AppManager.setPossibleSamplesSoFar(context, notificationsSoFar+1);
			}
		});

		Button btnDisplayStoredData = (Button) findViewById(R.id.btnDisplayStoredData);
		btnDisplayStoredData.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(context, AppManager.getStoredData(context), Toast.LENGTH_LONG)
						.show();
			}
		});

		// Perform first-time run actions
		if (AppManager.isFirstRun(context))
		{
			Intent startServiceIntent = new Intent(context, BootService.class);
			context.startService(startServiceIntent);
			Intent intent = new Intent(context, FirstRunFormActivity.class);
			startActivity(intent);
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_dashboard, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.menu_settings:
			Intent showSettingsIntent = new Intent(this, SettingsActivity.class);
			startActivity(showSettingsIntent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class ScheduleDownloadTask extends AsyncTask<String, Integer, Schedule>
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
			if (scheduleDownloaded && questionsDownloaded)
			{
				if (progressDialog != null) progressDialog.dismiss();
				if (AppManager.getSamplesCompleteToday(context) < 2
						&& AppManager.getGotNotification(context))
				{
					txtQuestionsAvailable.setText("New Questions Available.");
					txtQuestionsAvailable.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.action_help, 0, 0, 0);
					btnAnswerTestQuestions.setEnabled(true);
				}
				else
				{
					txtQuestionsAvailable.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.action_help, 0, 0, 0);
					txtQuestionsAvailable.setText("Today's Questions Have Been Answered");
					if (!AppManager.getGotNotification(context))
						txtQuestionsAvailable.setText("Wait for the next notification.");
					btnAnswerTestQuestions.setEnabled(false);
				}
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
			if (scheduleDownloaded && questionsDownloaded)
			{
				if (progressDialog != null) progressDialog.dismiss();

				if (AppManager.getSamplesCompleteToday(context) < 2
						&& AppManager.getGotNotification(context))
				{
					txtQuestionsAvailable.setText("New Questions Available.");
					txtQuestionsAvailable.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.action_help, 0, 0, 0);
					btnAnswerTestQuestions.setEnabled(true);
				}
				else
				{
					txtQuestionsAvailable.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.action_help, 0, 0, 0);
					txtQuestionsAvailable.setText("Today's Questions Have Been Answered");
					if (!AppManager.getGotNotification(context))
						txtQuestionsAvailable.setText("Wait for the next notification.");
					btnAnswerTestQuestions.setEnabled(false);
				}
			}
		}
	}

}
