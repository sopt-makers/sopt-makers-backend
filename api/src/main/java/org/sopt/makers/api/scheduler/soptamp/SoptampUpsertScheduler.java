package org.sopt.makers.api.scheduler.soptamp;

import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.app.soptamp.service.SoptampBatchService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "sopt.soptamp.scheduler.upsert.enabled", havingValue = "true")
public class SoptampUpsertScheduler implements SchedulingConfigurer {

  private static final ZoneId KST = ZoneId.of("Asia/Seoul");

  private final SoptampBatchService soptampBatchService;

  @Override
  public void configureTasks(ScheduledTaskRegistrar registrar) {
    registrar.addTriggerTask(
        () -> {
          log.info("솝탬프 upsert 배치 자동 실행 시작");
          soptampBatchService.upsertAllSoptampUsers();
        },
        context ->
            new CronTrigger(soptampBatchService.getUpsertCron(), KST).nextExecution(context));
  }
}
