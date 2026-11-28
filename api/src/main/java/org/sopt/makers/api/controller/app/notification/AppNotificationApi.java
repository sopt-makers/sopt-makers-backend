package org.sopt.makers.api.controller.app.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.sopt.makers.api.controller.app.notification.dto.NotificationDetailResponse;
import org.sopt.makers.api.controller.app.notification.dto.NotificationSimpleResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.notification.NotificationCategory;
import org.springframework.http.ResponseEntity;

@Tag(name = "알림", description = "앱 인앱 알림함 API")
public interface AppNotificationApi {

  @Operation(summary = "알림 목록 조회", description = "카테고리를 주지 않으면 전체 알림을 최신순으로 조회한다.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "알림 목록 조회에 성공했습니다."),
    @ApiResponse(
        responseCode = "400",
        description = "page가 0 미만이거나 size가 1에서 100 사이를 벗어났습니다.",
        content = @Content)
  })
  ResponseEntity<BaseResponse<List<NotificationSimpleResponse>>> getNotifications(
      @Parameter(hidden = true) Long userId,
      @Parameter(description = "알림 분류. 주지 않으면 전체를 조회한다") NotificationCategory category,
      @Parameter(description = "0부터 시작하는 페이지 번호", example = "0") @Min(0) int page,
      @Parameter(description = "한 페이지 크기. 1에서 100 사이", example = "10") @Min(1) @Max(100) int size);

  @Operation(summary = "알림 상세 조회")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "알림 상세 조회에 성공했습니다."),
    @ApiResponse(responseCode = "404", description = "존재하지 않는 알림입니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<NotificationDetailResponse>> getNotificationDetail(
      @Parameter(hidden = true) Long userId,
      @Parameter(description = "알림 아이디", example = "01HQ2N8K3P") String notificationId);

  @Operation(summary = "알림 읽음 처리", description = "notificationId를 주지 않으면 유저의 알림을 모두 읽음 처리한다.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "알림 읽음 처리에 성공했습니다."),
    @ApiResponse(responseCode = "404", description = "존재하지 않는 알림입니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<Void>> updateNotificationRead(
      @Parameter(hidden = true) Long userId,
      @Parameter(description = "읽음 처리할 알림 아이디. 생략하면 전체를 읽음 처리한다", example = "01HQ2N8K3P")
          String notificationId);
}
