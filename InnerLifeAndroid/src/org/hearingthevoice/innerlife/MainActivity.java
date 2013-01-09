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
		
		Intent startServiceIntent = new Intent(context, BootService.class);
        context.startService(startServiceIntent);
		
		notificationTime = Calendar.getInstance();
		
		boolean updateQuestions = true;
		boolean updateSchedule = false;
		
		boolean questionsCached = false;
		boolean scheduleCached = false;
		
		String[] files = context.fileList();
		
//		context.deleteFile("questions");
//		context.deleteFile("schedule");
		
		for(String file : files)
		{
			if(file.contains("questions"))
			{
				questionsCached = true;
				break;
			}
		}
		
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
				if(sections != null) questions = sections.get(0).getQuestions();
			}
			catch (Exception e) { e.printStackTrace(); }
		}
		else downloadQuestions();
		
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
					
				}
			}
			catch (Exception e) { e.printStackTrace(); }
		}
		else downloadSchedule();
		
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
				Toast.makeText(context, String.format("You chose: %d", rblResponses.getCheckedRadioButtonId()), Toast.LENGTH_SHORT).show();
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
			}
		});
		
		btnNext.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(context, String.format("You chose: %d", rblResponses.getCheckedRadioButtonId()), Toast.LENGTH_SHORT).show();
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
			FileOutputStream fos = context.openFileOutput("responses", Context.MODE_PRIVATE);
	
			StringBuffer str = new StringBuffer();
			
			str.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
			
			str.append("<submission ");
			str.append("userID=\"" + "1" + "\" ");
			str.append("sessionID=\"" + "1" + "\" ");
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar submissionTime = Calendar.getInstance();
			
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
			
			(new SubmitTask()).execute(str.toString());
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
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean postedSuccessfully)
		{
			if (postedSuccessfully) Toast.makeText(activity, "Responses submitted.", Toast.LENGTH_LONG).show();
			else Toast.makeText(activity, "Network Problem. Responses were not submitted.", Toast.LENGTH_LONG).show();

		}
	}
	
	public void populateResponses()
	{
		rblResponses.removeAllViews();
		rblResponses.clearCheck();
		
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_main, menu);
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
					sections = QuestionAPI.downloadQuestionXML(context, "questions.php");
					if(sections != null) questions = sections.get(0).getQuestions();
					
					activity.runOnUiThread(callback);
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
				if(sections != null && sections.size() > 0)
				{
					if(questions.isEmpty()) loadPlaceholder();
					else
					{
						loadQuestion();
						populateResponses();
					}
				}
			}
		};

		return callback;
	}
	
	public void downloadSchedule()
	{
		Runnable callback = getScheduleCallback();
		
		downloadScheduleThread = new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Log.d("DOWNLOAD", "Downloading schedule");
					schedule = QuestionAPI.downloadScheduleXML(context, "schedule.php");
					if(sections != null) sections = schedule.filterBySession(sections, 0);
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

			}
		};

		return callback;
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
