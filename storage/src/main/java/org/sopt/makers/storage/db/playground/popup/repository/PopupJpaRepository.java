package org.sopt.makers.storage.db.playground.popup.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.sopt.makers.storage.db.playground.popup.entity.PopupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PopupJpaRepository extends JpaRepository<PopupEntity, Long> {

  @Query(
      "SELECT p FROM PopupEntity p WHERE :currentDate BETWEEN p.startDate AND p.endDate ORDER BY p.startDate ASC LIMIT 1")
  Optional<PopupEntity> findFirstCurrentPopup(@Param("currentDate") LocalDate currentDate);
}
