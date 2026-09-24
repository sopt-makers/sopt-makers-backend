package org.sopt.makers.storage.db.playground.wordchaingame.repository;

import java.util.Optional;
import org.sopt.makers.storage.db.playground.wordchaingame.entity.WordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordJpaRepository extends JpaRepository<WordEntity, Long> {

  Optional<WordEntity> findFirstByRoomIdOrderByCreatedAtDesc(Long roomId);

  boolean existsByWordAndRoomId(String word, Long roomId);
}
