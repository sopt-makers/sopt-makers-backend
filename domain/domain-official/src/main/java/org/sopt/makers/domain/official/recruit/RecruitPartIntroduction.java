package org.sopt.makers.domain.official.recruit;

import org.sopt.makers.core.type.Part;

public record RecruitPartIntroduction(
    Long id, Integer generationId, Part part, String content, String preference) {}
