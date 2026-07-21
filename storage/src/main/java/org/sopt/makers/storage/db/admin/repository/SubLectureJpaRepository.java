package org.sopt.makers.storage.db.admin.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.admin.entity.SubLectureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubLectureJpaRepository extends JpaRepository<SubLectureEntity, Long> {

  @Query("SELECT sl FROM SubLectureEntity sl JOIN FETCH sl.lecture WHERE sl.id = :id")
  Optional<SubLectureEntity> findByIdWithLecture(@Param("id") Long id);

  Optional<SubLectureEntity> findByLectureIdAndRound(Long lectureId, int round);

  @Query(
      "SELECT sl FROM SubLectureEntity sl JOIN FETCH sl.lecture WHERE sl.lecture.id = :lectureId")
  List<SubLectureEntity> findAllByLectureId(@Param("lectureId") Long lectureId);

  @Query(
      "SELECT sl FROM SubLectureEntity sl JOIN FETCH sl.lecture"
          + " WHERE sl.lecture.id IN :lectureIds")
  List<SubLectureEntity> findAllByLectureIdIn(@Param("lectureIds") List<Long> lectureIds);

  @Modifying(clearAutomatically = true)
  @Query("UPDATE SubLectureEntity sl SET sl.code = :code, sl.startAt = :startAt WHERE sl.id = :id")
  void updateCodeAndStartAt(
      @Param("id") Long id, @Param("code") String code, @Param("startAt") LocalDateTime startAt);

  void deleteAllByLectureId(Long lectureId);
}
