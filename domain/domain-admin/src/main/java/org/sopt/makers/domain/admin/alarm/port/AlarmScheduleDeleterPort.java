package org.sopt.makers.domain.admin.alarm.port;

import java.time.LocalDateTime;

public interface AlarmScheduleDeleterPort {

  void delete(long alarmId, LocalDateTime scheduleDateTime);
}
