package org.unicef.etools.etrips.db.entity;

import android.support.annotation.NonNull;

import java.util.Calendar;

public class DateWrapper {

    private int year;
    private int month;
    private int dayOfMonth;

    public DateWrapper() {
        // empty
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    @NonNull
    public static DateWrapper from(@NonNull Calendar calendar) {
        final DateWrapper wrapper = new DateWrapper();
        wrapper.setYear(calendar.get(Calendar.YEAR));
        wrapper.setMonth(calendar.get(Calendar.MONTH));
        wrapper.setDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
        return wrapper;
    }
}
