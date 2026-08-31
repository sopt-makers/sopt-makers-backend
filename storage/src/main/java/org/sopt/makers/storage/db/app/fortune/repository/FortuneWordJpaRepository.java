package org.sopt.makers.storage.db.app.fortune.repository;

import java.util.List;
import org.sopt.makers.storage.db.app.fortune.entity.FortuneWordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FortuneWordJpaRepository extends JpaRepository<FortuneWordEntity, Long> {

  @Query("SELECT f.id FROM FortuneWordEntity f")
  List<Long> findAllIds();
}
