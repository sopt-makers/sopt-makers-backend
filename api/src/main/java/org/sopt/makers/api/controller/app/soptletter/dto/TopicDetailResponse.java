package org.sopt.makers.api.controller.app.soptletter.dto;

import java.time.LocalDateTime;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopic;

public record TopicDetailResponse(
    Long topicId,
    String title,
    boolean active,
    LocalDateTime startedAt,
    LocalDateTime endedAt,
    LocalDateTime createdAt) {

  public static TopicDetailResponse of(SoptLetterTopic topic, LocalDateTime now) {
    return new TopicDetailResponse(
        topic.id(),
        topic.title(),
        topic.isActiveAt(now),
        topic.startedAt(),
        topic.endedAt(),
        topic.createdAt());
  }
}
