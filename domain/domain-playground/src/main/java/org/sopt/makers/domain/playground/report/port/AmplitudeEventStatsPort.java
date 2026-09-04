package org.sopt.makers.domain.playground.report.port;

public interface AmplitudeEventStatsPort {

  int countAllByUserIdAndEventTypeAndEventTimeContains(String userId, String eventType, String eventTimeContains);

  int countAllByUserIdAndEventTypeAndEventTimeBetween(
      String userId, String eventType, String startEventTime, String endEventTime);

  int countByUserIdAndEventTypeAndPagePathAndEventTimeContains(
      String userId, String eventType, String pagePath, String eventTimeContains);

  int countByUserIdAndEventTypeAndPagePathAndEventTimeBetween(
      String userId, String eventType, String pagePath, String startEventTime, String endEventTime);
}
