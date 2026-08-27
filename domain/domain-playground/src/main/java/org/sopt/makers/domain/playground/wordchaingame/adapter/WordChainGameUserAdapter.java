package org.sopt.makers.domain.playground.wordchaingame.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.wordchaingame.port.WordChainGameUserPort;
import org.sopt.makers.domain.user.port.PlaygroundWordChainGameUserPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WordChainGameUserAdapter implements WordChainGameUserPort {

  private final PlaygroundWordChainGameUserPort playgroundWordChainGameUserPort;

  @Override
  public List<UserInfo> getUserInfosByIds(List<Long> userIds) {
    return playgroundWordChainGameUserPort.getUserInfosByIds(userIds).stream()
        .map(info -> new UserInfo(info.id(), info.name(), info.profileImage()))
        .toList();
  }
}
