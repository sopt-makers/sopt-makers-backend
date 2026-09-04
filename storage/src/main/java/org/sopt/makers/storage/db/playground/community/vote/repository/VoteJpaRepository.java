package org.sopt.makers.storage.db.playground.community.vote.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.playground.community.vote.entity.VoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteJpaRepository extends JpaRepository<VoteEntity, Long> {

  Optional<VoteEntity> findByPostId(Long postId);

  List<VoteEntity> findAllByPostIdIn(List<Long> postIds);

  @Modifying
  @Query("DELETE FROM VoteEntity vote WHERE vote.postId = :postId")
  void deleteByPostId(@Param("postId") Long postId);
}
