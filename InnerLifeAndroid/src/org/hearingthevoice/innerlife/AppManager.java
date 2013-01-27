package org.hearingthevoice.innerlife;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppManager extends Application
{
	private static AppManager sInstance;
	
	public static final String FIRST_RUN_KEY = "first_run";
	public static final String PREFERENCES_KEY = "preferences_key";
	public static final String LAST_SESSION_ID = "session_id_key";
	public static final String LAST_SESSION_COMPLETE = "session_complete_key";
	
	public static final String SAMPLES_COMPLETE = "samples_complete_key";
	public static final String SAMPLE_TIMES = "sample_times_key";
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		sInstance = this;
	}

	public static AppManager getInstance()
	{
		return sInstance;
	}

	public static void clearPreferences(Context context)
	{
		Editor editor = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
	}

	public static boolean isFirstRun(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		return preferences.getBoolean(FIRST_RUN_KEY, true);
	}

	public static boolean setFirstRun(Context context, boolean isFirstRun)
	{
		Editor editor = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE).edit();
		editor.putBoolean(FIRST_RUN_KEY, isFirstRun);
		return editor.commit();
	}
	
	public static boolean setLastSession(Context context, int sessionID)
	{
		Editor editor = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE).edit();
		editor.putInt(LAST_SESSION_ID, sessionID);
		return editor.commit();
	}
	
	public static int getLastSession(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		return preferences.getInt(LAST_SESSION_ID, -1);
	}
	
	public static boolean setSessionComplete(Context context, boolean complete)
	{
		Editor editor = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE).edit();
		editor.putBoolean(LAST_SESSION_COMPLETE, complete);
		return editor.commit();
	}
	
	public static boolean isSessionComplete(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		return preferences.getBoolean(LAST_SESSION_COMPLETE, false);
	}
	
	public static void recordSampleComplete(Context context, String time)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		
		editor.putInt(SAMPLES_COMPLETE, preferences.getInt(SAMPLES_COMPLETE, 0) + 1);
		editor.putInt(time, preferences.getInt(time, 0) + 1);
		
		editor.commit();
	}
	
	public static int getSamplesComplete(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		return preferences.getInt(SAMPLES_COMPLETE, 0);
	}
	
	public static int getSamplesComplete(Context context, String time)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		return preferences.getInt(time, 0);
	}
	
	public static int getSamplesCompleteToday(Context context)
	{
		Calendar today = Calendar.getInstance();
		
		String time = new SimpleDateFormat("yyyy-MM-dd").format(today.getTime());
		
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		return preferences.getInt(time, 0);
	}
}
