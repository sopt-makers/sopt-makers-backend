package org.sopt.makers.domain.official.recruitment;

public record Schedule(
    String applicationStartTime,
    String applicationEndTime,
    String applicationResultTime,
    String interviewStartTime,
    String interviewEndTime,
    String finalResultTime) {}
