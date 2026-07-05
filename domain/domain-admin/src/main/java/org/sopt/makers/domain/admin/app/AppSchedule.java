package org.sopt.makers.domain.admin.app;

import java.time.LocalDateTime;
import org.sopt.makers.domain.admin.lecture.LectureAttribute;

public record AppSchedule(
    Long id,
    LocalDateTime startAt,
    LocalDateTime endAt,
    LectureAttribute attribute,
    String title) {}
