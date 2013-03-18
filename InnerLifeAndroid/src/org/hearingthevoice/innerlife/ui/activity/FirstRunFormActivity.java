package org.hearingthevoice.innerlife.ui.activity;

import org.hearingthevoice.innerlife.AppManager;
import org.hearingthevoice.innerlife.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class FirstRunFormActivity extends Activity
{
	private Context context;

	private EditText edtUserCode;
	private EditText edtUserAge;
	private Spinner spnGender;
	private Button btnSubmitUserDetails;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_run_form);

		context = this;

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
				// add in some validation code
				// this should include checking the correct format
				// and then determining whether it is a registered user code through the web API
				String userAge = edtUserAge.getText().toString();
				// might want to check that this isn't a ridiculous value

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
				
				//Toast.makeText(context, "userCode: " + userCode + "\nuserAge: " + userAge + "\nuserGender: " + userGender, Toast.LENGTH_LONG).show();
				
				// I think this is supposed to be submitted to the database
				// but for now just store the details locally
				
				AppManager.setUserCode(context, userCode);
				AppManager.setUserAge(context, userAge);
				AppManager.setUserGender(context, userGender);
				
				AppManager.setFirstRun(context, false);
				
				Intent i = new Intent(context, MainActivity.class);
				startActivity(i);
				finish();
			}
		});

	}

}
