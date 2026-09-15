package org.sopt.makers.storage.db.app.calendar.repository;

import java.util.List;
import org.sopt.makers.storage.db.app.calendar.entity.CalendarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarJpaRepository extends JpaRepository<CalendarEntity, Long> {

  List<CalendarEntity> findAllByGenerationOrderByStartDateAscEndDateAsc(Integer generation);
}
