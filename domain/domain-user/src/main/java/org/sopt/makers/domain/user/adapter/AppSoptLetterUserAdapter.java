package org.sopt.makers.domain.user.adapter;

import java.util.OptionalInt;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.port.AppSoptLetterUserPort;
import org.sopt.makers.domain.user.service.UserQueryService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppSoptLetterUserAdapter implements AppSoptLetterUserPort {

  private final UserQueryService userQueryService;

  @Override
  public Set<Long> findSoptUserIdsByGeneration(int generation) {
    return Set.copyOf(userQueryService.findUserIdsByGenerationAndIsSopt(generation, true));
  }

  @Override
  public OptionalInt findLastSoptGeneration(Long userId) {
    return userQueryService.findWithActivitiesById(userId).stream()
        .flatMap(user -> user.activities().activities().stream())
        .filter(Activity::isSopt)
        .mapToInt(Activity::generation)
        .max();
  }
}
