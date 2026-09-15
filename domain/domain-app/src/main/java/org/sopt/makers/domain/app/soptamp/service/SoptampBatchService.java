package org.sopt.makers.domain.app.soptamp.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.app.operationconfig.OperationConfigCategory;
import org.sopt.makers.domain.app.operationconfig.service.OperationConfigService;
import org.sopt.makers.domain.app.soptamp.SoptampMode;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.port.SoptampProfileSourcePort;
import org.sopt.makers.domain.app.soptamp.port.SoptampUserQueryPort;
import org.sopt.makers.domain.user.User;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SoptampBatchService {

  private static final int BATCH_SIZE = 100;
  private static final String UPSERT_CRON_KEY = "UPSERT_CRON";
  private static final String DEFAULT_UPSERT_CRON = "0 0 3 * * *";
  private static final String UPSERT_CRON_DESCRIPTION = "솝탬프 upsert 배치 실행 cron 표현식";

  private final SoptampUserUpserter soptampUserUpserter;
  private final SoptampUserQueryPort soptampUserQueryPort;
  private final SoptampProfileSourcePort soptampProfileSourcePort;
  private final OperationConfigService operationConfigService;
  private final SoptampMode soptampMode;

  public SoptampUpsertResult upsertAllSoptampUsers() {
    if (soptampMode.isAppjam()) {
      List<User> allProfiles = soptampProfileSourcePort.findAllForUpsert();
      return upsertInChunks(
          "앱잼", allProfiles.stream().map(User::id).toList(), allProfiles::subList);
    }
    List<Long> targetUserIds = soptampUserQueryPort.findAllUserIds();
    return upsertInChunks(
        "일반",
        targetUserIds,
        (from, to) -> soptampProfileSourcePort.findAllByUserIds(targetUserIds.subList(from, to)));
  }

  public String getUpsertCron() {
    return operationConfigService
        .findValue(OperationConfigCategory.SOPTAMP_BATCH, UPSERT_CRON_KEY)
        .orElse(DEFAULT_UPSERT_CRON);
  }

  @Transactional
  public void setUpsertCron(String cron) {
    try {
      new CronTrigger(cron);
    } catch (IllegalArgumentException e) {
      throw new SoptampException(SoptampFailure.INVALID_UPSERT_CRON);
    }
    operationConfigService.upsertValue(
        OperationConfigCategory.SOPTAMP_BATCH, UPSERT_CRON_KEY, cron, UPSERT_CRON_DESCRIPTION);
    log.info("솝탬프 upsert 배치 스케줄 변경. cron={}", cron);
  }

  private SoptampUpsertResult upsertInChunks(
      String mode, List<Long> targetUserIds, ChunkLoader chunkLoader) {
    int total = targetUserIds.size();
    int processedCount = 0;
    int skippedCount = 0;
    int missingProfileCount = 0;
    int failedChunkCount = 0;
    log.info("솝탬프 유저 upsert 시작 ({}). 총 {}명, 청크 크기: {}", mode, total, BATCH_SIZE);

    for (int from = 0; from < total; from += BATCH_SIZE) {
      int to = Math.min(from + BATCH_SIZE, total);
      log.info("청크 처리 중: [{}/{}]", to, total);
      try {
        List<User> profiles = chunkLoader.load(from, to);
        missingProfileCount += logMissingProfiles(targetUserIds.subList(from, to), profiles);
        List<Long> skippedUserIds = soptampUserUpserter.upsertChunk(profiles);
        skippedCount += skippedUserIds.size();
        processedCount += profiles.size() - skippedUserIds.size();
      } catch (Exception e) {
        failedChunkCount++;
        log.error("솝탬프 upsert 청크 처리 실패. chunk=[{}/{}], error={}", to, total, e.getMessage(), e);
      }
    }

    SoptampUpsertResult result =
        new SoptampUpsertResult(
            total, processedCount, skippedCount, missingProfileCount, failedChunkCount);
    logSummary(mode, result);
    return result;
  }

  private int logMissingProfiles(List<Long> chunkUserIds, List<User> profiles) {
    Set<Long> foundUserIds = profiles.stream().map(User::id).collect(Collectors.toSet());
    List<Long> missingUserIds =
        chunkUserIds.stream().filter(userId -> !foundUserIds.contains(userId)).toList();
    if (!missingUserIds.isEmpty()) {
      log.error("솝탬프 upsert 대상 유저의 프로필을 찾지 못해 누락한다. userIds={}", missingUserIds);
    }
    return missingUserIds.size();
  }

  private void logSummary(String mode, SoptampUpsertResult result) {
    String summary =
        String.format(
            "솝탬프 유저 upsert 완료 (%s). 요청: %d명, 처리: %d명, 기수 없음 건너뜀: %d명, 프로필 누락: %d명, 실패 청크: %d개",
            mode,
            result.requestedCount(),
            result.processedCount(),
            result.skippedByNullGenerationCount(),
            result.missingProfileCount(),
            result.failedChunkCount());
    if (result.isFailed()) {
      log.error("{} 처리하지 못한 유저나 청크가 있어 이번 실행은 실패로 본다", summary);
    } else {
      log.info(summary);
    }
  }

  @FunctionalInterface
  private interface ChunkLoader {
    List<User> load(int from, int to);
  }
}
