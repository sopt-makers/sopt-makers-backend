package org.sopt.makers.domain.playground.member.relation.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.member.relation.UserBlock;
import org.sopt.makers.domain.playground.member.relation.UserReport;
import org.sopt.makers.domain.playground.member.relation.port.UserBlockRepositoryPort;
import org.sopt.makers.domain.playground.member.relation.port.UserReportNotifierPort;
import org.sopt.makers.domain.playground.member.relation.port.UserReportRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRelationService {

  private final UserBlockRepositoryPort userBlockRepositoryPort;
  private final UserReportRepositoryPort userReportRepositoryPort;
  private final UserReportNotifierPort userReportNotifierPort;

  @Transactional
  public UserBlock activateBlock(Long blockerUserId, Long blockedUserId) {
    return userBlockRepositoryPort
        .findByBlockerUserIdAndBlockedUserId(blockerUserId, blockedUserId)
        .map(existing -> userBlockRepositoryPort.save(
            new UserBlock(
                existing.id(),
                existing.blockerUserId(),
                existing.blockedUserId(),
                true,
                existing.createdAt(),
                existing.updatedAt())))
        .orElseGet(() -> userBlockRepositoryPort.save(
            new UserBlock(null, blockerUserId, blockedUserId, true, null, null)));
  }

  public Optional<UserBlock> getBlockStatus(Long blockerUserId, Long blockedUserId) {
    return userBlockRepositoryPort.findByBlockerUserIdAndBlockedUserId(blockerUserId, blockedUserId);
  }

  @Transactional
  public UserReport reportUser(Long reporterUserId, Long reportedUserId) {
    userReportNotifierPort.notifyUserReport(reporterUserId, reportedUserId);

    return userReportRepositoryPort.save(
        new UserReport(null, reporterUserId, reportedUserId, null, null, null));
  }
}
