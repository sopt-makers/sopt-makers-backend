package org.sopt.makers.domain.playground.report.port;

import java.time.LocalDateTime;
import java.util.List;

public interface WordChainGameStatsPort {

  List<String> findWordsByMemberIdAndCreatedAtBetween(Long memberId, LocalDateTime start, LocalDateTime end);

  long countWinnersByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
}
