package org.sopt.makers.storage.db.admin.repository;

import java.util.Optional;
import org.sopt.makers.storage.db.admin.entity.LectureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LectureJpaRepository extends JpaRepository<LectureEntity, Long> {

  @Query("SELECT l FROM LectureEntity l WHERE l.id = :id")
  Optional<LectureEntity> findByIdWithSubLectures(@Param("id") Long id);
}
