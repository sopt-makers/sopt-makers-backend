package org.sopt.makers.storage.db.playground.community.vote.repository;

import java.util.List;
import org.sopt.makers.storage.db.playground.community.vote.entity.VoteOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteOptionJpaRepository extends JpaRepository<VoteOptionEntity, Long> {

  List<VoteOptionEntity> findAllByVoteId(Long voteId);

  List<VoteOptionEntity> findAllByVoteIdIn(List<Long> voteIds);

  @Modifying
  @Query("UPDATE VoteOptionEntity option SET option.voteCount = option.voteCount + 1 WHERE option.id = :optionId")
  void increaseVoteCountDirect(@Param("optionId") Long optionId);

  @Modifying
  @Query("DELETE FROM VoteOptionEntity option WHERE option.voteId = :voteId")
  void deleteAllByVoteId(@Param("voteId") Long voteId);
}
