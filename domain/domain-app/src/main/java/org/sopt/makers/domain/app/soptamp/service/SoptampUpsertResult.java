package org.sopt.makers.domain.app.soptamp.service;

public record SoptampUpsertResult(
    int requestedCount,
    int processedCount,
    int skippedByNullGenerationCount,
    int missingProfileCount,
    int failedChunkCount) {

  public boolean isFailed() {
    return skippedByNullGenerationCount > 0 || missingProfileCount > 0 || failedChunkCount > 0;
  }
}
