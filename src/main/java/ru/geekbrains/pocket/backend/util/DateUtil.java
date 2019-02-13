package ru.geekbrains.pocket.backend.util;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    /**
     * Convert milliseconds to a date time. If zero or negative, just return
     * null.
     *
     * @param milliseconds
     * @return
     */
    public static Date MillisToDate(final long milliseconds) {
        if (milliseconds < 1) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        return calendar.getTime();
    }

    public static DateTime MillisToDateTime(final long milliseconds) {
        if (milliseconds < 1) {
            return null;
        }
        return new DateTime(milliseconds);
    }
}
