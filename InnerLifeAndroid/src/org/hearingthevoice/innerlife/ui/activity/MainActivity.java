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
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
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
	private LinearLayout sliderContainer;
	private SeekBar sbrScaleResponse;
	private TextView txtSliderValue;
	private Map<Long, Integer> responseIDs;
	private Map<Long, String> responseStrings;
	private Map<Long, String> responseValues;

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

		if (notificationTimeStored != null)
		{
			try
			{
				notificationTime.setTime(sdf.parse(notificationTimeStored));
			}
			catch (ParseException ex)
			{
				ex.printStackTrace();
			}
		}

		boolean questionsCached = false;

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

		try
		{
			Log.d("QUESTIONS", "read from file");
			sections = QuestionAPI.retrieveCachedQuestions(context);

			Log.d("SCHEDULE", "read from file");
			schedule = QuestionAPI.retrieveCachedSchedule(context);

			int samples = AppManager.getPossibleSamplesSoFar(context);
			session = (samples == 0) ? 28 : samples - 1;
			if (samples == 14) session = 29;
			if (samples > 14) session = samples - 1;
			if (samples == 28) session = 30;

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
		sliderContainer = (LinearLayout) findViewById(R.id.slider_container);
		txtSliderValue = (TextView) findViewById(R.id.txt_slider_value);

		sbrScaleResponse.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				txtSliderValue.setText("" + (progress + 1));
			}
		});

		responseIDs = new HashMap<Long, Integer>();
		responseStrings = new HashMap<Long, String>();
		responseValues = new HashMap<Long, String>();

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
					if (questions.get(question).getType() == QuestionType.NUMSCALE)
					{
						responseIDs.put(questions.get(question).getQuestionID(),
								sbrScaleResponse.getProgress());
						responseStrings.put(questions.get(question).getQuestionID(), ""
								+ sbrScaleResponse.getProgress());
						responseValues.put(questions.get(question).getQuestionID(), ""
								+ +sbrScaleResponse.getProgress());
					}
					else
					{
						responseIDs.put(questions.get(question).getQuestionID(),
								rblResponses.getCheckedRadioButtonId());
						if (rblResponses.getCheckedRadioButtonId() > 0)
						{
							responseValues.put(
									questions.get(question).getQuestionID(),
									sections.get(section).getResponses()
											.get(rblResponses.getCheckedRadioButtonId()).second);
						}
						else
						{
							responseValues.put(questions.get(question).getQuestionID(), "N/A");
						}

						RadioButton selection = (RadioButton) rblResponses.getChildAt(rblResponses
								.getCheckedRadioButtonId());
						String response = selection == null ? "No Response" : selection.getText()
								.toString();
						responseStrings.put(questions.get(question).getQuestionID(), response);
					}
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
						responseIDs.put(questions.get(question).getQuestionID(),
								sbrScaleResponse.getProgress());
						responseStrings.put(questions.get(question).getQuestionID(), ""
								+ sbrScaleResponse.getProgress());
						responseValues.put(questions.get(question).getQuestionID(), ""
								+ +sbrScaleResponse.getProgress());
					}
					else
					{
						responseIDs.put(questions.get(question).getQuestionID(),
								rblResponses.getCheckedRadioButtonId());
						if (rblResponses.getCheckedRadioButtonId() > 0)
						{							
							if (questions.get(question).getType() == QuestionType.YESNO)
							{
								responseValues.put(
										questions.get(question).getQuestionID(),
										""+rblResponses.getCheckedRadioButtonId());
							}
							else
							{
								responseValues.put(
										questions.get(question).getQuestionID(),
										sections.get(section).getResponses()
												.get(rblResponses.getCheckedRadioButtonId()).second);
							}
						}
						else
						{
							responseValues.put(questions.get(question).getQuestionID(), "N/A");
						}

						RadioButton selection = (RadioButton) rblResponses.getChildAt(rblResponses
								.getCheckedRadioButtonId());
						String response = selection == null ? "No Response" : selection.getText()
								.toString();
						responseStrings.put(questions.get(question).getQuestionID(), response);
					}
				}

				if (sections.isEmpty() || questions.isEmpty())
				{
					loadPlaceholder();
				}

				if (section == sections.size() - 1 && question == questions.size() - 1)
				{
					endSession();
				}

				if (question == questions.size() - 1)
				{
					if (section < sections.size() - 1) section++;
					questions = sections.get(section).getQuestions();
					question = -1;
				}

				question++;
				loadQuestion();

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
		manager.setResponseValues(responseValues);

		Intent i = new Intent(context, SummaryActivity.class);
		startActivity(i);
		finish();
	}

	public void populateResponses()
	{
		rblResponses.removeAllViews();

		List<Pair<String, String>> responses = new ArrayList<Pair<String, String>>();

		if (sections != null && sections.size() > 0)
		{
			switch (questions.get(question).getType())
			{
			case YESNO:
			{
				rblResponses.setVisibility(View.VISIBLE);
				sliderContainer.setVisibility(View.GONE);
				responses.add(Pair.create("No", "0"));
				responses.add(Pair.create("Yes", "1"));
				break;
			}
			case RADIO:
			{
				rblResponses.setVisibility(View.VISIBLE);
				sliderContainer.setVisibility(View.GONE);
				responses = sections.get(section).getResponses();
				break;
			}
			case NUMSCALE:
			{
				// TODO add labels for low and high values
				List<Pair<String, String>> minmax = sections.get(section).getResponses();

				rblResponses.setVisibility(View.GONE);
				sliderContainer.setVisibility(View.VISIBLE);
				String minDescription = minmax.get(0).first;
				String maxDescription = minmax.get(1).first;

				sbrScaleResponse.setMax(9);

				break;
			}
			}

			if (responses.size() > 0)
			{
				for (int i = 0; i < responses.size(); i++)
				{
					RadioButton rb = new RadioButton(context);
					rb.setText(responses.get(i).first);
					rb.setTextColor(Color.BLACK);
					rb.setId(i);
					rblResponses.addView(rb);
				}
			}
		}

		long questionID = questions.get(question).getQuestionID();

		if (responseIDs.containsKey(questionID))
		{
			rblResponses.check(responseIDs.get(questionID));
			if (sbrScaleResponse != null
					&& questions.get(question).getType() == QuestionType.NUMSCALE)
			{
				sbrScaleResponse.setProgress(responseIDs.get(questionID));
			}
		}
		else
		{
			rblResponses.clearCheck();
			sbrScaleResponse.setProgress(0);
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
		txtQuestionBody.setText(Html.fromHtml("<b>" + sections.get(section).getDescription()
				+ "</b><br /><br />" + questions.get(question).getDescription()));
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
