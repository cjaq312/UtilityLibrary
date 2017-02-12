package com.jagan.utilitylibrary;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

public class DateUtils {

	public static final String DEFAULT_DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

	public static String now() {
		return now(DEFAULT_DATE_FORMAT_NOW);
	}

	public static String now(final String dateFormat) {
		final Calendar cal = Calendar.getInstance();
		final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(cal.getTime());
	}

	public static Date today(boolean endOfDay) {
		return stripTime(new Date(), endOfDay);
	}

	public static Date parseDate(final String date, final String format, final TimeZone timezone) throws ParseException {
		final Calendar cal = Calendar.getInstance(timezone);
		final SimpleDateFormat sdf = new SimpleDateFormat(format);
		cal.setTime(sdf.parse(date));
		return cal.getTime();
	}

	public static Date convert(final Date in, final TimeZone fromTimezone, final TimeZone toTimezone) {
		DateFormat df1 = new SimpleDateFormat(DEFAULT_DATE_FORMAT_NOW);
		df1.setTimeZone(toTimezone);
		String out = df1.format(in);
		df1.setTimeZone(fromTimezone);
		try {
			return df1.parse(out);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Date stripTime(final Date dt, boolean begin) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		if (begin) {
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
		} else {
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 000);
		}
		return cal.getTime();
	}

	/*
	 * Input month string should following this pattern: "yyyy.MM" (example: 2012.01 for Jan 2012)
	 */
	public static Date getStartOfMonth(final String month) {
		final Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		final String[] yearmonth = StringUtils.split(month, ".");
		c.set(Integer.parseInt(yearmonth[0]), Integer.parseInt(yearmonth[1]) - 1, 1);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
		c.set(Calendar.HOUR_OF_DAY, c.getActualMinimum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getActualMinimum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getActualMinimum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getActualMinimum(Calendar.MILLISECOND));
		return c.getTime();
	}

	public static Date getEndOfMonth(final String month) {
		final Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		final String[] yearmonth = StringUtils.split(month, ".");
		c.set(Integer.parseInt(yearmonth[0]), Integer.parseInt(yearmonth[1]) - 1, 1);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		c.set(Calendar.HOUR_OF_DAY, c.getActualMaximum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getActualMaximum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getActualMaximum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getActualMaximum(Calendar.MILLISECOND));
		return c.getTime();
	}

	public static List<String> getYearList(final Date startRange, final Date endRange) {
		final List<String> years = new ArrayList<String>();
		final Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		c.setTime(startRange);
		final int startYear = c.get(Calendar.YEAR);
		c.setTime(endRange);
		final int endYear = c.get(Calendar.YEAR);
		years.add("" + startYear);
		if (endYear > startYear) {
			for (int i = startYear + 1; i <= endYear; i++) {
				years.add("" + i);
			}
		}
		return years;
	}

	public static List<String> getYearListSince(final Date startDate) {
		Date endRange = today(true);
		return getYearList(startDate, endRange);
	}
}
