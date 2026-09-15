package org.sopt.makers.domain.playground.community.notification.port;

public interface CommunitySlackNotificationPort {

  void sendReportMessage(String message);

  void sendNotMakersMessage(String message);
}
