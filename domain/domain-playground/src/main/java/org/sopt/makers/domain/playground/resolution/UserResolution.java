package org.sopt.makers.domain.playground.resolution;

import java.util.List;

public record UserResolution(
    Long id, Long userId, String content, int generation, List<ResolutionTag> resolutionTags) {}
