package it.pjor94.beerhunter.core;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class Utils {

    public static HashMap<String,Long>  timeframes = new HashMap<>();
    public static HashMap<String, Duration>  timeframesDurations = new HashMap<>();


    static {
        timeframes.put("3m", 3L);
        timeframes.put("5m", 5L);
        timeframes.put("15m",15L);
        timeframes.put("30m",30L);
        timeframes.put("1h", 60L);
        timeframes.put("4h", 240L);
        //
        timeframesDurations.put("3m", Duration.ofMinutes(3L));
        timeframesDurations.put("5m", Duration.ofMinutes(5L));
        timeframesDurations.put("15m",Duration.ofMinutes(15L));
        timeframesDurations.put("30m",Duration.ofMinutes(30L));
        timeframesDurations.put("1h", Duration.ofMinutes(60L));
        timeframesDurations.put("4h", Duration.ofMinutes(240L));
        timeframesDurations.put("8h", Duration.ofMinutes(480L));
        timeframesDurations.put("1d", Duration.ofMinutes(1440L));
    }

    static Long toStartOfTheDay(Long timestamp){
        Date date = new Date(timestamp);
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * Adds or subtracts the specified amount of time to the given calendar field, based on the calendar's rules. For example, to subtract 5 days from the current time of the calendar, you can achieve it by calling:
     * addToTimestamp(Calendar.DAY_OF_MONTH, -5).
     * Params:
     * @param timestamp – the timestamp to do operation whit.
     * @param field – the calendar field.
     * @param amount  – the amount of date or time to be added to the field.
     * @return timestamp
     */
    static Long addToTimestamp(Long timestamp,int field, int amount){
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date(timestamp));
        cal.add(field, amount);
        return cal.getTimeInMillis();
    }
}
