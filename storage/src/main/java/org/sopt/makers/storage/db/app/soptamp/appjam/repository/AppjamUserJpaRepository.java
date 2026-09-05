package org.sopt.makers.storage.db.app.soptamp.appjam.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.soptamp.appjam.TeamNumber;
import org.sopt.makers.storage.db.app.soptamp.appjam.entity.AppjamUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppjamUserJpaRepository extends JpaRepository<AppjamUserEntity, Long> {

  List<AppjamUserEntity> findAllByTeamNumber(TeamNumber teamNumber);

  Optional<AppjamUserEntity> findTopByTeamNumberOrderById(TeamNumber teamNumber);

  Optional<AppjamUserEntity> findByUserId(Long userId);

  List<AppjamUserEntity> findAllByTeamNumberIn(Collection<TeamNumber> teamNumbers);

  List<AppjamUserEntity> findAllByUserIdIn(Collection<Long> userIds);

  boolean existsByUserId(Long userId);
}
