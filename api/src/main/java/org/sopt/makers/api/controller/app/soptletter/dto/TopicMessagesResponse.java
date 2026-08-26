package org.sopt.makers.api.controller.app.soptletter.dto;

import java.util.List;
import org.sopt.makers.domain.app.soptletter.SoptLetterPage;

public record TopicMessagesResponse(
    Long topicId,
    String title,
    long totalCount,
    Long nextCursor,
    boolean hasNext,
    Boolean hasNormalTopic,
    List<TopicMessageResponse> messages) {

  public static TopicMessagesResponse of(SoptLetterPage page) {
    return new TopicMessagesResponse(
        page.topic().id(),
        page.topic().title(),
        page.totalCount(),
        page.nextCursor(),
        page.hasNext(),
        page.hasNormalTopic(),
        page.messages().stream().map(TopicMessageResponse::of).toList());
  }
}
