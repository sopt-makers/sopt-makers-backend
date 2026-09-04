package org.sopt.makers.storage.db.playground.report.adapter;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.report.port.WordChainGameStatsPort;
import org.sopt.makers.storage.db.playground.report.entity.WordEntity;
import org.sopt.makers.storage.db.playground.report.repository.WordChainGameWinnerJpaRepository;
import org.sopt.makers.storage.db.playground.report.repository.WordJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WordChainGameStatsRepositoryAdapter implements WordChainGameStatsPort {

  private final WordJpaRepository wordJpaRepository;
  private final WordChainGameWinnerJpaRepository wordChainGameWinnerJpaRepository;

  @Override
  public List<String> findWordsByMemberIdAndCreatedAtBetween(Long memberId, LocalDateTime start, LocalDateTime end) {
    return wordJpaRepository.findAllByMemberIdAndCreatedAtBetween(memberId, start, end).stream()
        .map(WordEntity::getWord)
        .toList();
  }

  @Override
  public long countWinnersByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end) {
    return wordChainGameWinnerJpaRepository.countByUserIdAndRoomCreatedAtBetween(userId, start, end);
  }
}
