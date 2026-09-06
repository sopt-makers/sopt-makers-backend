package org.sopt.makers.storage.db.playground.wordchaingame.repository;

import java.util.Optional;
import org.sopt.makers.storage.db.playground.wordchaingame.entity.WordChainGameRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WordChainGameRoomJpaRepository
    extends JpaRepository<WordChainGameRoomEntity, Long> {

  boolean existsByIdIsNotNull();

  @Query("SELECT r FROM WordChainGameRoomEntity r ORDER BY r.createdAt DESC LIMIT 1")
  Optional<WordChainGameRoomEntity> findLatestRoom();
}
