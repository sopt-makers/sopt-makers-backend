package org.sopt.makers.storage.db.app.soptamp.adapter;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.app.soptamp.port.SoptampPartMemberCountPort;
import org.sopt.makers.domain.user.Role;
import org.sopt.makers.storage.db.user.entity.UserActivityHistoryEntity;
import org.sopt.makers.storage.db.user.repository.UserActivityHistoryJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoptampPartMemberCountAdapter implements SoptampPartMemberCountPort {

  private static final Set<Part> RANKING_PARTS =
      EnumSet.of(Part.PLAN, Part.DESIGN, Part.WEB, Part.IOS, Part.ANDROID, Part.SERVER);

  private final UserActivityHistoryJpaRepository userActivityHistoryJpaRepository;

  @Override
  public Map<Part, Long> countMembersByPart(int generation) {
    return userActivityHistoryJpaRepository.findByGenerationAndIsSopt(generation, true).stream()
        .filter(activity -> activity.getRole() == Role.MEMBER)
        .filter(activity -> RANKING_PARTS.contains(activity.getPart()))
        .collect(
            Collectors.groupingBy(
                UserActivityHistoryEntity::getPart,
                () -> new EnumMap<>(Part.class),
                Collectors.counting()));
  }
}
