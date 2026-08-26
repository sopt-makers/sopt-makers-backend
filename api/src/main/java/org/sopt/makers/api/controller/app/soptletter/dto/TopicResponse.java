package org.sopt.makers.api.controller.app.soptletter.dto;

import java.time.LocalDateTime;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopic;

public record TopicResponse(
    Long topicId, String title, boolean isDefault, LocalDateTime createdAt) {

  public static TopicResponse of(SoptLetterTopic topic) {
    return new TopicResponse(topic.id(), topic.title(), topic.isDefault(), topic.createdAt());
  }
}
