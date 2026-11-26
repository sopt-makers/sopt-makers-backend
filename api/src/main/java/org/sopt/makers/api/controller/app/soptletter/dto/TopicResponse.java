package org.sopt.makers.api.controller.app.soptletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopic;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopicView;

public record TopicResponse(
    @Schema(description = "주제 아이디", example = "1") Long topicId,
    @Schema(description = "주제 제목", example = "이번 기수에서 가장 기억에 남는 순간") String title,
    @Schema(description = "주제 서브타이틀", example = "앱잼 끝나고 하고 싶은 말", nullable = true) String subtitle,
    @Schema(description = "상시 노출되는 기본 주제인지 여부", example = "false") boolean isDefault,
    @Schema(description = "만든 지 7일이 안 된 주제인지 여부", example = "true") boolean isNew,
    @Schema(description = "최근 7일 솝레터 수 상위 2개 주제인지 여부", example = "false") boolean isHot,
    @Schema(description = "안 읽은 솝레터 수. 내가 쓴 솝레터는 세지 않는다", example = "3") int unreadCount,
    @Schema(description = "주제가 만들어진 일시", example = "2026-09-01T00:00:00") LocalDateTime createdAt) {

  public static TopicResponse of(SoptLetterTopicView view) {
    SoptLetterTopic topic = view.topic();
    return new TopicResponse(
        topic.id(),
        topic.title(),
        topic.subtitle(),
        topic.isDefault(),
        view.isNew(),
        view.isHot(),
        Math.toIntExact(view.unreadCount()),
        topic.createdAt());
  }
}
