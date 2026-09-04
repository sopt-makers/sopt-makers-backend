package org.sopt.makers.storage.db.playground.community.vote.adapter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.vote.Vote;
import org.sopt.makers.domain.playground.community.vote.port.VoteRepositoryPort;
import org.sopt.makers.storage.db.playground.community.vote.entity.VoteEntity;
import org.sopt.makers.storage.db.playground.community.vote.entity.VoteOptionEntity;
import org.sopt.makers.storage.db.playground.community.vote.repository.VoteJpaRepository;
import org.sopt.makers.storage.db.playground.community.vote.repository.VoteOptionJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteRepositoryAdapter implements VoteRepositoryPort {

  private final VoteJpaRepository voteJpaRepository;
  private final VoteOptionJpaRepository voteOptionJpaRepository;

  @Transactional
  @Override
  public Vote save(Vote vote) {
    VoteEntity savedVote = voteJpaRepository.save(VoteEntity.from(vote));

    List<VoteOptionEntity> savedOptions =
        voteOptionJpaRepository.saveAll(
            vote.options().stream().map(option -> VoteOptionEntity.of(savedVote.getId(), option)).toList());

    return savedVote.toDomain(savedOptions.stream().map(VoteOptionEntity::toDomain).toList());
  }

  @Override
  public Optional<Vote> findByPostId(Long postId) {
    return voteJpaRepository
        .findByPostId(postId)
        .map(
            voteEntity ->
                voteEntity.toDomain(
                    voteOptionJpaRepository.findAllByVoteId(voteEntity.getId()).stream()
                        .map(VoteOptionEntity::toDomain)
                        .toList()));
  }

  @Override
  public List<Vote> findAllByPostIds(List<Long> postIds) {
    List<VoteEntity> voteEntities = voteJpaRepository.findAllByPostIdIn(postIds);
    if (voteEntities.isEmpty()) {
      return List.of();
    }

    List<Long> voteIds = voteEntities.stream().map(VoteEntity::getId).toList();
    Map<Long, List<VoteOptionEntity>> optionsByVoteId =
        voteOptionJpaRepository.findAllByVoteIdIn(voteIds).stream()
            .collect(Collectors.groupingBy(VoteOptionEntity::getVoteId));

    return voteEntities.stream()
        .map(
            voteEntity ->
                voteEntity.toDomain(
                    optionsByVoteId.getOrDefault(voteEntity.getId(), List.of()).stream()
                        .map(VoteOptionEntity::toDomain)
                        .toList()))
        .toList();
  }

  @Transactional
  @Override
  public void deleteByPostId(Long postId) {
    voteJpaRepository.deleteByPostId(postId);
  }
}
