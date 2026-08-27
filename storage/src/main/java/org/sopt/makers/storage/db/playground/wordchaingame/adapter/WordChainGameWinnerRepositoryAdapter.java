package org.sopt.makers.storage.db.playground.wordchaingame.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.wordchaingame.WordChainGameWinner;
import org.sopt.makers.domain.playground.wordchaingame.port.WordChainGameWinnerRepositoryPort;
import org.sopt.makers.storage.db.playground.wordchaingame.entity.WordChainGameWinnerEntity;
import org.sopt.makers.storage.db.playground.wordchaingame.repository.WordChainGameQueryRepository;
import org.sopt.makers.storage.db.playground.wordchaingame.repository.WordChainGameWinnerJpaRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WordChainGameWinnerRepositoryAdapter implements WordChainGameWinnerRepositoryPort {

  private final WordChainGameWinnerJpaRepository wordChainGameWinnerJpaRepository;
  private final WordChainGameQueryRepository wordChainGameQueryRepository;

  @Override
  public WordChainGameWinner save(WordChainGameWinner winner) {
    return wordChainGameWinnerJpaRepository
        .save(WordChainGameWinnerEntity.from(winner))
        .toDomain();
  }

  @Override
  public Optional<WordChainGameWinner> findLatestByUserId(Long userId) {
    return wordChainGameWinnerJpaRepository
        .findFirstByUserIdOrderByIdDesc(userId)
        .map(WordChainGameWinnerEntity::toDomain);
  }

  @Override
  public List<WordChainGameWinner> findAllDesc() {
    return wordChainGameWinnerJpaRepository.findAllByOrderByIdDesc().stream()
        .map(WordChainGameWinnerEntity::toDomain)
        .toList();
  }

  @Override
  public List<WordChainGameWinner> findAllLimited(Integer limit, Integer cursor) {
    return wordChainGameQueryRepository.findAllLimitedWinners(limit, cursor);
  }
}
