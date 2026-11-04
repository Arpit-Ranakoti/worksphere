package com.worksphere.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class DateTimeUtil {

    private DateTimeUtil() {
    }

    public static long minutesUntil(LocalDate d, LocalTime t) {
        return Duration.between(LocalDateTime.now(), LocalDateTime.of(d, t)).toMinutes();
    }

    /**
     * True when the job's date (and end time, if given) is already in the past.
     * A job with no date is treated as not past, so it is never hidden by mistake.
     */
    public static boolean isPast(LocalDate date, LocalTime endTime) {
        if (date == null) {
            return false;
        }
        LocalDateTime end = LocalDateTime.of(date, endTime != null ? endTime : LocalTime.MAX);
        return end.isBefore(LocalDateTime.now());
    }
}
