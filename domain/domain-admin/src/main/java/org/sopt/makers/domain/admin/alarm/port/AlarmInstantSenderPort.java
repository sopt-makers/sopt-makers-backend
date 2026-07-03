package org.sopt.makers.domain.admin.alarm.port;

import org.sopt.makers.domain.admin.alarm.Alarm;

public interface AlarmInstantSenderPort {

  void send(Alarm alarm);
}
