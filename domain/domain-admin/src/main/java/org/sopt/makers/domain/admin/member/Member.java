package org.sopt.makers.domain.admin.member;

import org.sopt.makers.core.type.Part;

public record Member(
    Long id,
    String name,
    int generation,
    Part part,
    float attendanceScore,
    int attendanceCount,
    int absentCount,
    int tardyCount,
    int participateCount) {}
