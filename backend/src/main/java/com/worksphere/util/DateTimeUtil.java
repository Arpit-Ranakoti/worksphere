package com.worksphere.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public final class DateTimeUtil {

    /**
     * Time zone that job dates and times are entered in. The MySQL profile already uses
     * Asia/Kolkata, so "now" is read in the same zone no matter where the server runs.
     */
    public static final ZoneId APP_ZONE = ZoneId.of("Asia/Kolkata");

    private DateTimeUtil() {
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(APP_ZONE);
    }

    public static long minutesUntil(LocalDate d, LocalTime t) {
        return Duration.between(now(), LocalDateTime.of(d, t)).toMinutes();
    }

    /**
     * The moment a job ends. If the end time is earlier than the start time the job runs
     * past midnight (for example 8 PM to 2 AM), so it ends on the next calendar day.
     * A missing end time means the job lasts until the end of its date.
     */
    public static LocalDateTime endOf(LocalDate date, LocalTime startTime, LocalTime endTime) {
        LocalDate endDate = date;
        if (startTime != null && endTime != null && endTime.isBefore(startTime)) {
            endDate = date.plusDays(1);
        }
        return LocalDateTime.of(endDate, endTime != null ? endTime : LocalTime.MAX);
    }

    /**
     * True when the job's date (and end time, if given) is already in the past.
     * A job with no date is treated as not past, so it is never hidden by mistake.
     */
    public static boolean isPast(LocalDate date, LocalTime startTime, LocalTime endTime) {
        if (date == null) {
            return false;
        }
        return endOf(date, startTime, endTime).isBefore(now());
    }

    /** Same as above for callers that do not know the start time. */
    public static boolean isPast(LocalDate date, LocalTime endTime) {
        return isPast(date, null, endTime);
    }
}
