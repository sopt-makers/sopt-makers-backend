package org.sopt.makers.storage.db.playground.community.anonymous.repository;

import java.util.List;
import org.sopt.makers.storage.db.playground.community.anonymous.entity.AnonymousProfileImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnonymousProfileImageJpaRepository
    extends JpaRepository<AnonymousProfileImageEntity, Long> {

  List<AnonymousProfileImageEntity> findAllByIdNot(Long id);
}
