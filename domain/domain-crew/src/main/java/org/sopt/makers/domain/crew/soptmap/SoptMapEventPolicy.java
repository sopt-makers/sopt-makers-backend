package org.sopt.makers.domain.crew.soptmap;

import java.time.LocalDate;
import java.util.List;

public record SoptMapEventPolicy(
    LocalDate startDate, LocalDate endDate, List<Integer> winnerOrders) {

  public SoptMapEventPolicy {
    winnerOrders = winnerOrders == null ? List.of() : List.copyOf(winnerOrders);
  }
}
