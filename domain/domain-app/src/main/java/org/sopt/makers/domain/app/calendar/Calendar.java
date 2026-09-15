package org.sopt.makers.domain.app.calendar;

import java.time.LocalDate;

public record Calendar(
    Long id,
    int generation,
    String title,
    boolean isOneDaySchedule,
    boolean isOnlyActiveGeneration,
    LocalDate startDate,
    LocalDate endDate,
    CalendarType type) {}
