package org.sopt.makers.storage.db.playground.community.vote.repository;

import java.util.List;
import org.sopt.makers.storage.db.playground.community.vote.entity.VoteSelectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteSelectionJpaRepository extends JpaRepository<VoteSelectionEntity, Long> {

  boolean existsByVoteOptionIdInAndUserId(List<Long> voteOptionIds, Long userId);

  @Query(
      """
      SELECT COUNT(DISTINCT selection.userId)
      FROM VoteSelectionEntity selection
      WHERE selection.voteOptionId IN :voteOptionIds
      """)
  int countDistinctUsersByVoteOptionIds(@Param("voteOptionIds") List<Long> voteOptionIds);

  @Query(
      """
      SELECT selection.voteOptionId
      FROM VoteSelectionEntity selection
      WHERE selection.voteOptionId IN :voteOptionIds
        AND selection.userId = :userId
      """)
  List<Long> findSelectedOptionIdsByVoteOptionIdsAndUserId(
      @Param("voteOptionIds") List<Long> voteOptionIds, @Param("userId") Long userId);

  @Modifying
  @Query("DELETE FROM VoteSelectionEntity selection WHERE selection.voteOptionId IN :voteOptionIds")
  void deleteAllByVoteOptionIds(@Param("voteOptionIds") List<Long> voteOptionIds);
}
