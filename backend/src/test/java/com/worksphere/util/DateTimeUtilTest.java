package com.worksphere.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

class DateTimeUtilTest {

    private static LocalDate today() {
        return DateTimeUtil.now().toLocalDate();
    }

    @Test
    void yesterdayIsPast() {
        assertTrue(DateTimeUtil.isPast(today().minusDays(1), LocalTime.of(9, 0), LocalTime.of(17, 0)));
    }

    @Test
    void tomorrowIsNotPast() {
        assertFalse(DateTimeUtil.isPast(today().plusDays(1), LocalTime.of(9, 0), LocalTime.of(17, 0)));
    }

    @Test
    void missingDateIsNeverPast() {
        assertFalse(DateTimeUtil.isPast(null, LocalTime.of(9, 0), LocalTime.of(17, 0)));
    }

    @Test
    void missingEndTimeLastsUntilEndOfDay() {
        assertFalse(DateTimeUtil.isPast(today(), null, null));
        assertTrue(DateTimeUtil.isPast(today().minusDays(1), null, null));
    }

    @Test
    void overnightJobEndsOnTheNextDay() {
        LocalDateTime end = DateTimeUtil.endOf(LocalDate.of(2026, 11, 5), LocalTime.of(20, 0), LocalTime.of(2, 0));
        assertEquals(LocalDateTime.of(2026, 11, 6, 2, 0), end);
    }

    @Test
    void overnightJobStartingTodayIsNotPast() {
        // Ends at 02:00 tomorrow, so it can never be past on the day it starts.
        assertFalse(DateTimeUtil.isPast(today(), LocalTime.of(20, 0), LocalTime.of(2, 0)));
    }

    @Test
    void normalJobEndsOnItsOwnDay() {
        LocalDateTime end = DateTimeUtil.endOf(LocalDate.of(2026, 11, 5), LocalTime.of(9, 0), LocalTime.of(17, 0));
        assertEquals(LocalDateTime.of(2026, 11, 5, 17, 0), end);
    }
}
