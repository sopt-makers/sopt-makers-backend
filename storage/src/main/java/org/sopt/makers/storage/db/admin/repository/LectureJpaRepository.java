package org.sopt.makers.storage.db.admin.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.attendance.LectureStatus;
import org.sopt.makers.storage.db.admin.entity.LectureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LectureJpaRepository extends JpaRepository<LectureEntity, Long> {

  @Query("SELECT l FROM LectureEntity l WHERE l.id = :id")
  Optional<LectureEntity> findByIdWithSubLectures(@Param("id") Long id);

  @Query(
      "SELECT l FROM LectureEntity l WHERE l.generation = :generation"
          + " AND (:part IS NULL OR l.part = :part OR l.part = org.sopt.makers.core.type.Part.ALL)")
  List<LectureEntity> findAllByGenerationAndPart(
      @Param("generation") int generation, @Param("part") Part part);

  @Modifying(clearAutomatically = true)
  @Query("UPDATE LectureEntity l SET l.status = :status WHERE l.id = :id")
  void updateStatus(@Param("id") Long id, @Param("status") LectureStatus status);
}
