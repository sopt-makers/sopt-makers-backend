package org.sopt.makers.api.controller.app.soptletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopic;

public record TopicResponse(
    @Schema(description = "주제 아이디", example = "1") Long topicId,
    @Schema(description = "주제 제목", example = "이번 기수에서 가장 기억에 남는 순간") String title,
    @Schema(description = "상시 노출되는 기본 주제인지 여부", example = "false") boolean isDefault,
    @Schema(description = "주제가 만들어진 일시", example = "2026-09-01T00:00:00") LocalDateTime createdAt) {

  public static TopicResponse of(SoptLetterTopic topic) {
    return new TopicResponse(topic.id(), topic.title(), topic.isDefault(), topic.createdAt());
  }
}
