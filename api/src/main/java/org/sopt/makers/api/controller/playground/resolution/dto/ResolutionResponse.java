package org.sopt.makers.api.controller.playground.resolution.dto;

import java.util.List;
import org.sopt.makers.domain.playground.resolution.ResolutionTag;
import org.sopt.makers.domain.playground.resolution.service.UserResolutionService.ResolutionResult;

public record ResolutionResponse(
        boolean hasWrittenTimeCapsule,
        List<ResolutionTag> tags,
        String content,
        boolean hasDrawnLuckyPick
) {
    public static ResolutionResponse from(ResolutionResult result) {
        return new ResolutionResponse(
                result.hasWrittenTimeCapsule(),
                result.tags(),
                result.content(),
                result.hasDrawnLuckyPick()
        );
    }
}
