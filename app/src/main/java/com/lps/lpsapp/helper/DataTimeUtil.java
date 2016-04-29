package com.lps.lpsapp.helper;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by dle on 29.04.2016.
 */
public class DataTimeUtil {
    public static Calendar convertToGTM(Calendar calendar)
    {
        GregorianCalendar result = new GregorianCalendar(TimeZone.getTimeZone("GTM"));
        result.set(Calendar.YEAR,calendar.get(Calendar.YEAR));
        result.set(Calendar.MONTH,calendar.get(Calendar.MONTH));
        result.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH));
        result.set(Calendar.HOUR_OF_DAY,calendar.get(Calendar.HOUR_OF_DAY));
        result.set(Calendar.MINUTE,calendar.get(Calendar.MINUTE));

        return result;
    }
}
