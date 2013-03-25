package org.hearingthevoice.innerlife.ui.activity;

import java.util.Iterator;
import java.util.Map;

import org.hearingthevoice.innerlife.AppManager;
import org.hearingthevoice.innerlife.R;
import org.hearingthevoice.innerlife.R.layout;
import org.hearingthevoice.innerlife.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class DebugActivity extends Activity
{
	private TableLayout tableLayout;

	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug);
		context = this;

		tableLayout = (TableLayout) findViewById(R.id.debugTableLayout);

		SharedPreferences preferences = context.getSharedPreferences(AppManager.PREFERENCES_KEY,
				Context.MODE_PRIVATE);
		final Editor editor = preferences.edit();
		Map<String, ?> prefMap = preferences.getAll();

		Iterator<?> it = prefMap.entrySet().iterator();

		while (it.hasNext())
		{
			final Map.Entry<String, ?> pairs = (Map.Entry<String, ?>) it.next();
			TableRow row = new TableRow(context);
			TextView txtKey = new TextView(context);
			txtKey.setText(pairs.getKey());
			row.addView(txtKey);

			Object o = pairs.getValue();

			if (o instanceof String)
			{
				String s = (String) o;
				EditText edtValue = new EditText(context);
				edtValue.setText(s);
				edtValue.addTextChangedListener(new TextWatcher()
				{
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count)
					{
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after)
					{
					}

					@Override
					public void afterTextChanged(Editable s)
					{
						editor.putString(pairs.getKey(), s.toString());
						editor.commit();
					}
				});
				row.addView(edtValue);
			}

			if (o instanceof Integer)
			{
				Integer i = (Integer) o;
				EditText edtValue = new EditText(context);
				edtValue.setText(i.toString());
				edtValue.addTextChangedListener(new TextWatcher()
				{
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count)
					{
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after)
					{
					}

					@Override
					public void afterTextChanged(Editable s)
					{
						if (s.length() > 0)
						{
							editor.putInt(pairs.getKey(), Integer.parseInt(s.toString()));
							editor.commit();
						}
					}
				});
				row.addView(edtValue);
			}

			if (o instanceof Boolean)
			{
				Boolean b = (Boolean) o;
				final Spinner spnValue = new Spinner(context);
				ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
						R.array.boolean_array, android.R.layout.simple_spinner_item);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spnValue.setAdapter(adapter);
				if (b) spnValue.setSelection(0);
				else spnValue.setSelection(1);
				spnValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
				{
					@Override
					public void onNothingSelected(AdapterView<?> parentView)
					{
					}

					@Override
					public void onItemSelected(AdapterView<?> adapter, View view, int i, long l)
					{
						if (spnValue.getSelectedItemId() == 0)
						{
							editor.putBoolean(pairs.getKey(), true);
							editor.commit();
						}
						else
						{
							editor.putBoolean(pairs.getKey(), false);
							editor.commit();
						}
					}

				});
				row.addView(spnValue);
			}

			tableLayout.addView(row);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.debug, menu);
		return true;
	}

}
