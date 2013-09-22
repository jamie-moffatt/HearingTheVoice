package org.hearingthevoice.innerlife.ui.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hearingthevoice.innerlife.AppManager;
import org.hearingthevoice.innerlife.Common;
import org.hearingthevoice.innerlife.R;
import org.hearingthevoice.innerlife.TimeUtils;
import org.hearingthevoice.innerlife.io.web.QuestionAPI;
import org.hearingthevoice.innerlife.model.Schedule;
import org.hearingthevoice.innerlife.model.Section;
import org.hearingthevoice.innerlife.services.BootService;
import org.hearingthevoice.innerlife.ui.view.SegmentedProgressBar;

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

import com.testflightapp.lib.TestFlight;

public class DashboardActivity extends Activity
{
	private Button btnAnswerTestQuestions;
	private Button btnTestNotification;
	private Button btnOpenDebugMenu;
	private TextView txtSamplesToday;
	private TextView txtNumResponses;
	private TextView txtResponseTime;
	private TextView txtQuestionsAvailable;
	private SegmentedProgressBar sessionProgressBar;

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
		btnOpenDebugMenu = (Button) findViewById(R.id.btnDisplayStoredData);
		txtSamplesToday = (TextView) findViewById(R.id.txtSamplesToday);
		txtNumResponses = (TextView) findViewById(R.id.txtResponses);
		txtResponseTime = (TextView) findViewById(R.id.txtResponseTime);
		txtQuestionsAvailable = (TextView) findViewById(R.id.txtQuestionsAvailable);
		sessionProgressBar = (SegmentedProgressBar) findViewById(R.id.sessionProgressBar);

		txtQuestionsAvailable.setCompoundDrawablesWithIntrinsicBounds(R.drawable.action_download, 0, 0, 0);

		btnAnswerTestQuestions.setEnabled(false);

		if (!Common.DEBUG)
		{
			btnTestNotification.setVisibility(View.GONE);
			btnOpenDebugMenu.setVisibility(View.GONE);
		}

		boolean questionsCached = areQuestionsCached();
		boolean scheduleCached = isScheduleCached();

		if (scheduleCached && questionsCached)
		{
			if (Common.DEBUG)
			{
				txtQuestionsAvailable.setText("New Questions Available.");
				txtQuestionsAvailable.setCompoundDrawablesWithIntrinsicBounds(R.drawable.action_help, 0, 0, 0);
				btnAnswerTestQuestions.setEnabled(true);
				btnAnswerTestQuestions.setText("Answer Session: " + AppManager.getDebugSessionID(context));
			}
			else
			{
				if (AppManager.getSamplesCompleteToday(context) < 2 && AppManager.getGotNotification(context))
				{
					txtQuestionsAvailable.setText("New Questions Available.");
					txtQuestionsAvailable.setCompoundDrawablesWithIntrinsicBounds(R.drawable.action_help, 0, 0, 0);
					btnAnswerTestQuestions.setEnabled(true);
				}
			}
		}

		if (!scheduleCached || !questionsCached)
		{
			progressDialog = ProgressDialog.show(context, "Downloading Questions",
					"Currently downloading questions. Please wait...", true);
		}

		if (!scheduleCached)
		{
			(new ScheduleDownloadTask()).execute();
		}
		if (!questionsCached)
		{
			txtQuestionsAvailable.setText("Downloading Questions.");
			(new QuestionDownloadTask()).execute();
		}

		int samplesCompletedToday = AppManager.getSamplesCompleteToday(context);
		txtSamplesToday.setText("You have submitted " + samplesCompletedToday + " samples today.");
		if (samplesCompletedToday == 1) txtSamplesToday.setText("You have submitted " + samplesCompletedToday
				+ " sample today.");
		if (samplesCompletedToday < 1) txtSamplesToday.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.action_empty_star, 0, 0, 0);
		else if (samplesCompletedToday == 1) txtSamplesToday.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.action_half_star, 0, 0, 0);
		else txtSamplesToday.setCompoundDrawablesWithIntrinsicBounds(R.drawable.action_full_star, 0, 0, 0);

		int numResponses = AppManager.getSamplesComplete(context);
		txtNumResponses.setText("You have made " + numResponses + " responses so far.");
		if (numResponses == 1) txtNumResponses.setText("You have made " + numResponses + " response so far.");

		String avgResponseTime = AppManager.getAverageResponseTime(context);
		txtResponseTime.setText("Your average response time is " + avgResponseTime + ".");

		if (!Common.DEBUG)
		{
			if (samplesCompletedToday > 1 && !AppManager.getGotNotification(context))
			{
				txtQuestionsAvailable.setText("Today's Questions Have Been Answered");
				if (!AppManager.getGotNotification(context)) txtQuestionsAvailable
						.setText("Wait for the next notification.");
				btnAnswerTestQuestions.setEnabled(false);
			}
		}

		// Perform first-time run actions
		if (AppManager.isFirstRun(context))
		{
			TestFlight.passCheckpoint("Set Start Time");
			AppManager.setStartDate(context, Calendar.getInstance(Locale.UK).getTime());
			Intent startServiceIntent = new Intent(context, BootService.class);
			context.startService(startServiceIntent);
			Intent intent = new Intent(context, FirstRunFormActivity.class);
			startActivity(intent);
			finish();
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		int session = TimeUtils.getSessionIDBasedOnTime(AppManager.getStartDate(context));
		Map<Integer, Boolean> sessionsCompleted = AppManager.getSessionsCompleted(context);
		Map<Integer, Boolean> sessionsSubmitted = AppManager.getSessionsSubmitted(context);
		List<Integer> segmentMap = new ArrayList<Integer>();
		for (int i = 1; i <= 28; i++)
		{
			if (i < session) segmentMap.add(SegmentedProgressBar.MISSED);
			else segmentMap.add(SegmentedProgressBar.EMPTY);
		}
		for (Integer i : sessionsCompleted.keySet())
		{
			if (sessionsCompleted.get(i) != null && sessionsCompleted.get(i)) segmentMap.set(i-1, SegmentedProgressBar.COMPLETED_BUT_UNSYNCED);
		}
		for (Integer i : sessionsSubmitted.keySet())
		{
			if (sessionsSubmitted.get(i) != null && sessionsSubmitted.get(i)) segmentMap.set(i-1, SegmentedProgressBar.SUBMITTED);
		}
		sessionProgressBar.setSegmentMap(segmentMap);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (progressDialog != null && progressDialog.isShowing())
		{
			progressDialog.cancel();
		}

	}

	private boolean areQuestionsCached()
	{
		String[] files = context.fileList();
		boolean questionsCached = false;

		for (String file : files)
		{
			if (file.contains("questions"))
			{
				questionsCached = true;
				break;
			}
		}
		return questionsCached;
	}

	private boolean isScheduleCached()
	{
		String[] files = context.fileList();
		boolean scheduleCached = false;

		for (String file : files)
		{
			if (file.contains("schedule"))
			{
				scheduleCached = true;
				break;
			}
		}
		return scheduleCached;
	}

	// =============================================================================================
	// Menu Creation
	// =============================================================================================

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
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

	// =============================================================================================
	// Button Callbacks
	// =============================================================================================

	public void answerQuestions__(View v)
	{
		Intent i = new Intent(context, MainActivity.class);
		if (Common.DEBUG)
		{
			i.putExtra("sessionID", AppManager.getDebugSessionID(context));
		}
		else
		{
			i.putExtra("sessionID", TimeUtils.getSessionIDBasedOnTime(AppManager.getStartDate(context)));
		}
		startActivity(i);
		finish();
	}

	public void testNotification__(View v)
	{
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder nb = new NotificationCompat.Builder(context);
		nb.setContentTitle("New Questions Available");
		nb.setContentText("Click to participate.");
		nb.setSmallIcon(R.drawable.next_item);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(DashboardActivity.class);
		stackBuilder.addNextIntent(new Intent(context, DashboardActivity.class));
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		nb.setContentIntent(resultPendingIntent);
		nb.setAutoCancel(true);
		nb.setSound(Uri.parse("content://settings/system/notification_sound"));
		nm.notify(0, nb.build());

		AppManager.setGotNotification(context, true);
		AppManager.setNotificationTime(context, TimeUtils.serializeTime(Calendar.getInstance(Locale.UK).getTime()));

		int sessionID = AppManager.getDebugSessionID(context);
		AppManager.setDebugSessionID(context, sessionID + 1);
	}

	public void debugMenu__(View v)
	{
		startActivity(new Intent(context, DebugActivity.class));
	}

	// =============================================================================================
	// Asynchronous Tasks
	// =============================================================================================

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
				if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
				if (AppManager.getSamplesCompleteToday(context) < 2 && AppManager.getGotNotification(context))
				{
					txtQuestionsAvailable.setText("New Questions Available.");
					txtQuestionsAvailable.setCompoundDrawablesWithIntrinsicBounds(R.drawable.action_help, 0, 0, 0);
					btnAnswerTestQuestions.setEnabled(true);
				}
				else
				{
					txtQuestionsAvailable.setCompoundDrawablesWithIntrinsicBounds(R.drawable.action_help, 0, 0, 0);
					txtQuestionsAvailable.setText("Today's Questions Have Been Answered");
					if (!AppManager.getGotNotification(context)) txtQuestionsAvailable
							.setText("Wait for the next notification.");
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
				if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();

				if (AppManager.getSamplesCompleteToday(context) < 2 && AppManager.getGotNotification(context))
				{
					txtQuestionsAvailable.setText("New Questions Available.");
					txtQuestionsAvailable.setCompoundDrawablesWithIntrinsicBounds(R.drawable.action_help, 0, 0, 0);
					btnAnswerTestQuestions.setEnabled(true);
				}
				else
				{
					txtQuestionsAvailable.setCompoundDrawablesWithIntrinsicBounds(R.drawable.action_help, 0, 0, 0);
					txtQuestionsAvailable.setText("Today's Questions Have Been Answered");
					if (!AppManager.getGotNotification(context)) txtQuestionsAvailable
							.setText("Wait for the next notification.");
					btnAnswerTestQuestions.setEnabled(false);
				}
			}
		}
	}

}
