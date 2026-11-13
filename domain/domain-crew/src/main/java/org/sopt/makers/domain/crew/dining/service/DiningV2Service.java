package org.sopt.makers.domain.crew.dining.service;

import org.sopt.makers.domain.crew.dining.command.CreateAndUpdateDiningCommand;
import org.sopt.makers.domain.crew.dining.command.CreateAndUpdateRoundCommand;
import org.sopt.makers.domain.crew.dining.query.GetAttendeesQuery;
import org.sopt.makers.domain.crew.dining.query.GetDiningsQuery;
import org.sopt.makers.domain.crew.dining.result.CreateDiningResult;
import org.sopt.makers.domain.crew.dining.result.CreateRoundResult;
import org.sopt.makers.domain.crew.dining.result.GetApprovedPayersResult;
import org.sopt.makers.domain.crew.dining.result.GetAttendanceResult;
import org.sopt.makers.domain.crew.dining.result.GetAttendeeManagementResult;
import org.sopt.makers.domain.crew.dining.result.GetAttendeesCsvResult;
import org.sopt.makers.domain.crew.dining.result.GetAttendeesResult;
import org.sopt.makers.domain.crew.dining.result.GetDiningDetailResult;
import org.sopt.makers.domain.crew.dining.result.GetDiningsResult;
import org.sopt.makers.domain.crew.dining.result.GetPayerRequestsResult;

public interface DiningV2Service {

  CreateDiningResult createDining(CreateAndUpdateDiningCommand requestBody, Long userId);

  void updateDining(Long meetingId, CreateAndUpdateDiningCommand requestBody, Long userId);

  void deleteDining(Long meetingId, Long userId);

  void endDining(Long meetingId, Long userId);

  CreateRoundResult createRound(
      Long meetingId, CreateAndUpdateRoundCommand requestBody, Long userId);

  void updateRound(
      Long meetingId, Long roundId, CreateAndUpdateRoundCommand requestBody, Long userId);

  void deleteRound(Long meetingId, Long roundId, Long userId);

  void closeRound(Long meetingId, Long roundId, Long userId);

  void applyAttendance(Long meetingId, Long roundId, Long userId);

  void cancelAttendance(Long meetingId, Long roundId, Long userId);

  GetAttendanceResult getAttendance(Long meetingId, Long roundId, Long userId);

  GetAttendeeManagementResult getAttendees(
      Long meetingId, Long roundId, GetAttendeesQuery queryCommand, Long userId);

  GetAttendeesResult getAttendanceAttendees(Long meetingId, Long roundId, Long userId);

  GetAttendeesCsvResult getAttendeesCsv(Long meetingId, Long roundId, Long userId);

  void addAttendee(Long meetingId, Long roundId, Long attendeeUserId, Long userId);

  void deleteAttendee(Long meetingId, Long roundId, Long attendeeUserId, Long userId);

  void requestPayer(Long meetingId, Long roundId, Long userId);

  GetPayerRequestsResult getPayerRequests(Long meetingId, Long userId);

  GetApprovedPayersResult getApprovedPayers(Long meetingId, Long userId);

  void approvePayer(Long meetingId, Long roundId, Long requesterUserId, Long userId);

  void rejectPayer(Long meetingId, Long roundId, Long requesterUserId, Long userId);

  void revokePayer(Long meetingId, Long roundId, Long requesterUserId, Long userId);

  GetDiningsResult getDinings(GetDiningsQuery queryCommand, Long userId);

  GetDiningDetailResult getDiningDetail(Long meetingId, Long userId);

  int closeExpiredRounds();
}
