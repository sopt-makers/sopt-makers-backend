package org.sopt.makers.domain.playground.coffeechat.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.coffeechat.port.CoffeeChatUserPort;
import org.sopt.makers.domain.user.port.PlaygroundCoffeeChatUserPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoffeeChatUserAdapter implements CoffeeChatUserPort {

  private final PlaygroundCoffeeChatUserPort playgroundCoffeeChatUserPort;

  @Override
  public UserDetail getUserDetail(Long userId) {
    return toUserDetail(playgroundCoffeeChatUserPort.getUserDetail(userId));
  }

  @Override
  public List<UserDetail> getUserDetails(List<Long> userIds) {
    return playgroundCoffeeChatUserPort.getUserDetails(userIds).stream()
        .map(this::toUserDetail)
        .toList();
  }

  private UserDetail toUserDetail(PlaygroundCoffeeChatUserPort.UserDetail info) {
    return new UserDetail(
        info.id(),
        info.name(),
        info.profileImage(),
        info.phone(),
        info.email(),
        info.isPhoneBlind(),
        info.university(),
        info.activities().stream()
            .map(a -> new ActivityInfo(a.generation(), a.part(), a.isSopt()))
            .toList(),
        info.lastCareer().map(c -> new CareerDetail(c.companyName(), c.title())));
  }
}
