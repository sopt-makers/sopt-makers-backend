package org.sopt.makers.storage.db.playground.member.relation.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.member.relation.UserReport;
import org.sopt.makers.domain.playground.member.relation.port.UserReportRepositoryPort;
import org.sopt.makers.storage.db.playground.member.relation.entity.UserReportEntity;
import org.sopt.makers.storage.db.playground.member.relation.repository.UserReportJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserReportRepositoryAdapter implements UserReportRepositoryPort {

  private final UserReportJpaRepository userReportJpaRepository;

  @Transactional
  @Override
  public UserReport save(UserReport userReport) {
    return userReportJpaRepository.save(UserReportEntity.fromDomain(userReport)).toDomain();
  }

  @Override
  public Optional<UserReport> findById(Long id) {
    return userReportJpaRepository.findById(id).map(UserReportEntity::toDomain);
  }

  @Transactional
  @Override
  public void deleteById(Long id) {
    userReportJpaRepository.deleteById(id);
  }
}
