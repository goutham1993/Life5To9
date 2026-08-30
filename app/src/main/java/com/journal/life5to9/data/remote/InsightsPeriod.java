package com.journal.life5to9.data.remote;

import java.util.Calendar;
import java.util.Date;

public enum InsightsPeriod {
    LAST_7_DAYS("Last 7 days"),
    THIS_WEEK("This week"),
    THIS_MONTH("This month");

    private final String label;

    InsightsPeriod(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Date[] getDateRange() {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        switch (this) {
            case LAST_7_DAYS:
                start.add(Calendar.DAY_OF_MONTH, -6);
                setStartOfDay(start);
                setEndOfDay(end);
                break;
            case THIS_WEEK:
                int dayOfWeek = start.get(Calendar.DAY_OF_WEEK);
                int daysFromMonday = (dayOfWeek == Calendar.SUNDAY) ? 6 : dayOfWeek - Calendar.MONDAY;
                start.add(Calendar.DAY_OF_MONTH, -daysFromMonday);
                setStartOfDay(start);
                end.setTime(start.getTime());
                end.add(Calendar.DAY_OF_MONTH, 6);
                setEndOfDay(end);
                break;
            case THIS_MONTH:
                start.set(Calendar.DAY_OF_MONTH, 1);
                setStartOfDay(start);
                end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));
                setEndOfDay(end);
                break;
        }

        return new Date[]{start.getTime(), end.getTime()};
    }

    private static void setStartOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void setEndOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }
}
