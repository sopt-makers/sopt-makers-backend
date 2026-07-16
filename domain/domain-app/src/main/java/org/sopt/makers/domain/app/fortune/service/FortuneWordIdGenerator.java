package org.sopt.makers.domain.app.fortune.service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.fortune.port.FortuneWordRepositoryPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FortuneWordIdGenerator {

  private final FortuneWordRepositoryPort fortuneWordRepositoryPort;

  public Long generate() {
    List<Long> ids = fortuneWordRepositoryPort.findAllIds();
    return ids.get(ThreadLocalRandom.current().nextInt(ids.size()));
  }
}
