package org.sopt.makers.domain.crew.dining.result;

import java.util.List;
import org.sopt.makers.domain.crew.dining.model.DiningAttendance;
import org.sopt.makers.domain.crew.dining.model.DiningUser;

public record GetApprovedPayersResult(List<ApprovedPayer> payers) {

  public record ApprovedPayer(
      Long userId, String name, String profileImage, Long roundId, int roundNumber) {
    public static ApprovedPayer of(DiningAttendance attendance, DiningUser user, int roundNumber) {
      return new ApprovedPayer(
          user.getId(),
          user.getName(),
          user.getProfileImage(),
          attendance.getRoundId(),
          roundNumber);
    }
  }
}
