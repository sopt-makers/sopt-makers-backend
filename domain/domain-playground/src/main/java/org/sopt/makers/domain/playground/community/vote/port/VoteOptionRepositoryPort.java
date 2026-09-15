package org.sopt.makers.domain.playground.community.vote.port;

import java.util.List;
import org.sopt.makers.domain.playground.community.vote.VoteOption;

public interface VoteOptionRepositoryPort {

  List<VoteOption> findAllByIds(List<Long> optionIds);

  void increaseVoteCount(Long optionId);

  void deleteAllByVoteId(Long voteId);
}
