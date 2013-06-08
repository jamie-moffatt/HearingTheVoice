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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

	private int section;
	private int question;
	private int session = 0;

	private RadioGroup rblResponses;
	private LinearLayout sliderContainer;
	private SeekBar sbrScaleResponse;
	private TextView txtSliderValue;
	
	// stored the values of question responses, mapped  by unique question ID
	private Map<Long, Integer> responseIDs;     // in terms of widget
	private Map<Long, String>  responseStrings; // in terms of natural language
	private Map<Long, String>  responseValues;  // in terms of data entry

	private Button btnBack;
	private Button btnNext;

	AppManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		
		txtQuestionHead = (TextView) findViewById(R.id.txt_question_header);
		txtQuestionBody = (TextView) findViewById(R.id.txt_question_body);
		rblResponses = (RadioGroup) findViewById(R.id.rbl_responses);
		sbrScaleResponse = (SeekBar) findViewById(R.id.sbr_scale_response);
		sliderContainer = (LinearLayout) findViewById(R.id.slider_container);
		txtSliderValue = (TextView) findViewById(R.id.txt_slider_value);

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

		if (!(QuestionAPI.areQuestionsCached(context) && QuestionAPI.isScheduleCached(context)))
		{
			AlertDialog.Builder bd = new AlertDialog.Builder(context);
			bd.setTitle("Failed to Download Questions");
			bd.setMessage("Ensure that you are connected to an Internet connection and then restart the application.");
			bd.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					finish();
				}
			});
			bd.show();
			return;
		}

		try
		{
			Log.d("QUESTIONS", "read from file");
			sections = QuestionAPI.retrieveCachedQuestions(context);

			Log.d("SCHEDULE", "read from file");
			schedule = QuestionAPI.retrieveCachedSchedule(context);

			int samples = AppManager.getPossibleSamplesSoFar(context);
			session = (samples == 0) ? 28 : samples - 1; // if first sample, load trait questions
			if (samples == 14) session = 29; // if half way through samples, load trait questions
			if (samples  > 14) session = samples - 1;
			if (samples == 28) session = 30; // if end of samples, load trait questions

			// TODO this may have been causing last sessions to not be loaded due to trait questions
			// adding extra sessions.
			// if (session > schedule.numberOfSessions() - 1) session %= schedule.numberOfSessions();

			sections = schedule.filterBySession(sections, session);

			manager = AppManager.getInstance();
			manager.setSection(sections);
			
			question = manager.question;
			section = manager.section;
			
			questions = sections.get(section).getQuestions();	
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

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

		// attempt to load stored values from application when screen refreshes
		responseIDs     = (manager.getResponseIDs()     == null) ? new HashMap<Long, Integer>() : manager.getResponseIDs(); // TODO need to check that questions are actually downloaded
		responseStrings = (manager.getResponseStrings() == null) ? new HashMap<Long, String>()  : manager.getResponseStrings();
		responseValues  = (manager.getResponseValues()  == null) ? new HashMap<Long, String>()  : manager.getResponseValues();

		btnBack = (Button) findViewById(R.id.btn_back);
		btnNext = (Button) findViewById(R.id.btn_next);

		btnBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Toast.makeText(context, String.format("You chose: %d",
				// rblResponses.getCheckedRadioButtonId()), Toast.LENGTH_SHORT).show();
				
				if (!questions.isEmpty()) recordResponse();

				question--; // cycle back to previous question
				
				if (question < 0 && !questions.isEmpty())
				{
					// if the question index is < 0, cycle back to previous section
					if (section > 0) section--; // cannot cycle back if on first section
					questions = sections.get(section).getQuestions(); // set questions to those of previous section
					question = questions.size() - 1; // set current question to last question in section upon cycling back
				}

				if (!questions.isEmpty()) loadQuestion();
				else loadPlaceholder();
				
				// if on the first question of the first section, there is no
				// question to go back to so the back button is disabled.
				btnBack.setEnabled(section != 0 || question != 0); // De Morgan's Law
				
				manager.question = question;
				manager.section = section;
			}
		});
		// back button starts as being disabled as activity opens on section 1 (0), question 1 (0)
		btnBack.setEnabled(section != 0 || question != 0);

		btnNext.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Toast.makeText(context, String.format("You chose: %d",
				// rblResponses.getCheckedRadioButtonId()), Toast.LENGTH_SHORT).show();
				
				if (!questions.isEmpty()) recordResponse();

				// upon clicking next on the last question of the last section, end the session
				if (section == sections.size() - 1 && question == questions.size() - 1)
				{
					endSession();
					return;
				}
				
				if (question == questions.size() - 1) // if next is clicked on the last question...
				{
					section++; // move onto next section
					questions = sections.get(section).getQuestions(); // set questions to those of next section //TODO ArrayIndexOutOfBounds
					question = -1; // for post increment below to work correctly
				}
				
				question++; // cycle to next question

				if (!questions.isEmpty()) loadQuestion();
				else loadPlaceholder();

				// re-enable the back button when user proceeds from the first question
				// of the first section.
				btnBack.setEnabled(section != 0 || question != 0); // De Morgan's Law
				
				manager.question = question;
				manager.section = section;
			}
		});

		if (QuestionAPI.areQuestionsCached(context) && QuestionAPI.isScheduleCached(context))
		{
			if (sections != null && sections.size() > 0)
			{
				loadQuestion(); // load first question
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

	/**
	 * This method adds an appropriate widget to the user interface allowing the user
	 * to select an appropriate response to the current question. This widget may vary
	 * depending on the style of question; for example, a seek bar is used for numeral
	 * answers while a group of radio buttons is used if there is a finite number of
	 * natural language responses. 
	 */
	public void populateResponses()
	{
		rblResponses.removeAllViews(); // remove current responses prior to repopulation

		List<Pair<String, String>> responses = new ArrayList<Pair<String, String>>();

		if (sections != null && sections.size() > 0)
		{
			switch (questions.get(question).getType())
			{
				case YESNO:
				{
					rblResponses.setVisibility(View.VISIBLE);
					sliderContainer.setVisibility(View.GONE);
					responses.add(Pair.create("No" , "0"));
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
					rblResponses.addView(rb); // add choice to radio group
				}
			}
		}

		reloadResponse();
	}

	private void reloadResponse()
	{
		long questionID = questions.get(question).getQuestionID();
		// attempt to load 'response widget' with a previously entered response
		// if the user is returning to a particular question / screen.
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

	/**
	 * Based on the current section and question IDs, this method loads the
	 * appropriate question and all of the available answers on screen.
	 */
	private void loadQuestion()
	{
		txtQuestionHead.setText("Section " + sections.get(section).getSectionID()
			+ ", Question " + questions.get(question).getNumber());
		
		if (sections.get(section).getDescription().length() == 0)
		{
			txtQuestionBody.setText(questions.get(question).getDescription());
		}
		else
		{
			txtQuestionBody.setText(Html.fromHtml("<b>" + sections.get(section).getDescription()
				+ "</b><br /><br />" + questions.get(question).getDescription()));
		}
		
		populateResponses();
	}

	/**
	 * This method is called when the appropriate question cannot be loaded
	 * due to an error (e.g. question data cannot be loaded / is null). 
	 */
	private void loadPlaceholder()
	{
		if (sections == null || sections.isEmpty()) txtQuestionHead.setText("No Section, No Question");
		else txtQuestionHead.setText("Section " + sections.get(section).getSectionID()
				+ ", No Question");
		txtQuestionBody.setText("There are currently no questions in this section");
	}

	private void recordResponse()
	{
		long id = questions.get(question).getQuestionID();
		
		if (questions.get(question).getType() == QuestionType.NUMSCALE)
		{
			int response = sbrScaleResponse.getProgress();
			
			responseIDs    .put(id, response);
			responseStrings.put(id, "" + response); // convert to string
			responseValues .put(id, "" + response);
		}
		else
		{
			int responseID = rblResponses.getCheckedRadioButtonId();
			responseIDs.put(id, responseID);
			
			if (responseID > -1) // -1 is used as an invalid ID by the Android API 
			{							
				if (questions.get(question).getType() == QuestionType.YESNO)
				{
					responseValues.put(id, "" + responseID); // yes/no values have direct mapping
				}
				else
				{
					responseValues.put(id, sections.get(section).getResponses().get(responseID).second);
				}
			}
			else responseValues.put(questions.get(question).getQuestionID(), "N/A"); // placeholder for skipped question

			RadioButton selection = (RadioButton) rblResponses.getChildAt(responseID);
			String response = (selection == null) ? "No Response" : selection.getText().toString();
			
			responseStrings.put(id, response);
		}
		
		manager.setResponseIDs(responseIDs);
		manager.setResponseStrings(responseStrings);
		manager.setResponseValues(responseValues);
	}
}
