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
    if (ids.isEmpty()) {
      throw new IllegalStateException("선택할 수 있는 FortuneWord가 없습니다. fortune_word 시드 데이터를 확인해야 합니다.");
    }
    return ids.get(ThreadLocalRandom.current().nextInt(ids.size()));
  }
}
