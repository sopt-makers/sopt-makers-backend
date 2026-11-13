package org.sopt.makers.domain.crew.dining.result;

public record CreateDiningResult(Long meetingId) {
  public static CreateDiningResult from(Long meetingId) {
    return new CreateDiningResult(meetingId);
  }
}
