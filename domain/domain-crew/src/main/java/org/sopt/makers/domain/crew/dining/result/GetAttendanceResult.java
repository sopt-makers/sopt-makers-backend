package org.sopt.makers.domain.crew.dining.result;

import org.sopt.makers.domain.crew.dining.model.enums.DiningPayerStatus;

public record GetAttendanceResult(
    long attendeeCount, boolean isAttending, DiningPayerStatus payerStatus) {}
