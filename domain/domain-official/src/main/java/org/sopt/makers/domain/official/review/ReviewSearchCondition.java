package org.sopt.makers.domain.official.review;

import org.sopt.makers.core.type.Part;

public record ReviewSearchCondition(
    String category, String activity, Part part, Integer generation) {}
