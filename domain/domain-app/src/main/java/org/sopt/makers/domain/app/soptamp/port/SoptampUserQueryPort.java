package org.sopt.makers.domain.app.soptamp.port;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.app.soptamp.SoptampUser;

public interface SoptampUserQueryPort {

  List<SoptampUser> findAllOfCurrentGeneration();

  List<SoptampUser> findAllByPartAndCurrentGeneration(Part part);

  Optional<SoptampUser> findByNickname(String nickname);

  Optional<SoptampUser> findById(Long userId);

  Map<Long, SoptampUser> findByIdsAsMap(List<Long> userIds);
}
