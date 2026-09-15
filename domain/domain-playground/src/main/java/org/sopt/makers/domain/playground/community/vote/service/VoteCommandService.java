package org.sopt.makers.domain.playground.community.vote.service;

import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.ALREADY_VOTED;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.INVALID_VOTE_OPTION_CONTENT;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.INVALID_VOTE_OPTION_COUNT;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.INVALID_VOTE_SELECTION_COUNT;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.INVALID_VOTE_SELECTION_OPTION;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.NOT_FOUND_VOTE;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.NOT_FOUND_WRITER;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.SOPTICLE_VOTE_NOT_ALLOWED;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.CommunityCategoryGroup;
import org.sopt.makers.domain.playground.community.exception.CommunityException;
import org.sopt.makers.domain.playground.community.member.service.CommunityMemberAssembler;
import org.sopt.makers.domain.playground.community.vote.Vote;
import org.sopt.makers.domain.playground.community.vote.VoteOption;
import org.sopt.makers.domain.playground.community.vote.VoteResult;
import org.sopt.makers.domain.playground.community.vote.VoteSelection;
import org.sopt.makers.domain.playground.community.vote.port.VoteOptionRepositoryPort;
import org.sopt.makers.domain.playground.community.vote.port.VoteRepositoryPort;
import org.sopt.makers.domain.playground.community.vote.port.VoteSelectionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 게시글 투표 생성/선택, 게시글 삭제에 연계된 투표 삭제 유스케이스. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteCommandService {

  private static final int MIN_OPTION_COUNT = 2;
  private static final int MAX_OPTION_COUNT = 5;
  private static final int MAX_OPTION_CONTENT_LENGTH = 40;

  private final VoteRepositoryPort voteRepositoryPort;
  private final VoteOptionRepositoryPort voteOptionRepositoryPort;
  private final VoteSelectionRepositoryPort voteSelectionRepositoryPort;
  private final VoteQueryService voteQueryService;
  private final CommunityMemberAssembler communityMemberAssembler;

  public record CreateVoteCommand(boolean isMultiple, List<String> voteOptions) {}

  @Transactional
  public void createVote(
      Long postId, CommunityCategoryGroup categoryGroup, CreateVoteCommand command) {
    if (command == null) {
      return;
    }

    validateVotePolicy(categoryGroup, command);
    voteRepositoryPort.save(Vote.create(postId, command.isMultiple(), command.voteOptions()));
  }

  @Transactional
  public VoteResult selectVote(Long userId, Long postId, List<Long> selectedOptionIds) {
    validateVoterExists(userId);

    Vote vote =
        voteRepositoryPort
            .findByPostId(postId)
            .orElseThrow(() -> new CommunityException(NOT_FOUND_VOTE));

    if (voteSelectionRepositoryPort.existsByVoteOptionIdsAndUserId(selectedOptionIds, userId)) {
      throw new CommunityException(ALREADY_VOTED);
    }

    List<VoteOption> selectedOptions = voteOptionRepositoryPort.findAllByIds(selectedOptionIds);
    validateVoteSelectionPolicy(vote, selectedOptionIds, selectedOptions);

    for (VoteOption option : selectedOptions) {
      voteSelectionRepositoryPort.save(VoteSelection.create(userId, option.id()));
      voteOptionRepositoryPort.increaseVoteCount(option.id());
    }

    return voteQueryService
        .getVoteByPostId(postId, userId)
        .orElseThrow(() -> new CommunityException(NOT_FOUND_VOTE));
  }

  /** 게시글 삭제 시 호출되는 투표 연계 삭제 처리. */
  @Transactional
  public void deleteVoteByPostId(Long postId) {
    voteRepositoryPort
        .findByPostId(postId)
        .ifPresent(
            vote -> {
              List<Long> optionIds = vote.options().stream().map(VoteOption::id).toList();
              voteSelectionRepositoryPort.deleteAllByVoteOptionIds(optionIds);
              voteOptionRepositoryPort.deleteAllByVoteId(vote.id());
              voteRepositoryPort.deleteByPostId(postId);
            });
  }

  private void validateVoterExists(Long userId) {
    if (communityMemberAssembler.getMemberSummary(userId) == null) {
      throw new CommunityException(NOT_FOUND_WRITER);
    }
  }

  private void validateVotePolicy(CommunityCategoryGroup categoryGroup, CreateVoteCommand command) {
    if (categoryGroup == CommunityCategoryGroup.SOPTICLE) {
      throw new CommunityException(SOPTICLE_VOTE_NOT_ALLOWED);
    }

    List<String> options = command.voteOptions();
    if (options == null || options.size() < MIN_OPTION_COUNT || options.size() > MAX_OPTION_COUNT) {
      throw new CommunityException(INVALID_VOTE_OPTION_COUNT);
    }

    for (String option : options) {
      if (option == null
          || option.trim().isEmpty()
          || option.length() > MAX_OPTION_CONTENT_LENGTH) {
        throw new CommunityException(INVALID_VOTE_OPTION_CONTENT);
      }
    }
  }

  private void validateVoteSelectionPolicy(
      Vote vote, List<Long> selectedOptionIds, List<VoteOption> selectedOptions) {
    if (!vote.isMultipleOptions() && selectedOptionIds.size() > 1) {
      throw new CommunityException(INVALID_VOTE_SELECTION_COUNT);
    }

    if (selectedOptions.size() != selectedOptionIds.size()) {
      throw new CommunityException(INVALID_VOTE_SELECTION_OPTION);
    }
  }
}
