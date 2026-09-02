package org.sopt.makers.domain.app.soptamp.port;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.sopt.makers.domain.app.soptamp.SoptampUser;

public interface SoptampUserQueryPort {

  Optional<SoptampUser> findByUserId(Long userId);

  List<SoptampUser> findAllByUserIds(Collection<Long> userIds);

  List<SoptampUser> findAllByGeneration(Long generation);

  Map<Long, SoptampUser> findByUserIdsAsMap(Collection<Long> userIds);

  Optional<SoptampUser> findByNickname(String nickname);

  boolean existsByNickname(String nickname);

  SoptampUser save(SoptampUser soptampUser);
}
