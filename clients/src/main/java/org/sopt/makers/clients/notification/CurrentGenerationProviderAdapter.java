package org.sopt.makers.clients.notification;

import org.sopt.makers.domain.playground.member.ask.port.CurrentGenerationProvider;
import org.springframework.stereotype.Component;

@Component
public class CurrentGenerationProviderAdapter implements CurrentGenerationProvider {

  // TODO: 새 기수 시작 전 값 변경 필수
  private static final int CURRENT_GENERATION = 39;

  @Override
  public int getCurrentGeneration() {
    return CURRENT_GENERATION;
  }
}
