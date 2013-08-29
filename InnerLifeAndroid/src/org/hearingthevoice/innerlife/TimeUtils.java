package org.hearingthevoice.innerlife;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

import static java.util.Calendar.*;

public class TimeUtils
{
	private static final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
	
	public static int getSessionIDBasedOnTime(Date startDate)
	{
		Calendar dayAfterStartDate = Calendar.getInstance(Locale.UK);
		dayAfterStartDate.setTime(startDate);
		dayAfterStartDate.add(DAY_OF_YEAR, 1);
		dayAfterStartDate.set(HOUR_OF_DAY, 0);
		dayAfterStartDate.set(MINUTE, 0);
		dayAfterStartDate.set(SECOND, 0);
		
		Calendar now = Calendar.getInstance(Locale.UK);
		
		if (now.before(dayAfterStartDate))
		{
			Log.d("TimeUtils:getSessionIDBasedOnTime(Date):30", "Start Time: " + serializeTime(startDate));
			Log.d("TimeUtils:getSessionIDBasedOnTime(Date):31", "Current Time: " + serializeTime(now.getTime()));
			Log.d("TimeUtils:getSessionIDBasedOnTime(Date):32", "SessionID: " + -1);
			return -1;
		}
		
		long timeOnScheduleInSeconds = Math.abs(now.getTime().getTime() - dayAfterStartDate.getTime().getTime()) / 1000;
		
		int sessionID = (int) ((timeOnScheduleInSeconds / (60 * 60 * 12)) + 1);
		Log.d("TimeUtils:getSessionIDBasedOnTime(Date):39", "Start Time: " + serializeTime(startDate));
		Log.d("TimeUtils:getSessionIDBasedOnTime(Date):40", "Current Time: " + serializeTime(now.getTime()));
		Log.d("TimeUtils:getSessionIDBasedOnTime(Date):41", "SessionID: " + sessionID);
		return sessionID;
	}
	
	public static int getSessionIDBasedOnTime(Date startDate, Date now)
	{
		Calendar c = Calendar.getInstance(Locale.UK);
		c.setTime(startDate);
		c.add(DAY_OF_YEAR, 1);
		c.set(HOUR_OF_DAY, 0);
		c.set(MINUTE, 0);
		c.set(SECOND, 0);
		
		Calendar c_now = Calendar.getInstance(Locale.UK);
		c_now.setTime(now);
		
		if (c_now.before(c)) return -1;
		
		long timeOnScheduleInSeconds = Math.abs(now.getTime() - c.getTime().getTime()) / 1000;
		
		return (int) ((timeOnScheduleInSeconds / (60 * 60 * 12)) + 1);
	}
	
	public static String serializeTime(Date datetime)
	{
		return dateformat.format(datetime);
	}
	
	public static Date deserializeTime(String timestamp)
	{
		try
		{
			Date date = dateformat.parse(timestamp);
			return date;
		}
		catch (ParseException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
}
