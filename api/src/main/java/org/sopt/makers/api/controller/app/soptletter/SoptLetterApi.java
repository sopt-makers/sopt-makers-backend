package org.sopt.makers.api.controller.app.soptletter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.sopt.makers.api.controller.app.soptletter.dto.WriteMessageRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "솝레터", description = "앱 솝레터 API")
public interface SoptLetterApi {

  @Operation(summary = "온보딩 프로필 조회", description = "프로필이 없으면 익명 닉네임을 뽑아 새로 만든다.")
  ResponseEntity<BaseResponse<?>> getOnboardingProfile(@Parameter(hidden = true) Long userId);

  @Operation(summary = "온보딩 완료 처리")
  ResponseEntity<BaseResponse<?>> completeOnboarding(@Parameter(hidden = true) Long userId);

  @Operation(summary = "익명 신고 폼 조회")
  ResponseEntity<BaseResponse<?>> getReportForm();

  @Operation(summary = "CTA 조회", description = "노출 기간에 들어온 주제가 없으면 showCta 가 false 다.")
  ResponseEntity<BaseResponse<?>> getCta();

  @Operation(summary = "주제 목록 조회", description = "type 이 없으면 전체, default/normal 이면 해당 종류만 조회한다.")
  ResponseEntity<BaseResponse<?>> getTopics(String type);

  @Operation(summary = "주제 조회")
  ResponseEntity<BaseResponse<?>> getTopic(Long topicId);

  @Operation(summary = "기본 주제 메시지 목록 조회", description = "개별 주제 존재 여부를 함께 준다.")
  ResponseEntity<BaseResponse<?>> getDefaultTopicMessages(
      @Parameter(hidden = true) Long userId, Long cursor, @Min(1) @Max(100) int size);

  @Operation(summary = "주제별 메시지 목록 조회")
  ResponseEntity<BaseResponse<?>> getTopicMessages(
      @Parameter(hidden = true) Long userId, Long topicId, Long cursor, @Min(1) @Max(100) int size);

  @Operation(summary = "메시지 상세 조회")
  ResponseEntity<BaseResponse<?>> getMessage(
      @Parameter(hidden = true) Long userId, Long topicId, Long messageId);

  @Operation(summary = "메시지 작성", description = "하루에 작성할 수 있는 개수가 제한된다.")
  ResponseEntity<BaseResponse<?>> writeMessage(
      @Parameter(hidden = true) Long userId, Long topicId, @Valid WriteMessageRequest request);

  @Operation(summary = "메시지 수정")
  ResponseEntity<BaseResponse<?>> updateMessage(
      @Parameter(hidden = true) Long userId,
      Long topicId,
      Long messageId,
      @Valid WriteMessageRequest request);

  @Operation(summary = "메시지 삭제")
  ResponseEntity<BaseResponse<?>> deleteMessage(
      @Parameter(hidden = true) Long userId, Long topicId, Long messageId);

  @Operation(summary = "메시지 좋아요")
  ResponseEntity<BaseResponse<?>> addLike(
      @Parameter(hidden = true) Long userId, Long topicId, Long messageId);

  @Operation(summary = "메시지 좋아요 취소")
  ResponseEntity<BaseResponse<?>> removeLike(
      @Parameter(hidden = true) Long userId, Long topicId, Long messageId);
}
