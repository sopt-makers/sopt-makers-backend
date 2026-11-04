package org.sopt.makers.domain.playground.wordchaingame.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.playground.wordchaingame.WordChainGameWinner;

public interface WordChainGameWinnerRepositoryPort {

  WordChainGameWinner save(WordChainGameWinner winner);

  Optional<WordChainGameWinner> findLatestByUserId(Long userId);

  List<WordChainGameWinner> findAllDesc();

  List<WordChainGameWinner> findAllLimited(Integer limit, Integer cursor);
}
