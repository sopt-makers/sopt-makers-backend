package org.sopt.makers.api.controller.playground.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.makers.domain.playground.member.ask.AskPage;

public record QuestionsResponse(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "질문 목록") List<QuestionResponse> questions,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "현재 페이지 번호 (0부터 시작)") Integer currentPage,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "페이지 크기") Integer pageSize,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "전체 질문 개수") Long totalElements,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "전체 페이지 수") Integer totalPages,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "다음 페이지 존재 여부") Boolean hasNext,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "이전 페이지 존재 여부") Boolean hasPrevious) {

  public static QuestionsResponse from(AskPage page) {
    List<QuestionResponse> questions = page.asks().stream().map(QuestionResponse::from).toList();
    return new QuestionsResponse(
        questions,
        page.currentPage(),
        page.pageSize(),
        page.totalElements(),
        page.totalPages(),
        page.hasNext(),
        page.hasPrevious());
  }
}
