package org.sopt.makers.domain.crew.meeting.port;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.crew.meeting.MeetingApplyStatus;

/** 기존 {@code GET /meeting/v2/:meetingId/list}를 대체하는 내부 Port. */
public interface PlaygroundMeetingApplicantPort {

  PageResult<ApplicantInfo> findApplicants(
      Long meetingId, Long requesterUserId, ApplicantQuery query);

  record ApplicantQuery(
      int page, int take, List<MeetingApplyStatus> statuses, SortDirection sortDirection) {

    public ApplicantQuery {
      if (page < 1 || take < 1) {
        throw new IllegalArgumentException("page와 take는 1 이상이어야 합니다.");
      }
      statuses =
          statuses == null || statuses.isEmpty()
              ? List.of(MeetingApplyStatus.values())
              : List.copyOf(statuses);
      sortDirection = sortDirection == null ? SortDirection.DESC : sortDirection;
    }
  }

  enum SortDirection {
    ASC,
    DESC
  }

  record ApplicantInfo(
      Long id,
      int applyNumber,
      String content,
      LocalDateTime appliedDate,
      MeetingApplyStatus status,
      ApplicantUser user) {}

  record ApplicantUser(
      Long id,
      String name,
      Long orgId,
      RecentActivity recentActivity,
      String profileImage,
      String phone) {}

  record RecentActivity(String part, int generation) {}
}
