package org.sopt.makers.domain.crew.soptmap.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.crew.soptmap.SoptMapUser;

public interface SoptMapUserPort {

  Optional<SoptMapUser> findById(Long userId);

  List<SoptMapUser> findAllByIds(List<Long> userIds);
}
