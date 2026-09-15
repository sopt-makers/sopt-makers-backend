package org.sopt.makers.api.controller.admin.soptamp.dto;

import org.sopt.makers.domain.app.soptamp.service.SoptampUpsertResult;

public record SoptampUpsertResponse(
    boolean failed,
    int requestedCount,
    int processedCount,
    int skippedByNullGenerationCount,
    int missingProfileCount,
    int failedChunkCount) {

  public static SoptampUpsertResponse from(SoptampUpsertResult result) {
    return new SoptampUpsertResponse(
        result.isFailed(),
        result.requestedCount(),
        result.processedCount(),
        result.skippedByNullGenerationCount(),
        result.missingProfileCount(),
        result.failedChunkCount());
  }
}
