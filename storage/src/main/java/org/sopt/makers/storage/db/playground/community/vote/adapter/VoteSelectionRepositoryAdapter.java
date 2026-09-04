package org.sopt.makers.storage.db.playground.community.vote.adapter;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.vote.VoteSelection;
import org.sopt.makers.domain.playground.community.vote.port.VoteSelectionRepositoryPort;
import org.sopt.makers.storage.db.playground.community.vote.entity.VoteSelectionEntity;
import org.sopt.makers.storage.db.playground.community.vote.repository.VoteSelectionJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteSelectionRepositoryAdapter implements VoteSelectionRepositoryPort {

  private final VoteSelectionJpaRepository voteSelectionJpaRepository;

  @Transactional
  @Override
  public VoteSelection save(VoteSelection voteSelection) {
    return voteSelectionJpaRepository.save(VoteSelectionEntity.from(voteSelection)).toDomain();
  }

  @Override
  public boolean existsByVoteOptionIdsAndUserId(List<Long> voteOptionIds, Long userId) {
    return voteSelectionJpaRepository.existsByVoteOptionIdInAndUserId(voteOptionIds, userId);
  }

  @Override
  public int countDistinctUsersByVoteOptionIds(List<Long> voteOptionIds) {
    return voteSelectionJpaRepository.countDistinctUsersByVoteOptionIds(voteOptionIds);
  }

  @Override
  public Set<Long> findSelectedOptionIdsByVoteOptionIdsAndUserId(List<Long> voteOptionIds, Long userId) {
    return Set.copyOf(voteSelectionJpaRepository.findSelectedOptionIdsByVoteOptionIdsAndUserId(voteOptionIds, userId));
  }

  @Transactional
  @Override
  public void deleteAllByVoteOptionIds(List<Long> voteOptionIds) {
    voteSelectionJpaRepository.deleteAllByVoteOptionIds(voteOptionIds);
  }
}
