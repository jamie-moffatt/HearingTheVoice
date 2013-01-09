package org.hearingthevoice.innerlife;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppManager extends Application
{
	private static AppManager sInstance;
	
	private static final String FIRST_RUN_KEY = "first_run";
	public static final String PREFERENCES_KEY = "preferences_key";
	public static final String LAST_SESSION_ID = "session_id_key";
	public static final String LAST_SESSION_COMPLETE = "session_complete_key";
	
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
}
