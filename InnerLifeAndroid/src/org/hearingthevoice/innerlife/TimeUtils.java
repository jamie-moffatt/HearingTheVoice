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
	
	public static int getSessionIDBasedOnTime(Date date)
	{
		if (Math.abs(date.getTime() - Calendar.getInstance(Locale.UK).getTime().getTime()) / 1000 < 60 * 60 * 24) return -1;
		
		Calendar c = Calendar.getInstance(Locale.UK);
		c.setTime(date);
		c.add(DAY_OF_YEAR, 1);
		c.set(HOUR_OF_DAY, 0);
		c.set(MINUTE, 0);
		c.set(SECOND, 0);
		
		long timeOnScheduleInSeconds = Math.abs(Calendar.getInstance(Locale.UK).getTime().getTime() - c.getTime().getTime()) / 1000;
		
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
