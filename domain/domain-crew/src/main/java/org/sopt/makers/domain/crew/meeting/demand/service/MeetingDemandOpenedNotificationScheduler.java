package org.sopt.makers.domain.crew.meeting.demand.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile({"local", "dev", "prod", "traffic", "lambda-dev"})
@RequiredArgsConstructor
public class MeetingDemandOpenedNotificationScheduler {

  private final MeetingDemandOpenedNotificationService openedNotificationService;

  @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul")
  public void sendPendingNotifications() {
    openedNotificationService.sendPendingNotifications();
  }
}
