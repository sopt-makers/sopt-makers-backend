package org.sopt.makers.storage.db.playground.member.ask.repository;

import org.sopt.makers.storage.db.playground.member.ask.entity.UserAskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAskJpaRepository extends JpaRepository<UserAskEntity, Long> {}
