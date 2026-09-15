package org.sopt.makers.api.controller.app.soptletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopic;

public record TopicDetailResponse(
    @Schema(description = "주제 아이디", example = "1") Long topicId,
    @Schema(description = "주제 제목", example = "이번 기수에서 가장 기억에 남는 순간") String title,
    @Schema(description = "지금이 노출 기간 안인지 여부", example = "true") boolean active,
    @Schema(description = "노출 시작 일시", example = "2026-09-01T00:00:00") LocalDateTime startedAt,
    @Schema(description = "노출 종료 일시", example = "2026-09-30T23:59:59") LocalDateTime endedAt,
    @Schema(description = "주제가 만들어진 일시", example = "2026-09-01T00:00:00") LocalDateTime createdAt) {

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
