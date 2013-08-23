package org.hearingthevoice.innerlife.ui.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Scanner;

import org.hearingthevoice.innerlife.AppManager;
import org.hearingthevoice.innerlife.R;
import org.hearingthevoice.innerlife.io.web.QuestionAPI;
import org.hearingthevoice.innerlife.model.Section;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FirstRunFormActivity extends Activity
{
	private Context context;

	private TextView txtUserIDHint;
	private EditText edtUserCode;
	private EditText edtUserAge;
	private Spinner spnGender;
	private Button btnSubmitUserDetails;

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_run_form);

		context = this;

		txtUserIDHint = (TextView) findViewById(R.id.txt_user_id_hint);
		edtUserCode = (EditText) findViewById(R.id.edtUserCode);
		edtUserAge = (EditText) findViewById(R.id.edtUserAge);
		spnGender = (Spinner) findViewById(R.id.spnGender);
		btnSubmitUserDetails = (Button) findViewById(R.id.btnSubmitUserDetails);

		// Initialize the spinner with Male and Female
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
				R.array.gender_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnGender.setAdapter(adapter);

		// TODO needs to prevent users from submitting or navigating back if
		// there's missing data

		btnSubmitUserDetails.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String userCode = edtUserCode.getText().toString();
				String userAge = edtUserAge.getText().toString();
				String userGender = "";
				int selectedRow = (int) spnGender.getSelectedItemId();
				switch (selectedRow)
				{
				case 0:
					userGender = "MALE";
					break;
				case 1:
					userGender = "FEMALE";
					break;
				}

				// and then determining whether it is a registered user code through the web API

				// validate that user input is there and is in the right format
				if (userCode.trim().equals(""))
				{
					Toast.makeText(context, "Missing Anonymous Code.", Toast.LENGTH_LONG).show();
					return;
				}
				else
				{
					if (!userCode.matches("[a-zA-Z]{4}\\d{2}"))
					{
						Toast.makeText(context, "Anonymous Code in the wrong format.",
								Toast.LENGTH_LONG).show();
						return;
					}
				}

				if (userAge.trim().equals(""))
				{
					Toast.makeText(context, "Missing Age.", Toast.LENGTH_LONG).show();
					return;
				}

				// TODO fill in user details on the form if they already exist
				// Store user info in the SharedPreferences so if the database request fails
				// the user doesn't have to enter it all again
				AppManager.setUserCode(context, userCode);
				// The constraints on the EditText 'should' ensure that this parse always works
				AppManager.setUserAge(context, Integer.parseInt(userAge));
				AppManager.setUserGender(context, userGender);

				(new SynchronizationTask()).execute(userCode, userAge, userGender);
			}
		});

		txtUserIDHint.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("Anonymous User Code").setMessage(R.string.user_id_help_string)
						.show();
			}
		});

	}

	private int synchronizeWithUserDatabase(String userCode, int userAge, String userGender)
	{
		String userXML = String.format(Locale.UK, "<user code=\"%s\" age=\"%d\" gender=\"%s\" />",
				userCode, userAge, userGender);
		Log.d(getClass().getCanonicalName(), "Submitting userXML: " + userXML);
		try
		{
			InputStream response = QuestionAPI.getHTTPResponseStream("/users.php", "POST",
					userXML.getBytes());
			Scanner sc = new Scanner(new InputStreamReader(response));
			int newUserID = -1;
			while (sc.hasNextInt())
			{
				newUserID = sc.nextInt();
			}
			Log.d(getClass().getCanonicalName(), "Returned new userID: " + newUserID);
			return newUserID;
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			return -1;
		}
	}

	private class SynchronizationTask extends AsyncTask<String, Void, Integer>
	{
		@Override
		protected void onPreExecute()
		{
			progressDialog = ProgressDialog.show(context, "Creating New User",
					"Synchronizing user details with the database.");
		};

		// The slow code that runs in the background
		@Override
		protected Integer doInBackground(String... params)
		{
			try
			{
				return synchronizeWithUserDatabase(params[0], Integer.parseInt(params[1]),
						params[2]);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return -1;
			}
		}

		// Run the result on the UI thread
		@Override
		protected void onPostExecute(Integer userID)
		{
			if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();

			if (userID == -1)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("Synchronization Failure.")
						.setMessage(
								"Please ensure that you have an Internet connection and try again later.")
						.setPositiveButton("OK", new OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								finish();
							}
						}).show();
				return;
			}

			AppManager.setUserID(context, userID);
			AppManager.setFirstRun(context, false);
			AppManager.setNotificationTime(context, new SimpleDateFormat("yyyy-MM-dd", Locale.UK).format((Calendar.getInstance()).getTime()));
			
			Intent i = new Intent(context, MainActivity.class);
			i.putExtra("sessionID", Section.START_TRAIT_SESSION_ID);
			startActivity(i);
			finish();
		}
	}
}
