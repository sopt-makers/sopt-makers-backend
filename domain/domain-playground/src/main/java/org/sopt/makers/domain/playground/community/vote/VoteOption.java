package org.sopt.makers.domain.playground.community.vote;

public record VoteOption(Long id, String content, int voteCount) {

  public static VoteOption create(String content) {
    return new VoteOption(null, content, 0);
  }
}
