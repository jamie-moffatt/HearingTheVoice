package org.hearingthevoice.innerlife.ui.activity;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.hearingthevoice.innerlife.AppManager;
import org.hearingthevoice.innerlife.R;
import org.hearingthevoice.innerlife.io.web.QuestionAPI;

import com.testflightapp.lib.TestFlight;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class EndActivity extends Activity
{
	private static final String THANK_YOU_TEXT = "Thanks!\n\n" +
			"You can now uninstall Inner Life via your Applications folder.\n\n" +
			"If you have any questions about the app, you can contact Dr Ben Alderson-Day at benjamin.alderson-day@durham.ac.uk.";
	
	private Context context;

	private TextView txtResponseTime;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_end);

		context = this;
		TestFlight.passCheckpoint("Reached EndActivity");
		txtResponseTime = (TextView) findViewById(R.id.txtResponseTimeEnd);
		String avgResponseTime = AppManager.getAverageResponseTime(context);
		txtResponseTime.setText("Your average response time was " + avgResponseTime + ".");
	}

	public void acceptDataSubmission(View v)
	{
		submitRequest(true);
	}

	public void declineDataSubmission(View v)
	{
		submitRequest(false);
	}

	public void submitRequest(boolean allowDataUse)
	{
		AppManager.setAllowDataUse(context, allowDataUse);
		if (allowDataUse)
		{
			progressDialog = ProgressDialog.show(context, "Submitting Your Request",
					"Notifying the database with new permissions.");
		}
		else
		{
			progressDialog = ProgressDialog.show(context, "Submitting Your Request",
					"Removing your data from the study.");
		}
		(new AcceptDeclineDataUseThread()).execute(allowDataUse);
	}

	private class AcceptDeclineDataUseThread extends AsyncTask<Boolean, Integer, Boolean>
	{
		// Send a notification to database detailing whether or not the user's data
		// should be used in the study
		@Override
		protected Boolean doInBackground(Boolean... params)
		{
			try
			{
				boolean allowDataUse = params[0];

				// Send the permission update to the database
				boolean success = notifyDatabase(allowDataUse);

				AppManager.setDataUsePreferenceSyncedWithDatabase(context, success);
				return success;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				AppManager.setDataUsePreferenceSyncedWithDatabase(context, false);
				return false;
			}
		}

		public boolean notifyDatabase(boolean allowDataUse)
		{
			try
			{
				URL url = new URL(QuestionAPI.INNER_LIFE_BASE_URL + "user-permissions.php");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();

				conn.setReadTimeout(10000 /* milliseconds */);
				conn.setConnectTimeout(15000 /* milliseconds */);
				conn.setRequestMethod("POST");
				conn.setDoInput(true);

				conn.connect();
				
				PrintWriter pw = new PrintWriter(conn.getOutputStream());
				pw.print(String.format("<user id=\"%d\" useDataPermission=\"%s\" />", AppManager.getUserID(context), allowDataUse ? "true" : "false"));
				pw.flush();
				pw.close();
				
				Log.d("USER PERMISSION CONNECT", "made connection");
				Log.d("USER PERMISSION HTTP RESPONSE MESSAGE", conn.getResponseMessage());
				Log.d("USER PERMISSION HTTP CODE", ""+conn.getResponseCode());

				String response = "";
				Scanner sc = new Scanner(conn.getInputStream());
				while (sc.hasNextLine()) response += sc.nextLine();

				conn.disconnect();
				
				Log.d("USER PERMISSION RESPONSE", response);

				if (response.contains("Success")) return true;
				else return false;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
		}

		//
		@Override
		protected void onPostExecute(Boolean submitted)
		{
			if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();

			if (submitted)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				AlertDialog dialog = builder.setTitle("Request Successfully Submitted.")
						.setMessage(THANK_YOU_TEXT)
						.setPositiveButton("OK", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								finish();
							}
						}).show();
			}
			else
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				AlertDialog dialog = builder
						.setTitle("Request Failed.")
						.setMessage(
								"Unfortunately your data request did not make it to the database. "
										+ "Ensure that you have an internet connection and restart the application.")
						.setPositiveButton("OK", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								finish();
							}
						}).show();
			}
		}
	}
}
