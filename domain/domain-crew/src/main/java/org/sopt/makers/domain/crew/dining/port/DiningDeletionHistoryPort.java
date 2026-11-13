package org.sopt.makers.domain.crew.dining.port;

import org.sopt.makers.domain.crew.dining.model.DiningDeletionHistory;

public interface DiningDeletionHistoryPort {
  void save(DiningDeletionHistory history);
}
