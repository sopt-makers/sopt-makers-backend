package org.sopt.makers.domain.admin.alarm.port;

import java.util.List;
import org.sopt.makers.core.type.Part;

public interface AlarmMemberQueryPort {

  List<Long> findAllUserIds();

  List<Long> findActiveUserIdsByGenerationAndPart(int generation, Part part);
}
