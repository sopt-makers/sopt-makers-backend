package org.sopt.makers.domain.app.soptamp.port;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.app.soptamp.SoptampUser;

public interface SoptampUserQueryPort {

  Optional<SoptampUser> findByUserId(Long userId);

  List<SoptampUser> findAllByUserIds(Collection<Long> userIds);

  List<SoptampUser> findAllByGeneration(Long generation);

  List<SoptampUser> findAllOfCurrentGeneration();

  List<SoptampUser> findAllByPartAndCurrentGeneration(Part part);

  Map<Long, SoptampUser> findByUserIdsAsMap(Collection<Long> userIds);

  Optional<SoptampUser> findByNickname(String nickname);

  List<Long> findAllUserIds();

  boolean existsByNickname(String nickname);

  boolean existsByNicknameAndUserIdNot(String nickname, Long userId);

  SoptampUser save(SoptampUser soptampUser);
}
