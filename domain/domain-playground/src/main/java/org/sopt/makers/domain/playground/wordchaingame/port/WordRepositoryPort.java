package org.sopt.makers.domain.playground.wordchaingame.port;

import java.util.Optional;
import org.sopt.makers.domain.playground.wordchaingame.Word;

public interface WordRepositoryPort {

  Word save(Word word);

  Optional<Word> findLastWordByRoomId(Long roomId);

  boolean existsByWordAndRoomId(String word, Long roomId);
}
