package org.sopt.makers.storage.db.playground.member.tl.repository;

import java.util.List;
import org.sopt.makers.storage.db.playground.member.tl.entity.TlUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TlUserJpaRepository extends JpaRepository<TlUserEntity, Long> {

  List<TlUserEntity> findAllByTlGeneration(Integer tlGeneration);
}
