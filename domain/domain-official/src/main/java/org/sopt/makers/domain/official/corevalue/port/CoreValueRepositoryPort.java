package org.sopt.makers.domain.official.corevalue.port;

import java.util.List;
import org.sopt.makers.domain.official.corevalue.CoreValue;

public interface CoreValueRepositoryPort {

  List<CoreValue> findByGeneration(Integer generationId);
}
