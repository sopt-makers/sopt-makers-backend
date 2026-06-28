package org.sopt.makers.storage.db.admin.repository;

import java.util.Optional;
import org.sopt.makers.storage.db.admin.entity.SubLectureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubLectureJpaRepository extends JpaRepository<SubLectureEntity, Long> {

  @Query("SELECT sl FROM SubLectureEntity sl JOIN FETCH sl.lecture WHERE sl.id = :id")
  Optional<SubLectureEntity> findByIdWithLecture(@Param("id") Long id);

  Optional<SubLectureEntity> findByLectureIdAndRound(Long lectureId, int round);
}
