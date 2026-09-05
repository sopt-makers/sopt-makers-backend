package org.sopt.makers.storage.db.playground.member.tl.repository;

import org.sopt.makers.storage.db.playground.member.tl.entity.TlUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TlUserJpaRepository extends JpaRepository<TlUserEntity, Long> {}
