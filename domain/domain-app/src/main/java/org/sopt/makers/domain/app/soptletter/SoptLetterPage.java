package org.sopt.makers.domain.app.soptletter;

import java.util.List;

public record SoptLetterPage(
    SoptLetterTopic topic,
    long totalCount,
    Long nextCursor,
    boolean hasNext,
    Boolean hasNormalTopic,
    List<SoptLetterView> messages) {

  public SoptLetterPage {
    messages = messages == null ? List.of() : List.copyOf(messages);
  }
}
