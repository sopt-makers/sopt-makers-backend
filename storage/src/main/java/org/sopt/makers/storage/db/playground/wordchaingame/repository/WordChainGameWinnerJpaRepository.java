package org.sopt.makers.storage.db.playground.wordchaingame.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.playground.wordchaingame.entity.WordChainGameWinnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordChainGameWinnerJpaRepository
    extends JpaRepository<WordChainGameWinnerEntity, Long> {

  Optional<WordChainGameWinnerEntity> findFirstByUserIdOrderByIdDesc(Long userId);

  List<WordChainGameWinnerEntity> findAllByOrderByIdDesc();
}
