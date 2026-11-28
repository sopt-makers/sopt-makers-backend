package org.sopt.makers.api.controller.admin.soptletter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.admin.soptletter.dto.AdminSoptLetterNotificationRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "어드민 솝레터 알림", description = "어드민 솝레터 알림 API. apiKey 헤더로 인증한다")
public interface AdminSoptLetterNotificationApi {

  @Operation(
      summary = "솝레터 미작성 유저 리마인드 알림 전송",
      description = "현재 기수 SOPT 회원 가운데 솝레터를 한 번도 쓰지 않은 유저에게 보낸다. 대상이 없으면 보내지 않는다.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "솝레터 미작성 리마인드 알림 발송이 완료되었습니다"),
    @ApiResponse(responseCode = "400", description = "알림 제목이나 내용이 비었습니다.", content = @Content),
    @ApiResponse(responseCode = "401", description = "알림 API 키가 틀렸습니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<?>> sendUnwrittenReminder(
      @Parameter(description = "알림 API 키", required = true) String apiKey,
      AdminSoptLetterNotificationRequest request);

  @Operation(
      summary = "솝레터 새로운 주제 등록 알림 전송",
      description = "현재 기수 SOPT 회원 전체에게 보낸다. 대상이 없으면 보내지 않는다.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "솝레터 새 주제 알림 발송이 완료되었습니다"),
    @ApiResponse(responseCode = "400", description = "알림 제목이나 내용이 비었습니다.", content = @Content),
    @ApiResponse(responseCode = "401", description = "알림 API 키가 틀렸습니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<?>> sendNewTopic(
      @Parameter(description = "알림 API 키", required = true) String apiKey,
      AdminSoptLetterNotificationRequest request);
}
