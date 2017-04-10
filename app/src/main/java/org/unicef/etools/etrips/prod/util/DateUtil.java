package org.unicef.etools.etrips.prod.util;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {

    private static final String LOG_TAG = DateUtil.class.getSimpleName();
    public static final String DD_MMM_YYYY = "dd MMM yyyy";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String SERVER_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public static String convertISOtoCalendarDate(final String iso8601string, String format) {
        if (iso8601string != null) {
            Calendar calendar = GregorianCalendar.getInstance();
            DateFormat simpleDateFormat = new SimpleDateFormat(SERVER_FORMAT);
            Date date;
            try {
                date = simpleDateFormat.parse(iso8601string);
                calendar.setTime(date);
                int timeZone = calendar.getTimeZone().getRawOffset();
                long dateInMill = calendar.getTimeInMillis() + timeZone;
                SimpleDateFormat formatter = new SimpleDateFormat(format);
                calendar.setTimeInMillis(dateInMill);
                String dateString = formatter.format(calendar.getTime());
                dateString = String.valueOf(dateString.charAt(0)).toUpperCase()
                        + dateString.subSequence(1, dateString.length());
                return dateString;
            } catch (ParseException e) {
                e.printStackTrace();
                return Constant.Symbol.NULL;
            }
        } else {
            return Constant.Symbol.NULL;
        }
    }

    @NonNull
    public static Calendar composeCalendarFromDate(@Nullable String source) {
        final Calendar calendar = Calendar.getInstance();
        if (source != null) {
            final SimpleDateFormat format = new SimpleDateFormat(DD_MMM_YYYY, Locale.getDefault());
            try {
                calendar.setTime(format.parse(source));
                return calendar;
            } catch (ParseException e) {
                return calendar;
            }
        } else {
            return calendar;
        }
    }

    @NonNull
    public static String convertDatePickerResultToString(int year, int month, int day, String format) {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(year, month, day);

        final int timeZone = calendar.getTimeZone().getRawOffset();
        final long dateInMill = calendar.getTimeInMillis() + timeZone;
        calendar.setTimeInMillis(dateInMill);

        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        String dateString = formatter.format(calendar.getTime());

        return String.valueOf(dateString.charAt(0)).toUpperCase()
                + dateString.subSequence(1, dateString.length());
    }

    @NonNull
    public static String convertDateToString(long timeInMillis, String format) {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(timeInMillis);

        final int timeZone = calendar.getTimeZone().getRawOffset();
        final long dateInMill = calendar.getTimeInMillis() + timeZone;
        calendar.setTimeInMillis(dateInMill);

        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        String dateString = formatter.format(calendar.getTime());

        return String.valueOf(dateString.charAt(0)).toUpperCase()
                + dateString.subSequence(1, dateString.length());
    }

    public static String convertStringDatetoCalendarDate(final String stringDate) {
        if (stringDate != null) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd", Locale.getDefault());
                Calendar.getInstance().setTime(simpleDateFormat.parse(stringDate));
                DateFormat formatter = DateFormat.getDateInstance(
                        DateFormat.DEFAULT);
                formatter.setTimeZone(Calendar.getInstance().getTimeZone());
                String formattedDate = formatter.format(Calendar.getInstance().getTime());
                return formattedDate;
            } catch (ParseException e) {
                e.printStackTrace();
                return Constant.Symbol.NULL;
            }
        } else {
            return Constant.Symbol.NULL;
        }
    }

    public static boolean isDateOutdated(Calendar source) {
        final Calendar today = Calendar.getInstance();

        // compare only dates without time part
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        source.set(Calendar.HOUR_OF_DAY, 0);
        source.set(Calendar.MINUTE, 0);
        source.set(Calendar.SECOND, 0);
        source.set(Calendar.MILLISECOND, 0);

        return today.after(source);
    }

}

