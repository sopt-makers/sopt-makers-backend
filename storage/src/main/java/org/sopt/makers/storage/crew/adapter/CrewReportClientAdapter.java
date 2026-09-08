package org.sopt.makers.storage.crew.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.meeting.port.PlaygroundCrewStatsPort;
import org.sopt.makers.domain.playground.report.port.CrewReportClientPort;
import org.springframework.stereotype.Component;

/**
 * 예전에는 별도로 운영되던 Crew 서버의 {@code GET /internal/meeting/stats/fastest-applied/:orgId}를 HTTP로 호출했지만,
 * domain-crew가 같은 기능을 대체하는 내부 Port({@link PlaygroundCrewStatsPort})를 제공하므로 이를 감싸는 어댑터로 대체한다.
 * domain-playground는 domain-crew를 직접 의존할 수 없으므로(hexagonal 격리), 두 도메인을 모두 참조 가능한 storage 모듈에 이 어댑터를
 * 둔다({@code PlaygroundCrewRelationAdapter}와 동일 패턴).
 */
@Component
@RequiredArgsConstructor
public class CrewReportClientAdapter implements CrewReportClientPort {

  private final PlaygroundCrewStatsPort playgroundCrewStatsPort;

  @Override
  public List<String> getFastestAppliedGroupTitles(Long userId, int limit, int year) {
    return playgroundCrewStatsPort.findFastestAppliedMeetings(userId, limit, year).stream()
        .map(PlaygroundCrewStatsPort.FastestAppliedMeeting::title)
        .toList();
  }
}
