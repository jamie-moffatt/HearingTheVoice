package org.hearingthevoice.innerlife.ui.activity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hearingthevoice.innerlife.AppManager;
import org.hearingthevoice.innerlife.R;
import org.hearingthevoice.innerlife.io.web.QuestionAPI;
import org.hearingthevoice.innerlife.model.Question;
import org.hearingthevoice.innerlife.model.Schedule;
import org.hearingthevoice.innerlife.model.Section;
import org.hearingthevoice.innerlife.services.BootService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
{
	private Context context;
	private Activity activity;

	private Calendar notificationTime;

	private TextView txtQuestionHead;
	private TextView txtQuestionBody;
	private List<Section> sections = new ArrayList<Section>();
	private List<Question> questions = new ArrayList<Question>();
	private Schedule schedule;

	private int section = 0;
	private int question = 0;
	private int session = 0;

	private String filename;

	private RadioGroup rblResponses;
	private Map<Long, Integer> responseIDs;

	private Button btnBack;
	private Button btnNext;
	private ProgressDialog submissionProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = getApplicationContext();
		activity = this;

		// This starts the background service for the first time and should
		// probably only run the first time the application is started
		Intent startServiceIntent = new Intent(context, BootService.class);
		context.startService(startServiceIntent);

		// The notification time should be taken from the SharedPreferences set by
		// the background service
		String notificationTimeStored = AppManager.getNotificationTime(context);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		notificationTime = Calendar.getInstance();
		
		try
		{
			notificationTime.setTime(sdf.parse(notificationTimeStored));
		}
		catch (ParseException ex)
		{
			ex.printStackTrace();
		}

		boolean updateQuestions = false;
		boolean updateSchedule = false;

		boolean questionsCached = false;
		boolean scheduleCached = false;

		String[] files = context.fileList();

		// determine whether the questions are cached in the file system
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

		try
		{
			Log.d("QUESTIONS", "read from file");
			sections = QuestionAPI.retrieveCachedQuestions(context);

			Log.d("SCHEDULE", "read from file");
			schedule = QuestionAPI.retrieveCachedSchedule(context);

			session = AppManager.getSamplesComplete(context);

			if (session > schedule.numberOfSessions() - 1) session %= schedule.numberOfSessions();

			sections = schedule.filterBySession(sections, session);
			questions = sections.get(0).getQuestions();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		txtQuestionHead = (TextView) findViewById(R.id.txt_question_header);
		txtQuestionBody = (TextView) findViewById(R.id.txt_question_body);

		rblResponses = (RadioGroup) findViewById(R.id.rbl_responses);

		responseIDs = new HashMap<Long, Integer>();

		btnBack = (Button) findViewById(R.id.btn_back);
		btnNext = (Button) findViewById(R.id.btn_next);

		btnBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Toast.makeText(context, String.format("You chose: %d",
				// rblResponses.getCheckedRadioButtonId()), Toast.LENGTH_SHORT).show();
				if (!questions.isEmpty())
					responseIDs.put(questions.get(question).getQuestionID(),
							rblResponses.getCheckedRadioButtonId());

				question--;
				if (question < 0 && !questions.isEmpty())
				{
					if (section > 0) section--;
					questions = sections.get(section).getQuestions();
					question = questions.size() - 1;
				}

				if (!questions.isEmpty()) loadQuestion();
				else loadPlaceholder();

				btnBack.setEnabled(section != 0 || question != 0);
			}
		});

		btnBack.setEnabled(false);

		btnNext.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Toast.makeText(context, String.format("You chose: %d",
				// rblResponses.getCheckedRadioButtonId()), Toast.LENGTH_SHORT).show();
				if (!questions.isEmpty())
					responseIDs.put(questions.get(question).getQuestionID(),
							rblResponses.getCheckedRadioButtonId());

				question++;
				if (question > questions.size() - 1 && !questions.isEmpty() && !sections.isEmpty())
				{
					if (section < sections.size() - 1) section++;
					questions = sections.get(section).getQuestions();
					question = 0;
				}

				if (section == sections.size() - 1 && question == questions.size() - 1)
				{
					submissionProgressDialog = ProgressDialog.show(v.getContext(),
							"Submitting Questions",
							"Submitting your responses. Please wait...", true);
					endSession();
				}
				else
				{
					if (!questions.isEmpty()) loadQuestion();
					else loadPlaceholder();
				}

				btnBack.setEnabled(section != 0 || question != 0);
			}
		});

		if (questionsCached)
		{
			if (sections != null && sections.size() > 0)
			{
				loadQuestion();
			}
			populateResponses();
		}
	}

	public void endSession()
	{
		Log.d("SESSION", "end of session");

		try
		{
			Calendar submissionTime = Calendar.getInstance();
			String extension = new SimpleDateFormat("yyyyMMddHHmmss").format(submissionTime
					.getTime());

			AppManager.recordSampleComplete(context,
					new SimpleDateFormat("yyyy-MM-dd").format(submissionTime.getTime()));

			filename = "responses" + extension;
			FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);

			StringBuffer str = new StringBuffer();

			str.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");

			str.append("<submission ");
			str.append("userID=\"" + AppManager.getUserCode(context) + "\" ");
			str.append("sessionID=\"" + session + "\" ");

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String _notificationTime = format.format(notificationTime.getTime());
			String _submissionTime = format.format(submissionTime.getTime());

			str.append("notificationTime=\"" + _notificationTime + "\" ");
			str.append("submissionTime=\"" + _submissionTime + "\">\n");

			for (Entry<Long, Integer> e : responseIDs.entrySet())
			{
				str.append("<response ");
				str.append("questionID=\"" + e.getKey() + "\" ");
				str.append("response=\"" + e.getValue() + "\"/>\n");
			}

			str.append("</submission>");

			Log.d("SUBMISSION_DATA", str.toString());
			
			fos.write(str.toString().getBytes());
			fos.close();

			if (QuestionAPI.networkIsConnected(activity)) (new SubmitTask())
					.execute(str.toString());
			else
			{
				if (submissionProgressDialog != null && submissionProgressDialog.isShowing())
				{
					submissionProgressDialog.dismiss();
				}
				Toast.makeText(activity, "No Connection. Saving responses.", Toast.LENGTH_LONG)
						.show();
				// TODO might be better ways of doing this now!!!
				finish();
				Intent i = new Intent(context, DashboardActivity.class);
				startActivity(i);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
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
			catch (IOException e1)
			{
				e1.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean postedSuccessfully)
		{
			if (postedSuccessfully)
			{
				Toast.makeText(activity, "Responses submitted.", Toast.LENGTH_LONG).show();
				Log.d("RESPONSES", "deleting file");
				context.deleteFile(filename);
				AppManager.setGotNotification(context, false);
				AppManager.setNotificationTime(context, null);
				if (submissionProgressDialog != null && submissionProgressDialog.isShowing())
				{
					submissionProgressDialog.dismiss();
				}
				// TODO might be better ways of doing this now!!!
				finish();
				Intent i = new Intent(context, DashboardActivity.class);
				startActivity(i);
			}
			else
			{
				Toast.makeText(activity, "Network Problem. Responses were not submitted.",
						Toast.LENGTH_LONG).show();

				if (submissionProgressDialog != null && submissionProgressDialog.isShowing())
				{
					submissionProgressDialog.dismiss();
				}
			}

		}
	}

	public void populateResponses()
	{
		rblResponses.removeAllViews();

		List<String> responses = new ArrayList<String>();

		if (sections != null && sections.size() > 0)
		{
			switch (questions.get(question).getType())
			{
			case YESNO:
			{
				responses.add("No");
				responses.add("Yes");
				break;
			}
			case RADIO:
			{
				responses = sections.get(section).getResponses();
				break;
			}
			case NUMSCALE:
			{
				List<String> minmax = sections.get(section).getResponses();

				responses.add("1 - " + minmax.get(0));
				for (int i = 2; i <= 9; i++)
					responses.add("" + i);
				responses.add("10 - " + minmax.get(1));

				break;
			}
			}

			if (responses.size() > 0)
			{
				for (int i = 0; i < responses.size(); i++)
				{
					RadioButton rb = new RadioButton(context);
					rb.setText(responses.get(i));
					rb.setTextColor(Color.BLACK);
					rb.setId(i);
					rblResponses.addView(rb);
				}
			}
		}

		long questionID = questions.get(question).getQuestionID();

		if (responseIDs.containsKey(questionID))
		{
			Log.d("CHECKBOX", "Recalling: " + responseIDs.get(questionID));
			rblResponses.check(responseIDs.get(questionID));
		}
		else
		{
			Log.d("CHECKBOX", "Clearing");
			rblResponses.clearCheck();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void loadQuestion()
	{
		txtQuestionHead
				.setText("Section  " + sections.get(section).getSectionID() + ", Question " + questions
						.get(question).getNumber());
		txtQuestionBody.setText(questions.get(question).getDescription());
		populateResponses();
	}

	private void loadPlaceholder()
	{
		if (sections.isEmpty()) txtQuestionHead.setText("No Section, No Question");
		else txtQuestionHead
				.setText("Section " + sections.get(section).getSectionID() + ", No Question");
		txtQuestionBody.setText("There are currently no questions in this section");
	}
}
