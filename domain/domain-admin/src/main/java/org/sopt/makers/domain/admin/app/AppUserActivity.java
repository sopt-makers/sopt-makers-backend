package org.sopt.makers.domain.admin.app;

import org.sopt.makers.core.type.Part;

public record AppUserActivity(
    Long userId, String name, int generation, Part part, Float attendanceScore) {}
