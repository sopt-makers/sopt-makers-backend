package org.sopt.makers.storage.db.admin.querydsl;

import java.util.List;
import org.sopt.makers.domain.admin.alarm.AlarmStatus;
import org.sopt.makers.storage.db.admin.entity.AlarmEntity;

public interface AlarmQuerydslRepository {

  List<AlarmEntity> findOrderByCreatedAt(
      Integer generation, AlarmStatus status, int page, int size);

  int count(Integer generation, AlarmStatus status);
}
