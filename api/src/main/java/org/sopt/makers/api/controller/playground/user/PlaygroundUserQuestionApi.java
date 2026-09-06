package org.sopt.makers.api.controller.playground.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sopt.makers.api.controller.playground.user.dto.AnswerSaveRequest;
import org.sopt.makers.api.controller.playground.user.dto.AnswerUpdateRequest;
import org.sopt.makers.api.controller.playground.user.dto.QuestionReportRequest;
import org.sopt.makers.api.controller.playground.user.dto.QuestionSaveRequest;
import org.sopt.makers.api.controller.playground.user.dto.QuestionUpdateRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.playground.member.ask.QuestionTab;
import org.springframework.http.ResponseEntity;

@Tag(name = "Member Question 관련 API", description = "회원 질문/답변 관련 API List")
@SecurityRequirement(name = "Authorization")
public interface PlaygroundUserQuestionApi {

  @Operation(
      summary = "질문 작성 API",
      description = "다른 사용자에게 질문을 작성합니다. 익명으로 작성할 경우 asker 정보가 숨겨집니다.")
  ResponseEntity<BaseResponse<?>> createQuestion(
      @Parameter(hidden = true) Long userId, Long receiverId, @Valid QuestionSaveRequest request);

  @Operation(summary = "질문 수정 API", description = "답변이 달리기 전에만 수정 가능합니다.")
  ResponseEntity<BaseResponse<?>> updateQuestion(
      @Parameter(hidden = true) Long userId, Long questionId, @Valid QuestionUpdateRequest request);

  @Operation(
      summary = "질문 삭제 API",
      description = "질문 삭제 규칙:\n- 답변 전: 질문 작성자만 삭제 가능\n- 항상: 질문 받은 사람은 삭제 가능")
  ResponseEntity<BaseResponse<?>> deleteQuestion(@Parameter(hidden = true) Long userId, Long questionId);

  @Operation(summary = "답변 작성 API", description = "질문을 받은 사람만 답변을 작성할 수 있습니다.")
  ResponseEntity<BaseResponse<?>> createAnswer(
      @Parameter(hidden = true) Long userId, Long questionId, @Valid AnswerSaveRequest request);

  @Operation(summary = "답변 수정 API", description = "답변 작성자만 수정 가능합니다.")
  ResponseEntity<BaseResponse<?>> updateAnswer(
      @Parameter(hidden = true) Long userId, Long answerId, @Valid AnswerUpdateRequest request);

  @Operation(summary = "답변 삭제 API", description = "답변 작성자만 삭제 가능합니다.")
  ResponseEntity<BaseResponse<?>> deleteAnswer(@Parameter(hidden = true) Long userId, Long answerId);

  @Operation(
      summary = "나도 궁금해요 토글 API",
      description = "답변이 달리기 전 질문에 '나도 궁금해요' 반응을 토글합니다. 이미 반응을 누른 경우 취소되고, 누르지 않은 경우 반응이 추가됩니다.")
  ResponseEntity<BaseResponse<?>> toggleQuestionReaction(
      @Parameter(hidden = true) Long userId, Long questionId);

  @Operation(
      summary = "도움돼요 토글 API",
      description = "답변에 '도움돼요' 반응을 토글합니다. 이미 반응을 누른 경우 취소되고, 누르지 않은 경우 반응이 추가됩니다.")
  ResponseEntity<BaseResponse<?>> toggleAnswerReaction(
      @Parameter(hidden = true) Long userId, Long answerId);

  @Operation(summary = "질문 신고 API")
  ResponseEntity<BaseResponse<?>> reportQuestion(
      @Parameter(hidden = true) Long userId, Long questionId, QuestionReportRequest request);

  @Operation(
      summary = "질문 목록 조회 API",
      description =
          "특정 사용자의 질문 목록을 조회합니다.\n"
              + "tab: answered (답변 완료), unanswered (새질문), 미입력 시 전체 조회\n"
              + "page: 페이지 번호 (0부터 시작, 기본값 0)\n"
              + "size: 페이지 크기 (기본 10, 최대 100)")
  ResponseEntity<BaseResponse<?>> getQuestions(
      @Parameter(hidden = true) Long userId,
      Long memberId,
      @Parameter(description = "answered/unanswered, 대소문자 무관") QuestionTab tab,
      @Parameter(description = "페이지 번호") Integer page,
      @Parameter(description = "페이지 크기") Integer size);

  @Operation(summary = "답변 대기 중인 질문 개수 조회 API", description = "현재 로그인한 사용자에게 달린 답변 대기 중인 질문의 개수를 조회합니다.")
  ResponseEntity<BaseResponse<?>> getUnansweredCount(@Parameter(hidden = true) Long userId);

  @Operation(
      summary = "내 질문의 답변 위치 조회 API",
      description = "특정 사용자의 답변 완료 탭에서 내가 남긴 가장 최신 질문이 몇 페이지 몇 번째에 있는지 조회합니다.")
  ResponseEntity<BaseResponse<?>> getMyLatestAnsweredQuestionLocation(
      @Parameter(hidden = true) Long userId, Long memberId);

  @Operation(
      summary = "특정 사용자의 question 탭에서 특정 질문 위치 조회 API",
      description = "특정 사용자의 question 탭에서 questionId에 해당하는 질문이 어느 탭(answered/unanswered)의 몇 페이지 몇 번째에 있는지 조회합니다.")
  ResponseEntity<BaseResponse<?>> getQuestionLocation(Long memberId, Long questionId);

  @Operation(
      summary = "최신 질문 5개 조회 API",
      description =
          "삭제/신고 질문 제외, 최신순으로 최대 5개의 답변 완료된 질문을 조회합니다. "
              + "가능한 경우 서로 다른 멤버 질문을 우선 노출하고, 부족하면 동일 멤버의 다른 질문으로 채웁니다. "
              + "응답에는 질문별 탭(answered) 및 위치 정보도 함께 포함됩니다.")
  ResponseEntity<BaseResponse<?>> getLatestAnsweredQuestions();
}
