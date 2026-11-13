package org.sopt.makers.domain.crew.dining.result;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.crew.dining.model.DiningAttendance;
import org.sopt.makers.domain.crew.dining.model.DiningUser;

public record GetPayerRequestsResult(List<PayerRequest> requests) {

  public record PayerRequest(
      Long userId,
      String name,
      String profileImage,
      Long roundId,
      int roundNumber,
      LocalDateTime requestedAt) {
    public static PayerRequest of(DiningAttendance attendance, DiningUser user, int roundNumber) {
      return new PayerRequest(
          user.getId(),
          user.getName(),
          user.getProfileImage(),
          attendance.getRoundId(),
          roundNumber,
          attendance.getPayerRequestedAt());
    }
  }
}
