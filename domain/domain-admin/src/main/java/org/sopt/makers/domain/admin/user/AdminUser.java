package org.sopt.makers.domain.admin.user;

import org.sopt.makers.core.type.Part;

public record AdminUser(
    Long id,
    String name,
    Part part,
    float attendanceScore,
    int attendanceCount,
    int absentCount,
    int tardyCount,
    int participateCount) {}
