package org.hearingthevoice.innerlife;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hearingthevoice.innerlife.model.Section;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.testflightapp.lib.TestFlight;

public class AppManager extends Application
{
	private static AppManager sInstance;

	private Map<Long, Integer> prevResponseIDs;
	private Map<Long, String> prevResponseStrings;
	private Map<Long, String> prevResponseValues;
	private List<Section> prevSession;

	public int question;
	public int section;

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

		TestFlight.takeOff(this, "d3efa820-9a27-4c93-8b55-68fe624e50dc");
	}

	public static AppManager getInstance()
	{
		return sInstance;
	}

	public void clearSample()
	{
		prevResponseIDs = null;
		prevResponseStrings = null;
		prevResponseValues = null;

		question = 0;
		section = 0;
	}

	public Map<Long, Integer> getResponseIDs()
	{
		return prevResponseIDs;
	}

	public void setResponseIDs(Map<Long, Integer> responses)
	{
		prevResponseIDs = responses;
	}

	public Map<Long, String> getResponseStrings()
	{
		return prevResponseStrings;
	}

	public void setResponseStrings(Map<Long, String> responses)
	{
		prevResponseStrings = responses;
	}

	public List<Section> getSection()
	{
		return prevSession;
	}

	public void setSection(List<Section> sections)
	{
		prevSession = sections;
	}

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

		String time = new SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(today.getTime());

		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
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
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();

		editor.putString(USER_CODE, userCode);

		editor.commit();
	}

	public static String getUserCode(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		return preferences.getString(USER_CODE, "------");
	}

	public static void setUserAge(Context context, int userAge)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();

		editor.putInt(USER_AGE, userAge);

		editor.commit();
	}

	public static int getUserAge(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		return preferences.getInt(USER_AGE, 0);
	}

	public static void setUserGender(Context context, String userGender)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();

		editor.putString(USER_GENDER, userGender);

		editor.commit();
	}

	public static String getUserGender(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
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

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);

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
		float newAverageResponseTime = (weightedAvgResponseTime + newResponseTime)
				/ (float) (preferences.getInt(SAMPLES_COMPLETE, 0));

		editor.putInt(AVERAGE_RESPONSE_TIME, (int) newAverageResponseTime);

		editor.commit();
	}

	public static String getAverageResponseTime(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		int avgResponseTime = preferences.getInt(AVERAGE_RESPONSE_TIME, 0);
		String formattedTime = "";
		if (avgResponseTime > (60 * 60))
		{
			int hours = avgResponseTime / (60 * 60);
			formattedTime += hours + (hours == 1 ? " hour " : " hours ");
		}
		if (avgResponseTime > 60)
		{
			int minutes = (avgResponseTime % (60 * 60)) / 60;
			formattedTime += minutes + (minutes == 1 ? " minute " : " minutes ");
		}
		if (avgResponseTime > 0)
		{
			int seconds = (avgResponseTime % (60 * 60)) % 60;
			formattedTime += seconds + (seconds == 1 ? " second" : " seconds");
		}
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

	public static int getDebugSessionID(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		return preferences.getInt(POSSIBLE_SAMPLES_SO_FAR, 1);
	}

	public static void setDebugSessionID(Context context, int samples)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt(POSSIBLE_SAMPLES_SO_FAR, samples);
		editor.commit();
	}

	public static final String ALLOW_DATA_USE = "allow_data_use_key";

	public static boolean getAllowDataUse(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		return preferences.getBoolean(ALLOW_DATA_USE, false);
	}

	public static void setAllowDataUse(Context context, boolean value)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(ALLOW_DATA_USE, value);
		editor.commit();
	}

	public static final String DATA_USE_PREFERENCE_SYNCED_WITH_DATABASE = "data_use_preference_synced_with_database_key";

	public static boolean getDataUsePreferenceSyncedWithDatabase(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		return preferences.getBoolean(DATA_USE_PREFERENCE_SYNCED_WITH_DATABASE, false);
	}

	public static void setDataUsePreferenceSyncedWithDatabase(Context context, boolean value)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(DATA_USE_PREFERENCE_SYNCED_WITH_DATABASE, value);
		editor.commit();
	}

	public static final String SUBMITTED_START_TRAIT = "submitted_start_trait_key";

	public static boolean getSubmittedStartTrait(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		return preferences.getBoolean(SUBMITTED_START_TRAIT, false);
	}

	public static void setSubmittedStartTrait(Context context, boolean value)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(SUBMITTED_START_TRAIT, value);
		editor.commit();
	}

	public static final String SUBMITTED_MIDDLE_TRAIT = "submitted_middle_trait_key";

	public static boolean getSubmittedMiddleTrait(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		return preferences.getBoolean(SUBMITTED_MIDDLE_TRAIT, false);
	}

	public static void setSubmittedMiddleTrait(Context context, boolean value)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(SUBMITTED_MIDDLE_TRAIT, value);
		editor.commit();
	}

	public static final String SUBMITTED_END_TRAIT = "submitted_end_trait_key";

	public static boolean getSubmittedEndTrait(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		return preferences.getBoolean(SUBMITTED_END_TRAIT, false);
	}

	public static void setSubmittedEndTrait(Context context, boolean value)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(SUBMITTED_END_TRAIT, value);
		editor.commit();
	}

	public static final String STOP_NOTIFICATIONS = "stop_notifications_key";

	public static boolean getStopNotifications(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		return preferences.getBoolean(STOP_NOTIFICATIONS, false);
	}

	public static void setStopNotifications(Context context, boolean value)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(STOP_NOTIFICATIONS, value);
		editor.commit();
	}

	public static final String START_DATE = "start_date_key";

	public static Date getStartDate(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		String sdate = preferences.getString(START_DATE, null);

		if (sdate != null)
		{
			return TimeUtils.deserializeTime(sdate);
		}
		else
		{
			return Calendar.getInstance(Locale.UK).getTime();
		}
	}

	public static void setStartDate(Context context, Date date)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(START_DATE, TimeUtils.serializeTime(date));
		editor.commit();
	}

	@SuppressWarnings("unchecked")
	@SuppressLint("UseSparseArrays")
	public static Map<Integer, Boolean> getSessionsCompleted(Context context)
	{
		File file = new File(context.getDir("data", MODE_PRIVATE), "sessions_completed");
		try
		{
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
			Map<Integer, Boolean> completed = (Map<Integer, Boolean>) inputStream.readObject();
			inputStream.close();

			if (completed != null) return completed;
			else return new HashMap<Integer, Boolean>();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return new HashMap<Integer, Boolean>();
		}
	}

	public static void setSessionsCompleted(Context context, Map<Integer, Boolean> map)
	{
		File file = new File(context.getDir("data", MODE_PRIVATE), "sessions_completed");
		try
		{
			ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
			outputStream.writeObject(map);
			outputStream.flush();
			outputStream.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@SuppressLint("UseSparseArrays")
	public static Map<Integer, Boolean> getSessionsSubmitted(Context context)
	{
		File file = new File(context.getDir("data", MODE_PRIVATE), "sessions_submitted");
		try
		{
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
			Map<Integer, Boolean> submitted = (Map<Integer, Boolean>) inputStream.readObject();
			inputStream.close();

			if (submitted != null) return submitted;
			else return new HashMap<Integer, Boolean>();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return new HashMap<Integer, Boolean>();
		}
	}

	public static void setSessionsSubmitted(Context context, Map<Integer, Boolean> map)
	{
		File file = new File(context.getDir("data", MODE_PRIVATE), "sessions_submitted");
		try
		{
			ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
			outputStream.writeObject(map);
			outputStream.flush();
			outputStream.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}