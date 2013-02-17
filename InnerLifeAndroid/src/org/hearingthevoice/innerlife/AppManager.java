package org.hearingthevoice.innerlife;

import java.text.ParseException;
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
	
	public static final String USER_CODE = "user_code_key";
	public static final String USER_AGE = "user_age_key";
	public static final String USER_GENDER = "user_gender_key";
	
	public static final String GOT_NOTIFICATION = "got_notification_key";
	public static final String NOTIFICATION_TIME = "notification_time_key";

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
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY,
				Context.MODE_PRIVATE);
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
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY,
				Context.MODE_PRIVATE);
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
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY,
				Context.MODE_PRIVATE);
		return preferences.getBoolean(LAST_SESSION_COMPLETE, false);
	}

	public static void recordSampleComplete(Context context, String time)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY,
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();

		editor.putInt(SAMPLES_COMPLETE, preferences.getInt(SAMPLES_COMPLETE, 0) + 1);
		editor.putInt(time, preferences.getInt(time, 0) + 1);

		editor.commit();
	}

	public static int getSamplesComplete(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY,
				Context.MODE_PRIVATE);
		return preferences.getInt(SAMPLES_COMPLETE, 0);
	}

	public static int getSamplesComplete(Context context, String time)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY,
				Context.MODE_PRIVATE);
		return preferences.getInt(time, 0);
	}

	public static int getSamplesCompleteToday(Context context)
	{
		Calendar today = Calendar.getInstance();

		String time = new SimpleDateFormat("yyyy-MM-dd").format(today.getTime());

		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY,
				Context.MODE_PRIVATE);
		return preferences.getInt(time, 0);
	}

	public static String getAverageResponseTime(Context context)
	{
		return "?";
	}

	public static void setUserCode(Context context, String userCode)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY,
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();

		editor.putString(USER_CODE, userCode);

		editor.commit();
	}

	public static String getUserCode(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY,
				Context.MODE_PRIVATE);
		return preferences.getString(USER_CODE, "1");
	}

	// userAge should potentially be an int, but since it's being taken straight from an EditText
	public static void setUserAge(Context context, String userAge)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY,
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();

		editor.putString(USER_AGE, userAge);

		editor.commit();
	}

	public static String getUserAge(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY,
				Context.MODE_PRIVATE);
		return preferences.getString(USER_AGE, "0");
	}

	// again, userGender could be an enum, but that seems like overkill
	public static void setUserGender(Context context, String userGender)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY,
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();

		editor.putString(USER_GENDER, userGender);

		editor.commit();
	}

	public static String getUserGender(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY,
				Context.MODE_PRIVATE);
		return preferences.getString(USER_GENDER, "");
	}
	
	public static String getStoredData(Context context)
	{
		StringBuffer sb = new StringBuffer();
		
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		sb.append(FIRST_RUN_KEY + ": " + preferences.getBoolean(FIRST_RUN_KEY, true) + "\n");
		sb.append(LAST_SESSION_ID + ": " + preferences.getInt(LAST_SESSION_ID, 0) + "\n");
		sb.append(LAST_SESSION_COMPLETE + ": " + preferences.getBoolean(LAST_SESSION_COMPLETE, false) + "\n");
		sb.append(SAMPLES_COMPLETE + ": " + preferences.getInt(SAMPLES_COMPLETE, 0) + "\n");
		sb.append(SAMPLE_TIMES + ": " + preferences.getString(SAMPLE_TIMES, "--") + "\n");
		sb.append(USER_CODE + ": " + preferences.getString(USER_CODE, "--") + "\n");
		sb.append(USER_AGE + ": " + preferences.getString(USER_AGE, "--") + "\n");
		sb.append(USER_GENDER + ": " + preferences.getString(USER_GENDER, "--") + "\n");
		sb.append(GOT_NOTIFICATION + ": " + preferences.getBoolean(GOT_NOTIFICATION, false) + "\n");
		sb.append(NOTIFICATION_TIME + ": " + preferences.getString(NOTIFICATION_TIME, "--") + "\n");
		
		return sb.toString();
	}
	
	public static void setGotNotification(Context context, boolean sentNotification)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();

		editor.putBoolean(GOT_NOTIFICATION, sentNotification);

		editor.commit();
	}
	
	public static boolean getGotNotification(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		return preferences.getBoolean(GOT_NOTIFICATION, false);
	}
	
	public static void setNotificationTime(Context context, String time)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();

		editor.putString(NOTIFICATION_TIME, time);

		editor.commit();
	}
	
	public static Calendar getNotificationTime(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		String timeString =  preferences.getString(NOTIFICATION_TIME, null);
		if (timeString == null) return null;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c = Calendar.getInstance();
		try
		{
			c.setTime(sdf.parse(timeString));
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			return null;
		}
		return c;
	}
}
