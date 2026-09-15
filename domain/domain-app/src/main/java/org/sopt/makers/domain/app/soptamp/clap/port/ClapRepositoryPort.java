package org.sopt.makers.domain.app.soptamp.clap.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.soptamp.clap.Clap;
import org.springframework.data.domain.Pageable;

public interface ClapRepositoryPort {

  Optional<Clap> findByUserIdAndStampId(Long userId, Long stampId);

  List<Clap> findAllByStampIdOrderByClapCountDesc(Long stampId, Pageable pageable);

  long countByStampId(Long stampId);

  List<Clap> findAllByUserId(Long userId);

  Clap save(Clap clap);

  void deleteAll();
}
