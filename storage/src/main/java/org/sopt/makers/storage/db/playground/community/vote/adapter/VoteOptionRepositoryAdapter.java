package org.sopt.makers.storage.db.playground.community.vote.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.vote.VoteOption;
import org.sopt.makers.domain.playground.community.vote.port.VoteOptionRepositoryPort;
import org.sopt.makers.storage.db.playground.community.vote.entity.VoteOptionEntity;
import org.sopt.makers.storage.db.playground.community.vote.repository.VoteOptionJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteOptionRepositoryAdapter implements VoteOptionRepositoryPort {

  private final VoteOptionJpaRepository voteOptionJpaRepository;

  @Override
  public List<VoteOption> findAllByIds(List<Long> optionIds) {
    return voteOptionJpaRepository.findAllById(optionIds).stream()
        .map(VoteOptionEntity::toDomain)
        .toList();
  }

  @Transactional
  @Override
  public void increaseVoteCount(Long optionId) {
    voteOptionJpaRepository.increaseVoteCountDirect(optionId);
  }

  @Transactional
  @Override
  public void deleteAllByVoteId(Long voteId) {
    voteOptionJpaRepository.deleteAllByVoteId(voteId);
  }
}
