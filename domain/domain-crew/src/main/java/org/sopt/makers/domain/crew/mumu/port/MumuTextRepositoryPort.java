package org.sopt.makers.domain.crew.mumu.port;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.crew.mumu.MumuText;

public interface MumuTextRepositoryPort {

  MumuText save(MumuText mumuText);

  Optional<MumuText> findById(Long id);

  Optional<MumuText> findActiveAt(LocalDateTime dateTime);

  List<MumuText> findAll();

  List<MumuText> findOverlapping(
      Long excludedId, LocalDateTime showStartDate, LocalDateTime showEndDate);

  void delete(MumuText mumuText);
}
