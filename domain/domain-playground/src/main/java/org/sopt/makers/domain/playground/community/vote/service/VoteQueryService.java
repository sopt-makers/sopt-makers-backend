package org.sopt.makers.domain.playground.community.vote.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.vote.Vote;
import org.sopt.makers.domain.playground.community.vote.VoteOption;
import org.sopt.makers.domain.playground.community.vote.VoteOptionResult;
import org.sopt.makers.domain.playground.community.vote.VoteResult;
import org.sopt.makers.domain.playground.community.vote.port.VoteRepositoryPort;
import org.sopt.makers.domain.playground.community.vote.port.VoteSelectionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 게시글 투표 결과 조회 유스케이스. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteQueryService {

  private final VoteRepositoryPort voteRepositoryPort;
  private final VoteSelectionRepositoryPort voteSelectionRepositoryPort;

  public Optional<VoteResult> getVoteByPostId(Long postId, Long viewerId) {
    return voteRepositoryPort.findByPostId(postId).map(vote -> toVoteResult(vote, viewerId));
  }

  public Map<Long, Integer> getTotalVoteCountMapByPostIds(List<Long> postIds) {
    if (postIds == null || postIds.isEmpty()) {
      return Map.of();
    }

    return voteRepositoryPort.findAllByPostIds(postIds).stream()
        .collect(Collectors.toMap(Vote::postId, vote -> vote.options().stream().mapToInt(VoteOption::voteCount).sum()));
  }

  /** postIds에 걸린 투표를 배치 조회해 postId -> VoteResult 맵으로 반환한다(N+1 없이 조회). */
  public Map<Long, VoteResult> getVoteResultsByPostIds(List<Long> postIds, Long viewerId) {
    if (postIds == null || postIds.isEmpty()) {
      return Map.of();
    }

    List<Vote> votes = voteRepositoryPort.findAllByPostIds(postIds);
    if (votes.isEmpty()) {
      return Map.of();
    }

    List<Long> voteIds = votes.stream().map(Vote::id).toList();
    List<Long> allOptionIds =
        votes.stream().flatMap(vote -> vote.options().stream()).map(VoteOption::id).toList();

    Set<Long> selectedOptionIds =
        viewerId == null
            ? Set.of()
            : voteSelectionRepositoryPort.findSelectedOptionIdsByVoteOptionIdsAndUserId(allOptionIds, viewerId);
    Map<Long, Integer> participantCountMap = voteSelectionRepositoryPort.countDistinctUsersGroupedByVoteIds(voteIds);

    return votes.stream()
        .collect(
            Collectors.toMap(
                Vote::postId,
                vote -> toVoteResult(vote, selectedOptionIds, participantCountMap.getOrDefault(vote.id(), 0))));
  }

  private VoteResult toVoteResult(Vote vote, Long viewerId) {
    List<VoteOption> sortedOptions = vote.options().stream().sorted(Comparator.comparing(VoteOption::id)).toList();
    List<Long> optionIds = sortedOptions.stream().map(VoteOption::id).toList();

    Set<Long> selectedOptionIds =
        viewerId == null
            ? Set.of()
            : voteSelectionRepositoryPort.findSelectedOptionIdsByVoteOptionIdsAndUserId(optionIds, viewerId);
    int totalParticipants = voteSelectionRepositoryPort.countDistinctUsersByVoteOptionIds(optionIds);

    return toVoteResult(vote, selectedOptionIds, totalParticipants);
  }

  private VoteResult toVoteResult(Vote vote, Set<Long> selectedOptionIds, int totalParticipants) {
    List<VoteOption> sortedOptions = vote.options().stream().sorted(Comparator.comparing(VoteOption::id)).toList();
    boolean hasVoted = sortedOptions.stream().anyMatch(option -> selectedOptionIds.contains(option.id()));
    int totalVoteCount = sortedOptions.stream().mapToInt(VoteOption::voteCount).sum();

    List<VoteOptionResult> optionResults =
        sortedOptions.stream()
            .map(
                option ->
                    new VoteOptionResult(
                        option.id(),
                        option.content(),
                        option.voteCount(),
                        calculateVotePercent(option.voteCount(), totalVoteCount),
                        selectedOptionIds.contains(option.id())))
            .toList();

    return new VoteResult(vote.id(), vote.isMultipleOptions(), hasVoted, totalParticipants, optionResults);
  }

  private int calculateVotePercent(int voteCount, int totalCount) {
    if (totalCount == 0) {
      return 0;
    }
    return (int) Math.round((double) voteCount * 100 / totalCount);
  }
}
