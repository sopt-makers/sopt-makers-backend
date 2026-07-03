package org.sopt.makers.api.controller.admin.alarm;

import static org.sopt.makers.api.controller.admin.alarm.AdminAlarmSuccessCode.SUCCESS_DELETE_ALARM;
import static org.sopt.makers.api.controller.admin.alarm.AdminAlarmSuccessCode.SUCCESS_GET_ALARM;
import static org.sopt.makers.api.controller.admin.alarm.AdminAlarmSuccessCode.SUCCESS_GET_ALARMS;
import static org.sopt.makers.api.controller.admin.alarm.AdminAlarmSuccessCode.SUCCESS_SCHEDULE_ALARM;
import static org.sopt.makers.api.controller.admin.alarm.AdminAlarmSuccessCode.SUCCESS_SEND_ALARM;
import static org.sopt.makers.api.controller.admin.alarm.AdminAlarmSuccessCode.SUCCESS_UPDATE_ALARM_STATUS;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.admin.alarm.dto.AdminAlarmRequest;
import org.sopt.makers.api.controller.admin.alarm.dto.AdminAlarmResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.admin.alarm.AlarmStatus;
import org.sopt.makers.domain.admin.alarm.service.AlarmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/alarms")
public class AdminAlarmController implements AdminAlarmApi {

  private final AlarmService alarmService;

  @Override
  @PostMapping("/send")
  public ResponseEntity<BaseResponse<?>> sendInstantAlarm(
      @Valid @RequestBody AdminAlarmRequest.InstantSendRequest request) {
    long id = alarmService.sendInstantAlarm(request.toCommand());
    return ResponseFactory.success(SUCCESS_SEND_ALARM, AdminAlarmResponse.AlarmCreated.from(id));
  }

  @Override
  @PostMapping("/schedule")
  public ResponseEntity<BaseResponse<?>> sendScheduleAlarm(
      @Valid @RequestBody AdminAlarmRequest.ScheduleSendRequest request) {
    long id = alarmService.sendScheduleAlarm(request.toCommand());
    return ResponseFactory.success(SUCCESS_SCHEDULE_ALARM, AdminAlarmResponse.AlarmCreated.from(id));
  }

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getAlarms(
      @RequestParam(required = false) Integer generation,
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    AlarmStatus alarmStatus = status != null ? AlarmStatus.valueOf(status) : null;
    return ResponseFactory.success(
        SUCCESS_GET_ALARMS,
        AdminAlarmResponse.AlarmList.from(alarmService.getAlarms(generation, alarmStatus, page, size)));
  }

  @Override
  @GetMapping("/{alarmId}")
  public ResponseEntity<BaseResponse<?>> getAlarm(@PathVariable long alarmId) {
    return ResponseFactory.success(SUCCESS_GET_ALARM, AdminAlarmResponse.AlarmDetail.from(alarmService.getAlarm(alarmId)));
  }

  @Override
  @DeleteMapping("/{alarmId}")
  public ResponseEntity<BaseResponse<?>> deleteAlarm(@PathVariable long alarmId) {
    alarmService.deleteAlarm(alarmId);
    return ResponseFactory.success(SUCCESS_DELETE_ALARM);
  }

  @Override
  @PatchMapping("/{alarmId}")
  public ResponseEntity<BaseResponse<?>> updateAlarmStatus(
      @PathVariable long alarmId,
      @RequestBody AdminAlarmRequest.ScheduleStatusUpdateRequest request) {
    alarmService.updateAlarmStatus(alarmId, request.sendAt());
    return ResponseFactory.success(SUCCESS_UPDATE_ALARM_STATUS);
  }
}
