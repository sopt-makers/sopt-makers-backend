package org.sopt.makers.domain.admin.alarm.port;

import org.sopt.makers.domain.admin.alarm.Alarm;

public interface AlarmScheduleSenderPort {

  void send(Alarm alarm);
}
