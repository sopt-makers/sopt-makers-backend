package org.sopt.makers.domain.crew.soptmap.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.soptmap.SoptMapUser;
import org.sopt.makers.domain.crew.soptmap.port.SoptMapUserPort;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.port.CrewSoptMapUserPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SoptMapUserAdapter implements SoptMapUserPort {

  private final CrewSoptMapUserPort crewSoptMapUserPort;

  @Override
  public Optional<SoptMapUser> findById(Long userId) {
    return crewSoptMapUserPort.findById(userId).map(this::toSoptMapUser);
  }

  @Override
  public List<SoptMapUser> findAllByIds(List<Long> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return List.of();
    }
    return crewSoptMapUserPort.findAllByIds(userIds).stream().map(this::toSoptMapUser).toList();
  }

  private SoptMapUser toSoptMapUser(User user) {
    return new SoptMapUser(user.id(), user.profile() == null ? null : user.profile().name());
  }
}
