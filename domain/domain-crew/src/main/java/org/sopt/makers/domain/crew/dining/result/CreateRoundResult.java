package org.sopt.makers.domain.crew.dining.result;

public record CreateRoundResult(Long roundId) {
  public static CreateRoundResult from(Long roundId) {
    return new CreateRoundResult(roundId);
  }
}
