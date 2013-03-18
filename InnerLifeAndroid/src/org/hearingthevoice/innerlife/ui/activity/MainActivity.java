package org.hearingthevoice.innerlife.ui.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hearingthevoice.innerlife.AppManager;
import org.hearingthevoice.innerlife.R;
import org.hearingthevoice.innerlife.io.web.QuestionAPI;
import org.hearingthevoice.innerlife.model.Question;
import org.hearingthevoice.innerlife.model.Question.QuestionType;
import org.hearingthevoice.innerlife.model.Schedule;
import org.hearingthevoice.innerlife.model.Section;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity
{
	private Context context;
	private Calendar notificationTime;

	private TextView txtQuestionHead;
	private TextView txtQuestionBody;
	private List<Section> sections = new ArrayList<Section>();
	private List<Question> questions = new ArrayList<Question>();
	private Schedule schedule;

	private int section = 0;
	private int question = 0;
	private int session = 0;

	private RadioGroup rblResponses;
	private SeekBar sbrScaleResponse;
	private Map<Long, Integer> responseIDs;
	private Map<Long, String> responseStrings;

	private Button btnBack;
	private Button btnNext;

	AppManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;

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

			manager = AppManager.getInstance();
			manager.setSection(sections);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		txtQuestionHead = (TextView) findViewById(R.id.txt_question_header);
		txtQuestionBody = (TextView) findViewById(R.id.txt_question_body);

		rblResponses = (RadioGroup) findViewById(R.id.rbl_responses);
		sbrScaleResponse = (SeekBar) findViewById(R.id.sbr_scale_response);

		responseIDs = new HashMap<Long, Integer>();
		responseStrings = new HashMap<Long, String>();

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
				{
					responseIDs.put(questions.get(question).getQuestionID(),
							rblResponses.getCheckedRadioButtonId());

					RadioButton selection = (RadioButton) rblResponses.getChildAt(rblResponses
							.getCheckedRadioButtonId());
					String response = selection == null ? "No Response" : selection.getText()
							.toString();
					responseStrings.put(questions.get(question).getQuestionID(), response);
				}

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
				{
					if (questions.get(question).getType() == QuestionType.NUMSCALE)
					{
						responseIDs.put(questions.get(question).getQuestionID(), sbrScaleResponse.getProgress());
						responseStrings.put(questions.get(question).getQuestionID(), ""+sbrScaleResponse.getProgress());
					}
					else
					{
						responseIDs.put(questions.get(question).getQuestionID(),
								rblResponses.getCheckedRadioButtonId());

						RadioButton selection = (RadioButton) rblResponses.getChildAt(rblResponses
								.getCheckedRadioButtonId());
						String response = selection == null ? "No Response" : selection.getText()
								.toString();
						responseStrings.put(questions.get(question).getQuestionID(), response);
					}
				}

				question++;
				if (question > questions.size() - 1 && !questions.isEmpty() && !sections.isEmpty())
				{
					if (section < sections.size() - 1) section++;
					questions = sections.get(section).getQuestions();
					question = 0;
				}

				if (section == sections.size() - 1 && question == questions.size() - 1)
				{
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

		manager.setResponseIDs(responseIDs);
		manager.setResponseStrings(responseStrings);

		Intent i = new Intent(context, SummaryActivity.class);
		startActivity(i);
		finish();
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
				rblResponses.setVisibility(View.VISIBLE);
				sbrScaleResponse.setVisibility(View.GONE);
				responses.add("No");
				responses.add("Yes");
				break;
			}
			case RADIO:
			{
				rblResponses.setVisibility(View.VISIBLE);
				sbrScaleResponse.setVisibility(View.GONE);
				responses = sections.get(section).getResponses();
				break;
			}
			case NUMSCALE:
			{
				List<String> minmax = sections.get(section).getResponses();
				//
				// responses.add("1 - " + minmax.get(0));
				// for (int i = 2; i <= 9; i++)
				// responses.add("" + i);
				// responses.add("10 - " + minmax.get(1));

				rblResponses.setVisibility(View.GONE);
				sbrScaleResponse.setVisibility(View.VISIBLE);
				int min = Integer.parseInt(minmax.get(0));
				int max = Integer.parseInt(minmax.get(1));

				sbrScaleResponse.setMax(max);

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
		txtQuestionHead.setText("Section  " + sections.get(section).getSectionID() + ", Question "
				+ questions.get(question).getNumber());
		txtQuestionBody.setText(questions.get(question).getDescription());
		populateResponses();
	}

	private void loadPlaceholder()
	{
		if (sections.isEmpty()) txtQuestionHead.setText("No Section, No Question");
		else txtQuestionHead.setText("Section " + sections.get(section).getSectionID()
				+ ", No Question");
		txtQuestionBody.setText("There are currently no questions in this section");
	}
}
