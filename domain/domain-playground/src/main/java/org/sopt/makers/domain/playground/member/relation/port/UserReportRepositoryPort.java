package org.sopt.makers.domain.playground.member.relation.port;

import java.util.Optional;
import org.sopt.makers.domain.playground.member.relation.UserReport;

public interface UserReportRepositoryPort {

  UserReport save(UserReport userReport);

  Optional<UserReport> findById(Long id);

  void deleteById(Long id);
}
