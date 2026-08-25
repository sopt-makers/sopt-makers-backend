package org.sopt.makers.domain.app.fortune.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.fortune.FortuneWord;

public interface FortuneWordRepositoryPort {

  Optional<FortuneWord> findById(Long id);

  List<Long> findAllIds();
}
