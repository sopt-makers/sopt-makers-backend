package org.sopt.makers.storage.db.official.repository;

import java.util.List;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.storage.db.official.entity.ReviewEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewJpaRepository extends JpaRepository<ReviewEntity, Long> {

  boolean existsByUrl(String url);

  List<ReviewEntity> findAllByAuthor(String author);

  @Query(
      """
      SELECT r FROM ReviewEntity r
      WHERE r.part = :part
      ORDER BY function('RANDOM')
      """)
  List<ReviewEntity> findRandomByPart(@Param("part") Part part, Pageable pageable);
}
