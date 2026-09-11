package org.sopt.makers.api.controller.app.soptletter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.sopt.makers.api.controller.app.soptletter.dto.CtaResponse;
import org.sopt.makers.api.controller.app.soptletter.dto.MessageResponse;
import org.sopt.makers.api.controller.app.soptletter.dto.OnboardingProfileResponse;
import org.sopt.makers.api.controller.app.soptletter.dto.ReportFormResponse;
import org.sopt.makers.api.controller.app.soptletter.dto.TopicDetailResponse;
import org.sopt.makers.api.controller.app.soptletter.dto.TopicMessagesResponse;
import org.sopt.makers.api.controller.app.soptletter.dto.TopicsResponse;
import org.sopt.makers.api.controller.app.soptletter.dto.WriteMessageRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "솝레터", description = "앱 솝레터 API")
public interface SoptLetterApi {

  @Operation(summary = "온보딩 프로필 조회", description = "프로필이 없으면 익명 닉네임을 뽑아 새로 만든다.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "솝레터 온보딩 프로필 조회에 성공했습니다."),
    @ApiResponse(
        responseCode = "409",
        description = "사용할 수 있는 익명 닉네임을 찾지 못했거나, 이미 생성된 프로필입니다.",
        content = @Content)
  })
  ResponseEntity<BaseResponse<OnboardingProfileResponse>> getOnboardingProfile(
      @Parameter(hidden = true) Long userId);

  @Operation(summary = "온보딩 완료 처리")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "솝레터 온보딩 완료 처리에 성공했습니다."),
    @ApiResponse(responseCode = "404", description = "솝레터 프로필이 존재하지 않습니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<OnboardingProfileResponse>> completeOnboarding(
      @Parameter(hidden = true) Long userId);

  @Operation(summary = "익명 신고 폼 조회")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "솝레터 신고 폼 조회에 성공했습니다."),
    @ApiResponse(responseCode = "404", description = "운영 설정 값이 존재하지 않습니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<ReportFormResponse>> getReportForm();

  @Operation(summary = "CTA 조회", description = "노출 기간에 들어온 주제가 없으면 showCta 가 false 다.")
  @ApiResponse(responseCode = "200", description = "솝레터 CTA 조회에 성공했습니다.")
  ResponseEntity<BaseResponse<CtaResponse>> getCta();

  @Operation(summary = "주제 목록 조회", description = "type 이 없으면 전체, default/normal 이면 해당 종류만 조회한다.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "솝레터 주제 목록 조회에 성공했습니다."),
    @ApiResponse(responseCode = "400", description = "지원하지 않는 주제 유형입니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<TopicsResponse>> getTopics(
      @Parameter(description = "주제 종류. default 또는 normal. 생략하면 전체", example = "normal")
          String type);

  @Operation(summary = "주제 조회")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "솝레터 주제 조회에 성공했습니다."),
    @ApiResponse(responseCode = "404", description = "존재하지 않는 주제입니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<TopicDetailResponse>> getTopic(
      @Parameter(description = "주제 아이디", example = "1") Long topicId);

  @Operation(summary = "기본 주제 메시지 목록 조회", description = "개별 주제 존재 여부를 함께 준다.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "솝레터 메시지 목록 조회에 성공했습니다."),
    @ApiResponse(
        responseCode = "400",
        description = "size가 1에서 100 사이를 벗어났습니다.",
        content = @Content),
    @ApiResponse(
        responseCode = "404",
        description = "솝레터 프로필이 없거나, 기본 주제가 존재하지 않습니다.",
        content = @Content)
  })
  ResponseEntity<BaseResponse<TopicMessagesResponse>> getDefaultTopicMessages(
      @Parameter(hidden = true) Long userId,
      @Parameter(description = "이전 응답의 nextCursor. 첫 페이지는 생략한다", example = "120") Long cursor,
      @Parameter(description = "한 번에 가져올 개수. 1에서 100 사이", example = "20") @Min(1) @Max(100)
          int size);

  @Operation(summary = "주제별 메시지 목록 조회")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "솝레터 메시지 목록 조회에 성공했습니다."),
    @ApiResponse(
        responseCode = "400",
        description = "size가 1에서 100 사이를 벗어났습니다.",
        content = @Content),
    @ApiResponse(
        responseCode = "404",
        description = "솝레터 프로필이 없거나, 존재하지 않는 주제입니다.",
        content = @Content)
  })
  ResponseEntity<BaseResponse<TopicMessagesResponse>> getTopicMessages(
      @Parameter(hidden = true) Long userId,
      @Parameter(description = "주제 아이디", example = "1") Long topicId,
      @Parameter(description = "이전 응답의 nextCursor. 첫 페이지는 생략한다", example = "120") Long cursor,
      @Parameter(description = "한 번에 가져올 개수. 1에서 100 사이", example = "20") @Min(1) @Max(100)
          int size);

  @Operation(summary = "메시지 상세 조회")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "솝레터 메시지 조회에 성공했습니다."),
    @ApiResponse(
        responseCode = "404",
        description = "솝레터 프로필이 없거나, 존재하지 않는 메시지입니다.",
        content = @Content)
  })
  ResponseEntity<BaseResponse<MessageResponse>> getMessage(
      @Parameter(hidden = true) Long userId,
      @Parameter(description = "주제 아이디", example = "1") Long topicId,
      @Parameter(description = "메시지 아이디", example = "120") Long messageId);

  @Operation(summary = "메시지 작성", description = "하루에 작성할 수 있는 개수가 제한된다.")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "솝레터 메시지 작성에 성공했습니다."),
    @ApiResponse(
        responseCode = "400",
        description = "하루에 작성할 수 있는 메시지 수를 초과했거나, 내용이 1자 미만 또는 350자를 넘었습니다.",
        content = @Content),
    @ApiResponse(
        responseCode = "404",
        description = "솝레터 프로필이 없거나, 존재하지 않는 주제입니다.",
        content = @Content)
  })
  ResponseEntity<BaseResponse<MessageResponse>> writeMessage(
      @Parameter(hidden = true) Long userId,
      @Parameter(description = "주제 아이디", example = "1") Long topicId,
      @Valid WriteMessageRequest request);

  @Operation(summary = "메시지 수정")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "솝레터 메시지 수정에 성공했습니다."),
    @ApiResponse(
        responseCode = "400",
        description = "내용이 1자 미만이거나 350자를 넘었습니다.",
        content = @Content),
    @ApiResponse(responseCode = "403", description = "본인이 작성한 메시지가 아닙니다.", content = @Content),
    @ApiResponse(
        responseCode = "404",
        description = "솝레터 프로필이 없거나, 존재하지 않는 메시지입니다.",
        content = @Content)
  })
  ResponseEntity<BaseResponse<MessageResponse>> updateMessage(
      @Parameter(hidden = true) Long userId,
      @Parameter(description = "주제 아이디", example = "1") Long topicId,
      @Parameter(description = "메시지 아이디", example = "120") Long messageId,
      @Valid WriteMessageRequest request);

  @Operation(summary = "메시지 삭제")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "솝레터 메시지 삭제에 성공했습니다."),
    @ApiResponse(responseCode = "403", description = "본인이 작성한 메시지가 아닙니다.", content = @Content),
    @ApiResponse(
        responseCode = "404",
        description = "솝레터 프로필이 없거나, 존재하지 않는 메시지입니다.",
        content = @Content)
  })
  ResponseEntity<BaseResponse<Void>> deleteMessage(
      @Parameter(hidden = true) Long userId,
      @Parameter(description = "주제 아이디", example = "1") Long topicId,
      @Parameter(description = "메시지 아이디", example = "120") Long messageId);

  @Operation(summary = "메시지 좋아요")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "솝레터 메시지 좋아요에 성공했습니다."),
    @ApiResponse(responseCode = "404", description = "존재하지 않는 메시지입니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<Void>> addLike(
      @Parameter(hidden = true) Long userId,
      @Parameter(description = "주제 아이디", example = "1") Long topicId,
      @Parameter(description = "메시지 아이디", example = "120") Long messageId);

  @Operation(summary = "메시지 좋아요 취소")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "솝레터 메시지 좋아요 취소에 성공했습니다."),
    @ApiResponse(responseCode = "404", description = "존재하지 않는 메시지입니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<Void>> removeLike(
      @Parameter(hidden = true) Long userId,
      @Parameter(description = "주제 아이디", example = "1") Long topicId,
      @Parameter(description = "메시지 아이디", example = "120") Long messageId);
}
