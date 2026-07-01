package org.sopt.makers.domain.official.activityschedule;

import java.time.LocalDate;

public record ActivitySchedule(
    Long id, Integer generationId, String name, LocalDate startDate, LocalDate endDate) {}
