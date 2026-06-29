package org.sopt.makers.storage.db.official.repository;

import java.util.List;
import org.sopt.makers.storage.db.official.entity.FaqEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FaqJpaRepository extends JpaRepository<FaqEntity, Long> {

  @Query("SELECT f FROM FaqEntity f ORDER BY f.part ASC")
  List<FaqEntity> findAllOrderByPart();
}
