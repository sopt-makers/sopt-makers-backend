package org.sopt.makers.api.controller.admin.alarm;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.admin.alarm.dto.AdminAlarmRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Admin Alarm", description = "어드민 알림 관련 API")
public interface AdminAlarmApi {

  @Operation(summary = "알림 즉시 발송")
  ResponseEntity<BaseResponse<?>> sendInstantAlarm(AdminAlarmRequest.InstantSendRequest request);

  @Operation(summary = "알림 예약 발송")
  ResponseEntity<BaseResponse<?>> sendScheduleAlarm(AdminAlarmRequest.ScheduleSendRequest request);

  @Operation(summary = "알림 목록 조회")
  ResponseEntity<BaseResponse<?>> getAlarms(Integer generation, String status, int page, int size);

  @Operation(summary = "알림 상세 조회")
  ResponseEntity<BaseResponse<?>> getAlarm(long alarmId);

  @Operation(summary = "알림 삭제")
  ResponseEntity<BaseResponse<?>> deleteAlarm(long alarmId);

  @Operation(summary = "예약 알림 상태 업데이트")
  ResponseEntity<BaseResponse<?>> updateAlarmStatus(long alarmId, AdminAlarmRequest.ScheduleStatusUpdateRequest request);
}
