package org.sopt.makers.storage.db.playground.report.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.report.port.AmplitudeEventStatsPort;
import org.sopt.makers.storage.db.playground.report.repository.AmplitudeEventRawDataJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AmplitudeEventStatsRepositoryAdapter implements AmplitudeEventStatsPort {

  private final AmplitudeEventRawDataJpaRepository amplitudeEventRawDataJpaRepository;

  @Override
  public int countAllByUserIdAndEventTypeAndEventTimeContains(
      String userId, String eventType, String eventTimeContains) {
    return amplitudeEventRawDataJpaRepository.countAllByUserIdAndEventTypeAndEventTimeContains(
        userId, eventType, eventTimeContains);
  }

  @Override
  public int countAllByUserIdAndEventTypeAndEventTimeBetween(
      String userId, String eventType, String startEventTime, String endEventTime) {
    return amplitudeEventRawDataJpaRepository.countAllByUserIdAndEventTypeAndEventTimeBetween(
        userId, eventType, startEventTime, endEventTime);
  }

  @Override
  public int countByUserIdAndEventTypeAndPagePathAndEventTimeContains(
      String userId, String eventType, String pagePath, String eventTimeContains) {
    return amplitudeEventRawDataJpaRepository.countByUserIdAndEventTypeAndEventPropertiesPagePathAndEventTimeContains(
        userId, eventType, pagePath, eventTimeContains);
  }

  @Override
  public int countByUserIdAndEventTypeAndPagePathAndEventTimeBetween(
      String userId, String eventType, String pagePath, String startEventTime, String endEventTime) {
    return amplitudeEventRawDataJpaRepository.countByUserIdAndEventTypeAndEventPropertiesPagePathAndEventTimeBetween(
        userId, eventType, pagePath, startEventTime, endEventTime);
  }
}
