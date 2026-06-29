package org.sopt.makers.domain.official.recruit;

import java.util.List;
import org.sopt.makers.core.type.Part;

public record RecruitPartInfo(
    Long id, Integer generationId, Part part, String description, List<String> curriculums) {}
