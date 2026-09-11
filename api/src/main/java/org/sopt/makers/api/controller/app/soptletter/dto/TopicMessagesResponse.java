package org.sopt.makers.api.controller.app.soptletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.makers.domain.app.soptletter.SoptLetterPage;

public record TopicMessagesResponse(
    @Schema(description = "주제 아이디", example = "1") Long topicId,
    @Schema(description = "주제 제목", example = "이번 기수에서 가장 기억에 남는 순간") String title,
    @Schema(description = "주제에 달린 전체 메시지 수", example = "120") long totalCount,
    @Schema(
            description = "다음 페이지 요청에 넣을 cursor. 목록이 비면 null이고, 마지막 페이지에도 값이 담길 수 있어 hasNext로 판단한다",
            example = "100")
        Long nextCursor,
    @Schema(description = "다음 페이지가 있는지 여부", example = "true") boolean hasNext,
    @Schema(description = "기본 주제 조회일 때만 채워진다. 개별 주제가 하나라도 있는지 여부", example = "true")
        Boolean hasNormalTopic,
    @Schema(description = "메시지 목록") List<TopicMessageResponse> messages) {

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
