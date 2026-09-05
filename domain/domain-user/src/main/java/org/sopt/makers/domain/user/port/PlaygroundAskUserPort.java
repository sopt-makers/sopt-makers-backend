package org.sopt.makers.domain.user.port;

public interface PlaygroundAskUserPort {

  String getName(Long userId);

  String getPhoneNumber(Long userId);

  int getLastSoptGeneration(Long userId);
}
