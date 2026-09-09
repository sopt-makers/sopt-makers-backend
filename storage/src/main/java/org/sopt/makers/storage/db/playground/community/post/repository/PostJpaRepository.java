package org.sopt.makers.storage.db.playground.community.post.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.sopt.makers.domain.playground.community.CommunityCategoryGroup;
import org.sopt.makers.storage.db.playground.community.post.entity.PostEntity;
import org.sopt.makers.storage.db.playground.community.post.querydsl.PostQuerydslRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostJpaRepository extends JpaRepository<PostEntity, Long>, PostQuerydslRepository {

  @EntityGraph(attributePaths = {"category", "category.parent"})
  @Query("SELECT post FROM PostEntity post WHERE post.id = :postId")
  Optional<PostEntity> findByIdWithCategory(@Param("postId") Long postId);

  List<PostEntity> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

  Integer countAllByWriterIdAndCreatedAtBetween(
      Long writerId, LocalDateTime start, LocalDateTime end);

  @Modifying
  @Query("UPDATE PostEntity post SET post.hits = post.hits + 1 WHERE post.id = :postId")
  void increaseHitsDirect(@Param("postId") Long postId);

  @EntityGraph(attributePaths = {"category"})
  List<PostEntity> findTop5ByCategory_CodeInOrderByCreatedAtDesc(
      List<CommunityCategoryCode> categoryCodes);

  @EntityGraph(attributePaths = {"category", "category.parent"})
  List<PostEntity> findTop3ByCategory_CategoryGroupInOrderByCreatedAtDesc(
      List<CommunityCategoryGroup> categoryGroups);

  @EntityGraph(attributePaths = {"category", "category.parent"})
  Optional<PostEntity> findFirstByCategory_CodeInOrderByCreatedAtDesc(
      List<CommunityCategoryCode> categoryCodes);

  @Query(
      """
      SELECT COUNT(post)
      FROM PostEntity post
      WHERE post.writerId = :writerId
        AND post.category.categoryGroup = :categoryGroup
      """)
  long countByWriterIdAndCategoryGroup(
      @Param("writerId") Long writerId,
      @Param("categoryGroup") CommunityCategoryGroup categoryGroup);

  default long countSopticleByWriterId(Long writerId) {
    return countByWriterIdAndCategoryGroup(writerId, CommunityCategoryGroup.SOPTICLE);
  }

  @EntityGraph(attributePaths = {"category"})
  Optional<PostEntity> findFirstByIsHotTrueOrderByCreatedAtDesc();

  @EntityGraph(attributePaths = {"category", "category.parent"})
  List<PostEntity> findByCreatedAtAfterOrderByHitsDesc(LocalDateTime after, Pageable pageable);

  @EntityGraph(attributePaths = {"category", "category.parent"})
  List<PostEntity> findByCreatedAtGreaterThanEqualAndIsReportedFalseOrderByCreatedAtDescIdDesc(
      LocalDateTime since);
}
