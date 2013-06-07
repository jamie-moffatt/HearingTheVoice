package org.hearingthevoice.innerlife.ui.activity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.hearingthevoice.innerlife.AppManager;
import org.hearingthevoice.innerlife.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class EndActivity extends Activity
{
	private Context context;

	private TextView txtResponseTime;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_end);

		context = this;

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
				URL url = new URL("https://www.dur.ac.uk/matthew.bates/HearingTheVoice/user-perimissions.php");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();

				conn.setReadTimeout(10000 /* milliseconds */);
				conn.setConnectTimeout(15000 /* milliseconds */);
				conn.setRequestMethod("POST");
				conn.setDoInput(true);

				conn.connect();
				int httpResponseCode = conn.getResponseCode();

				if (httpResponseCode != HttpURLConnection.HTTP_OK) throw new IOException();

				PrintWriter pw = new PrintWriter(conn.getOutputStream());
				pw.print(String.format("<user id=\"%d\" useDataPermission=\"%s\" />", AppManager.getUserID(context), allowDataUse ? "true" : "false"));
				pw.flush();
				pw.close();

				String response = "";
				Scanner sc = new Scanner(conn.getInputStream());
				if (sc.hasNextLine()) response = sc.nextLine();

				conn.disconnect();

				if (response.contains("success")) return true;
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
						.setMessage("You may now close and uninstall the application.")
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
