package org.sopt.makers.domain.playground.member.relation.port;

public interface UserReportNotifierPort {

  void notifyUserReport(Long reporterUserId, Long reportedUserId);
}
