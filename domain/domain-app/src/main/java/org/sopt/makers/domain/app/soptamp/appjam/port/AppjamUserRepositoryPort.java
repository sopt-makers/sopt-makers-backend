package org.sopt.makers.domain.app.soptamp.appjam.port;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamUser;
import org.sopt.makers.domain.app.soptamp.appjam.TeamNumber;

public interface AppjamUserRepositoryPort {

  List<AppjamUser> findAll();

  List<AppjamUser> findAllByTeamNumber(TeamNumber teamNumber);

  Optional<AppjamUser> findTopByTeamNumberOrderById(TeamNumber teamNumber);

  Optional<AppjamUser> findByUserId(Long userId);

  List<AppjamUser> findAllByTeamNumberIn(Collection<TeamNumber> teamNumbers);

  List<AppjamUser> findAllByUserIdIn(Collection<Long> userIds);

  boolean existsByUserId(Long userId);
}
