package org.sopt.makers.domain.playground.community.anonymous.port;

import java.util.List;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousProfileImage;

public interface AnonymousProfileImageRepositoryPort {

  List<AnonymousProfileImage> findAllByIdNot(Long excludeId);
}
