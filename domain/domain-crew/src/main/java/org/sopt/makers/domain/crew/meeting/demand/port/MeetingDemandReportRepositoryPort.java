package org.sopt.makers.domain.crew.meeting.demand.port;

import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandReport;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandReportTarget;

public interface MeetingDemandReportRepositoryPort {

  MeetingDemandReport save(MeetingDemandReport report);

  boolean existsByUserIdAndTarget(Long userId, MeetingDemandReportTarget targetType, Long targetId);

  void deleteAllByTarget(MeetingDemandReportTarget targetType, Long targetId);
}
