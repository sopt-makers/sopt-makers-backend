package org.sopt.makers.domain.crew.flash.port;

import java.util.Optional;
import org.sopt.makers.domain.crew.flash.Flash;

public interface FlashRepositoryPort {

  Flash save(Flash flash);

  Optional<Flash> findByMeetingId(Long meetingId);

  void deleteByMeetingId(Long meetingId);
}
