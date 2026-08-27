package org.sopt.makers.storage.db.playground.wordchaingame.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.wordchaingame.Word;
import org.sopt.makers.domain.playground.wordchaingame.port.WordRepositoryPort;
import org.sopt.makers.storage.db.playground.wordchaingame.entity.WordEntity;
import org.sopt.makers.storage.db.playground.wordchaingame.repository.WordJpaRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WordRepositoryAdapter implements WordRepositoryPort {

  private final WordJpaRepository wordJpaRepository;

  @Override
  public Word save(Word word) {
    return wordJpaRepository.save(WordEntity.from(word)).toDomain();
  }

  @Override
  public Optional<Word> findLastWordByRoomId(Long roomId) {
    return wordJpaRepository.findFirstByRoomIdOrderByCreatedAtDesc(roomId).map(WordEntity::toDomain);
  }

  @Override
  public boolean existsByWordAndRoomId(String word, Long roomId) {
    return wordJpaRepository.existsByWordAndRoomId(word, roomId);
  }
}
