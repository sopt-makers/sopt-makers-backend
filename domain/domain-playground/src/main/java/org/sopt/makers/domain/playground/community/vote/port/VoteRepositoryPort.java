package org.sopt.makers.domain.playground.community.vote.port;

import java.util.Optional;
import org.sopt.makers.domain.playground.community.vote.Vote;

public interface VoteRepositoryPort {

  Vote save(Vote vote);

  Optional<Vote> findByPostId(Long postId);

  void deleteByPostId(Long postId);
}
