package org.sopt.makers.storage.db.playground.wordchaingame.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.wordchaingame.WordChainGameRoom;
import org.sopt.makers.domain.playground.wordchaingame.port.WordChainGameRoomRepositoryPort;
import org.sopt.makers.storage.db.playground.wordchaingame.entity.WordChainGameRoomEntity;
import org.sopt.makers.storage.db.playground.wordchaingame.repository.WordChainGameQueryRepository;
import org.sopt.makers.storage.db.playground.wordchaingame.repository.WordChainGameRoomJpaRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WordChainGameRoomRepositoryAdapter implements WordChainGameRoomRepositoryPort {

  private final WordChainGameRoomJpaRepository wordChainGameRoomJpaRepository;
  private final WordChainGameQueryRepository wordChainGameQueryRepository;

  @Override
  public WordChainGameRoom save(WordChainGameRoom room) {
    return wordChainGameRoomJpaRepository.save(WordChainGameRoomEntity.from(room)).toDomain();
  }

  @Override
  public Optional<WordChainGameRoom> findById(Long id) {
    return wordChainGameRoomJpaRepository.findById(id).map(WordChainGameRoomEntity::toDomain);
  }

  @Override
  public boolean existsAny() {
    return wordChainGameRoomJpaRepository.existsByIdIsNotNull();
  }

  @Override
  public List<WordChainGameRoom> findAllRooms(Integer limit, Long cursor) {
    if (limit != null) {
      return wordChainGameQueryRepository.findAllLimitedRooms(limit, cursor);
    }
    return wordChainGameQueryRepository.findAllRooms();
  }

  @Override
  public Optional<WordChainGameRoom> findLatestRoom() {
    return wordChainGameRoomJpaRepository.findLatestRoom().map(WordChainGameRoomEntity::toDomain);
  }
}
