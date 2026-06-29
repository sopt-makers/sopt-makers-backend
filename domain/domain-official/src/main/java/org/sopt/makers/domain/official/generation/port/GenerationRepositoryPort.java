package org.sopt.makers.domain.official.generation.port;

import java.util.Optional;
import org.sopt.makers.domain.official.generation.Generation;

public interface GenerationRepositoryPort {

  Optional<Generation> findLatest();
}
