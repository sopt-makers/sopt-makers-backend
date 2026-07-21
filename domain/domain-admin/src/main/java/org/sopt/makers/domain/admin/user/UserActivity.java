package org.sopt.makers.domain.admin.user;

import org.sopt.makers.core.type.Part;

public record UserActivity(
    Long userId, String name, int generation, Part part, Float attendanceScore) {}
