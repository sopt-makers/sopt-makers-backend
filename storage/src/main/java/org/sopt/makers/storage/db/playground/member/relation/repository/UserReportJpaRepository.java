package org.sopt.makers.storage.db.playground.member.relation.repository;

import org.sopt.makers.storage.db.playground.member.relation.entity.UserReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserReportJpaRepository extends JpaRepository<UserReportEntity, Long> {}
