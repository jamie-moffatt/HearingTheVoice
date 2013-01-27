package org.hearingthevoice.innerlife;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
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
	
	private Runnable downloadQuestionsThread;
	private Runnable downloadScheduleThread;
	
	private Calendar notificationTime;
	
	private TextView txtQuestionHead;
	private TextView txtQuestionBody;
	private List<Section> sections = new ArrayList<Section>();
	private List<Question> questions = new ArrayList<Question>();
	private Schedule schedule;
	
	private int section = 0;
	private int question = 0;
	
	private String filename;
	
	private RadioGroup rblResponses;
	private Map<Long, Integer> responseIDs;
	
	private Button btnBack;
	private Button btnNext;
	
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
		notificationTime = Calendar.getInstance();
		
		boolean updateQuestions = false;
		boolean updateSchedule = false;
		
		boolean questionsCached = false;
		boolean scheduleCached = false;
		
		String[] files = context.fileList();
		
//		context.deleteFile("questions");
//		context.deleteFile("schedule");
		
		// determine whether the questions are cached in the file system
		for(String file : files)
		{
			if(file.contains("questions"))
			{
				questionsCached = true;
				break;
			}
		}
		
		// determine whether the schedule is cached in the file system
		for(String file : files)
		{
			if(file.contains("schedule"))
			{
				scheduleCached = true;
				break;
			}
		}
		
		if(questionsCached && !updateQuestions)
		{
			try
			{
				Log.d("QUESTIONS", "read from file");
				
				sections = QuestionAPI.retrieveCachedQuestions(context);
				// TODO I think the bug from issue #1 is here
				Log.wtf("ISSUE #1", sections.toString());
				//if(sections != null) questions = sections.get(0).getQuestions();
				schedule = QuestionAPI.retrieveCachedSchedule(context);
				sections = schedule.filterBySession(sections, 0);
				questions = sections.get(0).getQuestions();
			}
			catch (Exception e) { e.printStackTrace(); }
		}
		else {}//downloadQuestions();
		
		if(scheduleCached && !updateSchedule)
		{
			try
			{
				Log.d("SCHEDULE", "read from file");
				
				schedule = QuestionAPI.retrieveCachedSchedule(context);
				
				if(schedule != null)
				{
					Log.d("SCHEDULE", "sessions: " + schedule.numberOfSessions());
					Log.d("SCHEDULE", schedule.toString());
				}
				
				if(schedule != null && sections != null)
				{
					Log.d("SCHEDULE", "sections before: " + sections.size());
					sections = schedule.filterBySession(sections, 0);
					Log.d("SCHEDULE", "sections after: " + sections.size());
					Log.wtf("ISSUE #1", "actual sections: " + sections);
					
				}
			}
			catch (Exception e) { e.printStackTrace(); }
		}
		else {} //downloadSchedule();
		
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
				//Toast.makeText(context, String.format("You chose: %d", rblResponses.getCheckedRadioButtonId()), Toast.LENGTH_SHORT).show();
				if(!questions.isEmpty()) responseIDs.put(questions.get(question).getQuestionID(), rblResponses.getCheckedRadioButtonId());
				
				question--;
				if(question < 0 && !questions.isEmpty())
				{
					if(section > 0) section--;
					questions = sections.get(section).getQuestions();
					question = questions.size() - 1;
				}
				
				if(!questions.isEmpty()) loadQuestion();
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
				//Toast.makeText(context, String.format("You chose: %d", rblResponses.getCheckedRadioButtonId()), Toast.LENGTH_SHORT).show();
				if(!questions.isEmpty()) responseIDs.put(questions.get(question).getQuestionID(), rblResponses.getCheckedRadioButtonId());
				
				question++;
				if(question > questions.size() - 1 && !questions.isEmpty() && !sections.isEmpty())
				{
					if(section < sections.size() - 1) section++;
					questions = sections.get(section).getQuestions();
					question = 0;
				}
				
				if(section == sections.size() - 1 && question == questions.size() - 1) endSession();
				else
				{		
					if(!questions.isEmpty()) loadQuestion();
					else loadPlaceholder();
				}
				
				btnBack.setEnabled(section != 0 || question != 0);
			}
		});
		
		if(questionsCached)
		{
			if(sections != null && sections.size() > 0)
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
			String extension = new SimpleDateFormat("yyyyMMddHHmmss").format(submissionTime.getTime());
			
			AppManager.recordSampleComplete(context, new SimpleDateFormat("yyyy-MM-dd").format(submissionTime.getTime()));
			
			filename = "responses" + extension; 
			FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
	
			StringBuffer str = new StringBuffer();
			
			str.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
			
			str.append("<submission ");
			str.append("userID=\"" + "1" + "\" ");
			str.append("sessionID=\"" + "1" + "\" ");
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			String _notificationTime = format.format(notificationTime.getTime());
			String _submissionTime = format.format(submissionTime.getTime());
			
			str.append("notificationTime=\"" + _notificationTime + "\" ");
			str.append("submissionTime=\"" + _submissionTime + "\">\n");
			
			for(Entry<Long, Integer> e : responseIDs.entrySet())
			{
				str.append("<response ");
				str.append("questionID=\"" + e.getKey() + "\" ");
				str.append("response=\"" + e.getValue() + "\"/>\n");
			}
			
			str.append("</submission>");
			
			fos.write(str.toString().getBytes());
			fos.close();
			
			if(QuestionAPI.networkIsConnected(activity)) (new SubmitTask()).execute(str.toString());
			else Toast.makeText(activity, "No Connection. Saving responses.", Toast.LENGTH_LONG).show();
		}
		catch (IOException e) { e.printStackTrace(); }
	}
	
	private class SubmitTask extends AsyncTask<String, Void, Boolean>
	{	
		@Override
		protected Boolean doInBackground(String... responseXMLRequest)
		{
			try
			{
				InputStream response = QuestionAPI.getHTTPResponseStream(
						"responses.php", "POST", responseXMLRequest[0].getBytes());
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
				Intent i = new Intent(context, DashboardActivity.class);
				// TODO might be better ways of doing this now!!!
				finish();
				startActivity(i);
			}
			else Toast.makeText(activity, "Network Problem. Responses were not submitted.", Toast.LENGTH_LONG).show();

		}
	}
	
	public void populateResponses()
	{
		rblResponses.removeAllViews();
		
		List<String> responses = new ArrayList<String>();
		
		if(sections != null && sections.size() > 0)
			responses = sections.get(section).getResponses();
		
		if(responses.size() > 0)
		{
			for(int i = 0; i < responses.size(); i++)
			{
				RadioButton rb = new RadioButton(context);
				rb.setText(responses.get(i));
				rb.setTextColor(Color.BLACK);
				rb.setId(i);
				rblResponses.addView(rb);
			}
		}
		
		long questionID = questions.get(question).getQuestionID();
		
		if(responseIDs.containsKey(questionID))
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
		txtQuestionHead.setText("Section  " + sections.get(section).getSectionID() +
		                      ", Question " + questions.get(question).getNumber());
		txtQuestionBody.setText(questions.get(question).getDescription());
		populateResponses();		
	}

	private void loadPlaceholder()
	{
		if(sections.isEmpty()) txtQuestionHead.setText("No Section, No Question");
		else txtQuestionHead.setText("Section " + sections.get(section).getSectionID() + ", No Question");
		txtQuestionBody.setText("There are currently no questions in this section");
	}
}
