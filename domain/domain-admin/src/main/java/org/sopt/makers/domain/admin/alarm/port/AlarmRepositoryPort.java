package org.sopt.makers.domain.admin.alarm.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.admin.alarm.Alarm;
import org.sopt.makers.domain.admin.alarm.AlarmStatus;

public interface AlarmRepositoryPort {

  Alarm save(Alarm alarm);

  Optional<Alarm> findById(long id);

  List<Alarm> findAllOrdered(Integer generation, AlarmStatus status, int page, int size);

  int count(Integer generation, AlarmStatus status);

  void delete(Alarm alarm);
}
