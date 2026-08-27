package org.sopt.makers.storage.db.crew.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.storage.db.crew.entity.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeJpaRepository extends JpaRepository<NoticeEntity, Long> {

  List<NoticeEntity>
      findAllByExposeStartDateLessThanEqualAndExposeEndDateGreaterThanEqualOrderByCreatedDateDesc(
          LocalDateTime startAt, LocalDateTime endAt);
}
