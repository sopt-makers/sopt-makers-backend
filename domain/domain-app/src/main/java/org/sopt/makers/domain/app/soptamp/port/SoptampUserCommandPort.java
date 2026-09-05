package org.sopt.makers.domain.app.soptamp.port;

import org.sopt.makers.domain.app.soptamp.SoptampPart;
import org.sopt.makers.domain.app.soptamp.SoptampUser;

public interface SoptampUserCommandPort {

  SoptampUser updateProfileMessage(Long userId, String profileMessage);

  void create(Long userId, String nickname, Long generation, SoptampPart part);

  void updateChangedGenerationInfo(Long userId, Long generation, SoptampPart part, String nickname);

  void deleteAll();
}
