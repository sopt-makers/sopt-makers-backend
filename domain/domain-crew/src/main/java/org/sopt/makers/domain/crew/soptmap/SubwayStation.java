package org.sopt.makers.domain.crew.soptmap;

import java.time.LocalDateTime;
import java.util.List;

public record SubwayStation(
    Long id,
    String name,
    List<SubwayLine> lines,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public SubwayStation {
    lines = lines == null ? List.of() : List.copyOf(lines);
  }
}
