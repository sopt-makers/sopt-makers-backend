package org.sopt.makers.domain.playground.wordchaingame.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.playground.wordchaingame.WordChainGameRoom;

public interface WordChainGameRoomRepositoryPort {

  WordChainGameRoom save(WordChainGameRoom room);

  Optional<WordChainGameRoom> findById(Long id);

  boolean existsAny();

  List<WordChainGameRoom> findAllRooms(Integer limit, Long cursor);

  Optional<WordChainGameRoom> findLatestRoom();
}
