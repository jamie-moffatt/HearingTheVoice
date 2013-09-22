package org.hearingthevoice.innerlife.ui.activity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.hearingthevoice.innerlife.AppManager;
import org.hearingthevoice.innerlife.R;
import org.hearingthevoice.innerlife.io.web.QuestionAPI;
import org.hearingthevoice.innerlife.model.Question;
import org.hearingthevoice.innerlife.model.Section;
import org.hearingthevoice.innerlife.ui.adapter.QuestionListAdapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class SummaryActivity extends Activity
{
	private Context context;
	private Activity activity;
	private AppManager manager;

	private String filename;

	private ListView questionListView;
	private Button btnConfirm;

	private ProgressDialog submissionProgressDialog;
	private int sessionID;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.summary_layout);

		context = this;
		activity = this;

		Bundle e = getIntent().getExtras();
		sessionID = e.getInt("sessionID");

		manager = AppManager.getInstance();

		List<Section> session = manager.getSection();
		List<Question> questions = new ArrayList<Question>();

		for (Section section : session)
		{
			for (Question q : section.getQuestions())
			{
				questions.add(q);
			}
		}

		questionListView = (ListView) findViewById(R.id.questionListView);
		questionListView.setAdapter(new QuestionListAdapter(context, R.layout.question_list_row_layout, questions,
				manager.getResponseStrings()));

		btnConfirm = (Button) findViewById(R.id.btnConfirmSubmission);

		btnConfirm.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				submissionProgressDialog = ProgressDialog.show(v.getContext(), "Submitting Questions",
						"Submitting your responses. Please wait...", true);

				confirmSubmission();
			}
		});
	}

	public void confirmSubmission()
	{
		try
		{
			String notificationTimeStored = AppManager.getNotificationTime(context);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
			Calendar notificationTime = Calendar.getInstance();

			try
			{
				if (notificationTimeStored != null) notificationTime.setTime(sdf.parse(notificationTimeStored));
			}
			catch (ParseException ex)
			{
				ex.printStackTrace();
			}

			Calendar submissionTime = Calendar.getInstance();
			String extension = new SimpleDateFormat("yyyyMMddHHmmss", Locale.UK).format(submissionTime.getTime());

			if (sessionID < Section.START_TRAIT_SESSION_ID) AppManager.recordSampleComplete(context,
					new SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(submissionTime.getTime()));

			filename = extension + " responses";
			FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);

			StringBuffer str = new StringBuffer();

			str.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

			str.append("<submission ");
			str.append("userID=\"" + AppManager.getUserID(context) + "\" ");
			str.append("sessionID=\"" + (sessionID) + "\" ");

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);

			String _notificationTime = format.format(notificationTime.getTime());
			String _submissionTime = format.format(submissionTime.getTime());

			str.append("notificationTime=\"" + _notificationTime + "\" ");
			str.append("submissionTime=\"" + _submissionTime + "\">\n");

			for (Entry<Long, String> e : manager.getResponseValues().entrySet())
			{
				str.append("<response ");
				str.append("questionID=\"" + e.getKey() + "\" ");
				str.append("response=\"" + e.getValue() + "\"/>\n");
			}

			str.append("</submission>");

			Log.d("SUBMISSION_DATA", str.toString());

			fos.write(str.toString().getBytes());
			fos.close();

			Map<Integer, Boolean> sessionsCompleted = AppManager.getSessionsCompleted(context);
			sessionsCompleted.put(sessionID, true);
			AppManager.setSessionsCompleted(context, sessionsCompleted);

			AppManager.updateAverageResponseTime(context, _notificationTime, _submissionTime);

			if (QuestionAPI.networkIsConnected(activity))
			{
				(new SubmitTask()).execute(str.toString());
			}
			else
			{
				if (submissionProgressDialog != null && submissionProgressDialog.isShowing())
				{
					submissionProgressDialog.dismiss();
				}
				Toast.makeText(activity, "No Connection. Saving responses.", Toast.LENGTH_LONG).show();
				commonEndBehaviour();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * This runs regardless of whether the submission is successful or not
	 */
	private void commonEndBehaviour()
	{
		if (submissionProgressDialog != null && submissionProgressDialog.isShowing())
		{
			submissionProgressDialog.dismiss();
		}

		AppManager.setGotNotification(context, false);
		AppManager.setNotificationTime(context, null);

		manager.clearSample();

		if (sessionID == 14)
		{
			Intent i = new Intent(context, MainActivity.class);
			i.putExtra("sessionID", Section.MIDDLE_TRAIT_SESSION_ID);
			startActivity(i);
			finish();
		}
		else if (sessionID == 28)
		{
			Intent i = new Intent(context, MainActivity.class);
			i.putExtra("sessionID", Section.END_TRAIT_SESSION_ID);
			startActivity(i);
			finish();
		}
		else if (sessionID == Section.END_TRAIT_SESSION_ID)
		{
			AppManager.setStopNotifications(context, true);
			Intent i = new Intent(context, EndActivity.class);
			startActivity(i);
			finish();
		}
		else
		{
			Intent i = new Intent(context, DashboardActivity.class);
			startActivity(i);
			finish();
		}
	}

	private class SubmitTask extends AsyncTask<String, Void, Boolean>
	{
		@Override
		protected Boolean doInBackground(String... responseXMLRequest)
		{
			try
			{
				InputStream response = QuestionAPI.getHTTPResponseStream("responses.php", "POST",
						responseXMLRequest[0].getBytes());
				return true;

			}
			catch (IOException ex)
			{
				ex.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean postedSuccessfully)
		{
			if (postedSuccessfully)
			{
				Map<Integer, Boolean> sessionsSubmitted = AppManager.getSessionsSubmitted(context);
				sessionsSubmitted.put(sessionID, true);
				AppManager.setSessionsSubmitted(context, sessionsSubmitted);

				Toast.makeText(activity, "Responses submitted.", Toast.LENGTH_LONG).show();
				commonEndBehaviour();
			}
			else
			{
				Map<Integer, Boolean> sessionsSubmitted = AppManager.getSessionsSubmitted(context);
				sessionsSubmitted.put(sessionID, false);
				AppManager.setSessionsSubmitted(context, sessionsSubmitted);

				Toast.makeText(activity, "Network Problem. Responses were not submitted.", Toast.LENGTH_LONG).show();

				if (submissionProgressDialog != null && submissionProgressDialog.isShowing())
				{
					submissionProgressDialog.dismiss();
				}
			}

		}
	}

}
