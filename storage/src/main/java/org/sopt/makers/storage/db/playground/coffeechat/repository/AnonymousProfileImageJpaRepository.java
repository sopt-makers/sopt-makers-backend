package org.sopt.makers.storage.db.playground.coffeechat.repository;

import org.sopt.makers.storage.db.playground.coffeechat.entity.AnonymousProfileImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnonymousProfileImageJpaRepository
    extends JpaRepository<AnonymousProfileImageEntity, Long> {}
