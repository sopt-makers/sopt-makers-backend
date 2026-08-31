package org.sopt.makers.storage.db.crew.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.sopt.makers.storage.db.crew.entity.MeetingDemandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetingDemandJpaRepository extends JpaRepository<MeetingDemandEntity, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT demand FROM MeetingDemandEntity demand WHERE demand.id = :id")
  Optional<MeetingDemandEntity> findByIdForUpdate(@Param("id") Long id);
}
