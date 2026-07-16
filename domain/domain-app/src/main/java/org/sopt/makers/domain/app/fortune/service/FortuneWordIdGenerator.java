package org.sopt.makers.domain.app.fortune.service;

import java.util.List;
import java.util.random.RandomGenerator;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.fortune.port.FortuneWordRepositoryPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FortuneWordIdGenerator {

  private final FortuneWordRepositoryPort fortuneWordRepositoryPort;
  private final RandomGenerator random = RandomGenerator.getDefault();

  public Long generate() {
    List<Long> ids = fortuneWordRepositoryPort.findAllIds();
    return ids.get(random.nextInt(ids.size()));
  }
}
