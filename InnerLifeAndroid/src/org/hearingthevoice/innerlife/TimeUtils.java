package org.hearingthevoice.innerlife;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.util.Calendar.*;

public class TimeUtils
{
	private static final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
	
	public static int getSessionIDBasedOnTime(Date startDate)
	{
		Calendar c = Calendar.getInstance(Locale.UK);
		c.setTime(startDate);
		c.add(DAY_OF_YEAR, 1);
		c.set(HOUR_OF_DAY, 0);
		c.set(MINUTE, 0);
		c.set(SECOND, 0);
		
		if (Calendar.getInstance(Locale.UK).before(c)) return -1;
		
		long timeOnScheduleInSeconds = Math.abs(Calendar.getInstance(Locale.UK).getTime().getTime() - c.getTime().getTime()) / 1000;
		
		return (int) ((timeOnScheduleInSeconds / (60 * 60 * 12)) + 1);
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
