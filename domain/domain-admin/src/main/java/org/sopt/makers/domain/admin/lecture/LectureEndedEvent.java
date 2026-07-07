package org.sopt.makers.domain.admin.lecture;

import java.util.List;

public record LectureEndedEvent(Lecture lecture, List<Long> userIds) {}
