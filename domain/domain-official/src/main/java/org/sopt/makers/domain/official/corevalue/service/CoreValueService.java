package org.sopt.makers.domain.official.corevalue.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.corevalue.CoreValue;
import org.sopt.makers.domain.official.corevalue.port.CoreValueRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoreValueService {

  private final CoreValueRepositoryPort coreValueRepositoryPort;

  public List<CoreValue> findByGeneration(Integer generationId) {
    return coreValueRepositoryPort.findByGeneration(generationId);
  }
}
