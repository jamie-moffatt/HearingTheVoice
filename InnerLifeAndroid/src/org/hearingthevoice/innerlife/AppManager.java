package org.hearingthevoice.innerlife;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.hearingthevoice.innerlife.model.Section;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppManager extends Application
{
	private static AppManager sInstance;
	
	private Map<Long, Integer> prevResponseIDs;
	private Map<Long, String> prevResponseStrings;
	private Map<Long, String> prevResponseValues;
	private List<Section> prevSession;

	public static final String FIRST_RUN_KEY = "first_run";
	public static final String PREFERENCES_KEY = "preferences_key";
	public static final String LAST_SESSION_ID = "session_id_key";
	public static final String LAST_SESSION_COMPLETE = "session_complete_key";

	public static final String SAMPLES_COMPLETE = "samples_complete_key";
	public static final String SAMPLE_TIMES = "sample_times_key";
	
	public static final String POSSIBLE_SAMPLES_SO_FAR = "possible_samples_so_far_key";
	
	public static final String USER_ID = "user_id_key";
	public static final String USER_CODE = "user_code_key";
	public static final String USER_AGE = "user_age_key";
	public static final String USER_GENDER = "user_gender_key";
	
	public static final String AM_NOTIFICATION = "am_notification_key";
	public static final String PM_NOTIFICATION = "pm_notification_key";
	
	public static final String GOT_NOTIFICATION = "got_notification_key";
	public static final String NOTIFICATION_TIME = "notification_time_key";
	
	public static final String AVERAGE_RESPONSE_TIME = "average_response_time_key";

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
	
	public Map<Long, Integer> getResponseIDs() { return prevResponseIDs; }
	
	public void setResponseIDs(Map<Long, Integer> responses) { prevResponseIDs = responses; }
	
	public Map<Long, String> getResponseStrings() { return prevResponseStrings; }
	
	public void setResponseStrings(Map<Long, String> responses) { prevResponseStrings = responses; }
	
	public List<Section> getSection() { return prevSession; }
	
	public void setSection(List<Section> sections) { prevSession = sections; }

	public Map<Long, String> getResponseValues()
	{
		return prevResponseValues;
	}

	public void setResponseValues(Map<Long, String> responseValues)
	{
		this.prevResponseValues = responseValues;
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
	
	public static void setUserID(Context context, int userID)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		
		editor.putInt(USER_ID, userID);
		editor.commit();
	}
	
	public static int getUserID(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		return preferences.getInt(USER_ID, 0);
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

	public static void setUserAge(Context context, int userAge)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY,
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();

		editor.putInt(USER_AGE, userAge);

		editor.commit();
	}

	public static int getUserAge(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY,
				Context.MODE_PRIVATE);
		return preferences.getInt(USER_AGE, 0);
	}

	// userGender could be an enum, but that seems like overkill
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
	
	public static String getNotificationTime(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		return preferences.getString(NOTIFICATION_TIME, null);
	}
	
	public static void updateAverageResponseTime(Context context, String notificationTime, String responseTime)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Calendar nT = Calendar.getInstance();
		try
		{
			nT.setTime(sdf.parse(notificationTime));
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			return;
		}
		
		Calendar rT = Calendar.getInstance();
		try
		{
			rT.setTime(sdf.parse(responseTime));
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			return;
		}
		
		long newResponseTime = (rT.getTimeInMillis() / 1000) - (nT.getTimeInMillis() / 1000);
		
		int currentAvgResponseTime = preferences.getInt(AVERAGE_RESPONSE_TIME, 0);
		int weightedAvgResponseTime = currentAvgResponseTime * (preferences.getInt(SAMPLES_COMPLETE, 0) - 1);
		float newAverageResponseTime = (weightedAvgResponseTime + newResponseTime) / (float) (preferences.getInt(SAMPLES_COMPLETE, 0));
		
		editor.putInt(AVERAGE_RESPONSE_TIME, (int) newAverageResponseTime);

		editor.commit();
	}
	
	public static String getAverageResponseTime(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		int avgResponseTime = preferences.getInt(AVERAGE_RESPONSE_TIME, 0);
		String formattedTime = "";
		if (avgResponseTime > (60*60)) formattedTime += (avgResponseTime / (60*60)) + "h";
		if (avgResponseTime > 60) formattedTime += ((avgResponseTime % (60*60)) / 60) + "m";
		if (avgResponseTime > 0) formattedTime += ((avgResponseTime % (60*60)) % 60) + "s";
		else return "?";
		return formattedTime;
	}
	
	public static int getAMNotificationTime(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		return preferences.getInt(AM_NOTIFICATION, 9);
	}
	
	public static void setAMNotificationTime(Context context, int time)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt(AM_NOTIFICATION, time);
		editor.commit();
	}
	
	public static int getPMNotificationTime(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		return preferences.getInt(PM_NOTIFICATION, 17);
	}
	
	public static void setPMNotificationTime(Context context, int time)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt(PM_NOTIFICATION, time);
		editor.commit();
	}
	
	public static int getPossibleSamplesSoFar(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		return preferences.getInt(POSSIBLE_SAMPLES_SO_FAR, 0);
	}
	
	public static void setPossibleSamplesSoFar(Context context, int samples)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt(POSSIBLE_SAMPLES_SO_FAR, samples);
		editor.commit();
	}
	
	public static boolean getAllowDataUse(Context context)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public static void setAllowDataUse(Context context, boolean allowDataUse)
	{
		// TODO Auto-generated method stub
	}
	
	public static boolean getDataUsePreferenceSyncedWithDatabase(Context context)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public static void setDataUsePreferenceSyncedWithDatabase(Context context, boolean success)
	{
		// TODO Auto-generated method stub
	}

}
