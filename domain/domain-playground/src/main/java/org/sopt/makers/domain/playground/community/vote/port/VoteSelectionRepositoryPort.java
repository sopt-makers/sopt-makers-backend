package org.sopt.makers.domain.playground.community.vote.port;

import java.util.List;
import java.util.Set;
import org.sopt.makers.domain.playground.community.vote.VoteSelection;

public interface VoteSelectionRepositoryPort {

  VoteSelection save(VoteSelection voteSelection);

  boolean existsByVoteOptionIdsAndUserId(List<Long> voteOptionIds, Long userId);

  int countDistinctUsersByVoteOptionIds(List<Long> voteOptionIds);

  Set<Long> findSelectedOptionIdsByVoteOptionIdsAndUserId(List<Long> voteOptionIds, Long userId);

  void deleteAllByVoteOptionIds(List<Long> voteOptionIds);
}
