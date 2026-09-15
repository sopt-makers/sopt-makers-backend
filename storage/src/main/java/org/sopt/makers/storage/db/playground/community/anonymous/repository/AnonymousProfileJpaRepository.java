package org.sopt.makers.storage.db.playground.community.anonymous.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.playground.community.anonymous.entity.AnonymousProfileEntity;
import org.sopt.makers.storage.db.playground.community.anonymous.querydsl.AnonymousProfileQuerydslRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnonymousProfileJpaRepository
    extends JpaRepository<AnonymousProfileEntity, Long>, AnonymousProfileQuerydslRepository {

  @EntityGraph(attributePaths = {"nickname", "profileImage"})
  Optional<AnonymousProfileEntity> findByUserIdAndPostId(Long userId, Long postId);

  @EntityGraph(attributePaths = {"nickname", "profileImage"})
  List<AnonymousProfileEntity> findAllByPostId(Long postId);

  @EntityGraph(attributePaths = {"nickname", "profileImage"})
  @Override
  Optional<AnonymousProfileEntity> findById(Long id);

  @EntityGraph(attributePaths = {"nickname", "profileImage"})
  List<AnonymousProfileEntity> findAllByIdIn(List<Long> ids);

  @Query(
      """
      SELECT DISTINCT ap.nickname.nickname
      FROM AnonymousProfileEntity ap
      WHERE ap.postId = :postId
      AND ap.nickname.nickname IN :nicknames
      """)
  List<String> findNicknamesByPostIdAndNicknamesIn(
      @Param("postId") Long postId, @Param("nicknames") List<String> nicknames);

  @Query(
      """
      SELECT DISTINCT ap
      FROM AnonymousProfileEntity ap
      JOIN FETCH ap.nickname
      JOIN FETCH ap.profileImage
      WHERE ap.postId = :postId
      AND ap.nickname.nickname IN :nicknames
      """)
  List<AnonymousProfileEntity> findByPostIdAndNicknamesIn(
      @Param("postId") Long postId, @Param("nicknames") List<String> nicknames);
}
