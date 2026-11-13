package org.sopt.makers.domain.crew.dining.service;

import static org.sopt.makers.domain.crew.dining.exception.DiningFailure.*;
import static org.sopt.makers.domain.crew.dining.service.DiningMeetingMapper.*;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.crew.dining.command.CreateAndUpdateDiningCommand;
import org.sopt.makers.domain.crew.dining.command.CreateAndUpdateRoundCommand;
import org.sopt.makers.domain.crew.dining.event.DiningAttendeeAddedEvent;
import org.sopt.makers.domain.crew.dining.event.DiningAttendeeRemovedEvent;
import org.sopt.makers.domain.crew.dining.event.DiningDeletedEvent;
import org.sopt.makers.domain.crew.dining.event.DiningPayerApprovedEvent;
import org.sopt.makers.domain.crew.dining.event.DiningPayerRejectedEvent;
import org.sopt.makers.domain.crew.dining.event.DiningPayerRequestedEvent;
import org.sopt.makers.domain.crew.dining.event.DiningPayerRevokedEvent;
import org.sopt.makers.domain.crew.dining.event.DiningRoundCreatedEvent;
import org.sopt.makers.domain.crew.dining.event.DiningRoundDeletedEvent;
import org.sopt.makers.domain.crew.dining.event.DiningRoundUpdatedEvent;
import org.sopt.makers.domain.crew.dining.event.DiningUpdatedEvent;
import org.sopt.makers.domain.crew.dining.exception.DiningException;
import org.sopt.makers.domain.crew.dining.model.DiningAttendance;
import org.sopt.makers.domain.crew.dining.model.DiningMeeting;
import org.sopt.makers.domain.crew.dining.model.DiningRound;
import org.sopt.makers.domain.crew.dining.model.DiningRounds;
import org.sopt.makers.domain.crew.dining.model.DiningUser;
import org.sopt.makers.domain.crew.dining.model.enums.DiningPayerStatus;
import org.sopt.makers.domain.crew.dining.model.enums.DiningRoundStatus;
import org.sopt.makers.domain.crew.dining.model.projection.DiningAttendeeCountProjection;
import org.sopt.makers.domain.crew.dining.port.DiningAttendancePort;
import org.sopt.makers.domain.crew.dining.port.DiningMeetingPort;
import org.sopt.makers.domain.crew.dining.port.DiningRoundPort;
import org.sopt.makers.domain.crew.dining.port.DiningSearchPort;
import org.sopt.makers.domain.crew.dining.port.DiningUserPort;
import org.sopt.makers.domain.crew.dining.query.GetAttendeesQuery;
import org.sopt.makers.domain.crew.dining.query.GetDiningsQuery;
import org.sopt.makers.domain.crew.dining.query.PageMetaDto;
import org.sopt.makers.domain.crew.dining.result.CreateDiningResult;
import org.sopt.makers.domain.crew.dining.result.CreateRoundResult;
import org.sopt.makers.domain.crew.dining.result.DiningLeader;
import org.sopt.makers.domain.crew.dining.result.GetApprovedPayersResult;
import org.sopt.makers.domain.crew.dining.result.GetAttendanceResult;
import org.sopt.makers.domain.crew.dining.result.GetAttendeeManagementResult;
import org.sopt.makers.domain.crew.dining.result.GetAttendeesCsvResult;
import org.sopt.makers.domain.crew.dining.result.GetAttendeesResult;
import org.sopt.makers.domain.crew.dining.result.GetDiningDetailResult;
import org.sopt.makers.domain.crew.dining.result.GetDiningDetailResult.MyAttendanceStatus;
import org.sopt.makers.domain.crew.dining.result.GetDiningsResult;
import org.sopt.makers.domain.crew.dining.result.GetDiningsResult.DiningCard;
import org.sopt.makers.domain.crew.dining.result.GetPayerRequestsResult;
import org.sopt.makers.domain.crew.meeting.MeetingCategory;
import org.sopt.makers.domain.crew.meeting.MeetingImage;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiningV2ServiceImpl implements DiningV2Service {

  private final DiningMeetingPort meetingRepository;
  private final DiningRoundPort diningRoundRepository;
  private final DiningAttendancePort diningAttendanceRepository;
  private final DiningSearchPort diningSearchRepository;
  private final DiningUserPort userRepository;

  private final DiningMeetingMapper diningMeetingMapper;
  private final DiningMapLinkValidator diningMapLinkValidator;
  private final DiningPermissionValidator diningPermissionValidator;
  private final DiningDeletionHistoryRecorder diningDeletionHistoryRecorder;

  @org.springframework.beans.factory.annotation.Value(
      "${crew.dining.default-image:${DINING_IMAGE:}}")
  private String defaultDiningImage;

  private final org.sopt.makers.domain.crew.meeting.port.MeetingActiveGenerationPort
      activeGenerationProvider;
  private final ApplicationEventPublisher eventPublisher;
  private final Clock clock;
  private final org.sopt.makers.domain.crew.dining.port.DiningCsvPort diningCsvPort;

  /** 회식 등록: 회식(meeting) + 1차 회차 생성, 개설자는 1차에 자동 참석한다. */
  @Override
  @Transactional
  public CreateDiningResult createDining(CreateAndUpdateDiningCommand requestBody, Long userId) {
    userRepository.findByIdOrThrow(userId);

    LocalDate diningDate = parseDiningDate(requestBody.diningDate());
    validateNotPastDiningDate(diningDate);
    diningMapLinkValidator.validate(requestBody.naverMapLink(), requestBody.kakaoMapLink());

    DiningMeeting dining =
        meetingRepository.save(
            diningMeetingMapper.toDiningMeeting(
                requestBody.title(),
                getImageUrlsOrDefault(requestBody.files()),
                diningDate,
                normalizeJoinableParts(requestBody.joinableParts()),
                userId,
                java.time.LocalDateTime.now(clock.withZone(java.time.ZoneId.of("Asia/Seoul"))),
                activeGenerationProvider.getActiveGeneration()));

    DiningRound firstRound =
        diningRoundRepository.save(
            DiningRound.builder()
                .meetingId(dining.getId())
                .place(blankToNull(requestBody.place()))
                .naverMapLink(blankToNull(requestBody.naverMapLink()))
                .kakaoMapLink(blankToNull(requestBody.kakaoMapLink()))
                .startTime(parseStartTime(requestBody.startTime()))
                .build());
    attend(firstRound, userId);

    return CreateDiningResult.from(dining.getId());
  }

  /** 회식 수정: 회식 정보 + 1차 회차 정보. 수정 시에는 지난 날짜도 허용한다. */
  @Override
  @Transactional
  public void updateDining(Long meetingId, CreateAndUpdateDiningCommand requestBody, Long userId) {
    DiningMeeting dining = meetingRepository.findDiningByIdForUpdateOrThrow(meetingId);
    diningPermissionValidator.validateLeader(dining, userId);
    dining.validateDiningNotEnded();

    LocalDate diningDate = parseDiningDate(requestBody.diningDate());
    diningMapLinkValidator.validate(requestBody.naverMapLink(), requestBody.kakaoMapLink());

    // 수정 알림 기준: 회식명·일자만 회식 정보로 본다. (이미지, 참여 파트 변경은 알림 대상 아님)
    boolean dateChanged = !dining.getmStartDate().toLocalDate().equals(diningDate);
    boolean diningInfoChanged =
        dateChanged || !Objects.equals(dining.getTitle(), requestBody.title());

    dining.updateDining(
        requestBody.title(),
        getImageUrlsOrDefault(requestBody.files()),
        toStartOfDay(diningDate),
        toEndOfDay(diningDate),
        normalizeJoinableParts(requestBody.joinableParts()));

    DiningRounds rounds = getRounds(meetingId);
    DiningRound firstRound = rounds.getFirstRound();
    boolean firstRoundChanged =
        firstRound.update(
            blankToNull(requestBody.place()),
            blankToNull(requestBody.naverMapLink()),
            blankToNull(requestBody.kakaoMapLink()),
            parseStartTime(requestBody.startTime()));

    meetingRepository.save(dining);
    diningRoundRepository.save(firstRound);

    // 회식 정보가 바뀌면 1차 변경 여부와 관계없이 회식 수정 1건, 1차 정보만 바뀌면 1차 회차 수정 1건
    if (diningInfoChanged) {
      List<Long> receiverUserIds =
          dateChanged
              ? diningAttendanceRepository.findDistinctUserIdsByRoundIdIn(rounds.getRoundIds())
              : findAttendeeUserIds(firstRound.getId());
      eventPublisher.publishEvent(
          new DiningUpdatedEvent(meetingId, dining.getTitle(), receiverUserIds));
    } else if (firstRoundChanged) {
      eventPublisher.publishEvent(
          new DiningRoundUpdatedEvent(
              meetingId,
              dining.getTitle(),
              rounds.getRoundNumber(firstRound.getId()),
              findAttendeeUserIds(firstRound.getId())));
    }
  }

  /** 회식 삭제(= 1차 삭제): 삭제 이력을 먼저 남기고 참석 → 회차 → 회식 순으로 삭제한다. 마감된 회차가 있거나 종료된 회식은 삭제할 수 없다. */
  @Override
  @Transactional
  public void deleteDining(Long meetingId, Long userId) {
    DiningMeeting dining = meetingRepository.findDiningByIdForUpdateOrThrow(meetingId);
    diningPermissionValidator.validateLeader(dining, userId);
    dining.validateDiningNotEnded();

    DiningRounds rounds = getRoundsForUpdate(meetingId);
    if (rounds.hasClosedRound()) {
      throw new DiningException(CANNOT_DELETE_DINING_WITH_CLOSED_ROUND);
    }

    List<DiningAttendance> attendances =
        diningAttendanceRepository.findAllByRoundIdInForUpdate(rounds.getRoundIds());
    diningDeletionHistoryRecorder.recordDiningDeletion(
        dining, rounds.getRounds(), attendances, userId);

    diningAttendanceRepository.deleteAllInBatch(attendances);
    diningRoundRepository.deleteAllInBatch(rounds.getRounds());
    meetingRepository.delete(dining);

    eventPublisher.publishEvent(
        new DiningDeletedEvent(meetingId, dining.getTitle(), toUserIds(attendances)));
  }

  /** 회식 종료: 이후 회차 추가와 회식·회차 수정이 불가하며, 마감하지 않은 회차는 함께 마감한다. 참석자 관리는 종료 후에도 가능하다. */
  @Override
  @Transactional
  public void endDining(Long meetingId, Long userId) {
    DiningMeeting dining = meetingRepository.findDiningByIdForUpdateOrThrow(meetingId);
    diningPermissionValidator.validateLeader(dining, userId);

    LocalDateTime now =
        java.time.LocalDateTime.now(clock.withZone(java.time.ZoneId.of("Asia/Seoul")));
    dining.endDining(now);
    getRounds(meetingId)
        .getOpenRounds()
        .forEach(
            round -> {
              round.close(now);
              diningRoundRepository.save(round);
            });
    meetingRepository.save(dining);
  }

  /** 회차 추가: 개설자는 새 회차에 자동 참석하고, 커밋 후 이전 회차 참석자에게 알림을 보낸다. */
  @Override
  @Transactional
  public CreateRoundResult createRound(
      Long meetingId, CreateAndUpdateRoundCommand requestBody, Long userId) {
    DiningMeeting dining = meetingRepository.findDiningByIdForUpdateOrThrow(meetingId);
    diningPermissionValidator.validateLeader(dining, userId);
    dining.validateDiningNotEnded();
    diningMapLinkValidator.validate(requestBody.naverMapLink(), requestBody.kakaoMapLink());

    List<Long> previousRoundIds = getRounds(meetingId).getRoundIds();
    List<Long> attendeeUserIds =
        previousRoundIds.isEmpty()
            ? List.of()
            : diningAttendanceRepository.findDistinctUserIdsByRoundIdIn(previousRoundIds).stream()
                .filter(attendeeUserId -> !attendeeUserId.equals(dining.getUserId()))
                .toList();

    DiningRound round =
        diningRoundRepository.save(
            DiningRound.builder()
                .meetingId(meetingId)
                .place(blankToNull(requestBody.place()))
                .naverMapLink(blankToNull(requestBody.naverMapLink()))
                .kakaoMapLink(blankToNull(requestBody.kakaoMapLink()))
                .startTime(parseStartTime(requestBody.startTime()))
                .build());
    attend(round, userId);

    eventPublisher.publishEvent(
        new DiningRoundCreatedEvent(meetingId, round.getId(), attendeeUserIds));

    return CreateRoundResult.from(round.getId());
  }

  /** 회차 수정: 장소, 링크, 시작 시간만 수정한다. 마감된 회차도 수정할 수 있다. */
  @Override
  @Transactional
  public void updateRound(
      Long meetingId, Long roundId, CreateAndUpdateRoundCommand requestBody, Long userId) {
    DiningMeeting dining = meetingRepository.findDiningByIdForUpdateOrThrow(meetingId);
    diningPermissionValidator.validateLeader(dining, userId);
    dining.validateDiningNotEnded();
    diningMapLinkValidator.validate(requestBody.naverMapLink(), requestBody.kakaoMapLink());

    DiningRound round = findRoundOfDining(meetingId, roundId);
    boolean changed =
        round.update(
            blankToNull(requestBody.place()),
            blankToNull(requestBody.naverMapLink()),
            blankToNull(requestBody.kakaoMapLink()),
            parseStartTime(requestBody.startTime()));
    diningRoundRepository.save(round);
    if (changed) {
      eventPublisher.publishEvent(
          new DiningRoundUpdatedEvent(
              meetingId,
              dining.getTitle(),
              getRounds(meetingId).getRoundNumber(roundId),
              findAttendeeUserIds(roundId)));
    }
  }

  /** 회차 삭제(2차 이상): 삭제 이력을 먼저 남기고 참석 → 회차 순으로 삭제한다. 뒤 회차 번호는 자동으로 당겨진다. */
  @Override
  @Transactional
  public void deleteRound(Long meetingId, Long roundId, Long userId) {
    DiningMeeting dining = meetingRepository.findDiningByIdForUpdateOrThrow(meetingId);
    diningPermissionValidator.validateLeader(dining, userId);
    dining.validateDiningNotEnded();

    DiningRounds rounds = getRoundsForUpdate(meetingId);
    DiningRound round = rounds.getRound(roundId);
    if (rounds.isFirstRound(round)) {
      throw new DiningException(CANNOT_DELETE_FIRST_DINING_ROUND);
    }
    if (round.isClosed()) {
      throw new DiningException(CANNOT_DELETE_CLOSED_DINING_ROUND);
    }

    List<DiningAttendance> attendances =
        diningAttendanceRepository.findAllByRoundIdInForUpdate(List.of(roundId));
    diningDeletionHistoryRecorder.recordRoundDeletion(round, attendances, userId);

    diningAttendanceRepository.deleteAllInBatch(attendances);
    diningRoundRepository.delete(round);

    eventPublisher.publishEvent(
        new DiningRoundDeletedEvent(
            meetingId, dining.getTitle(), rounds.getRoundNumber(roundId), toUserIds(attendances)));
  }

  /** 회차 마감(확정) */
  @Override
  @Transactional
  public void closeRound(Long meetingId, Long roundId, Long userId) {
    DiningMeeting dining = meetingRepository.findDiningByIdForUpdateOrThrow(meetingId);
    diningPermissionValidator.validateLeader(dining, userId);

    DiningRound round = findRoundOfDining(meetingId, roundId);
    round.close(java.time.LocalDateTime.now(clock.withZone(java.time.ZoneId.of("Asia/Seoul"))));
    diningRoundRepository.save(round);
  }

  @Override
  @Transactional
  public void applyAttendance(Long meetingId, Long roundId, Long userId) {
    meetingRepository.findDiningByIdForUpdateOrThrow(meetingId);
    DiningRound round = findRoundOfDining(meetingId, roundId);
    round.validateNotClosed();
    addAttendance(round, userId);
  }

  @Override
  @Transactional
  public void cancelAttendance(Long meetingId, Long roundId, Long userId) {
    meetingRepository.findDiningByIdForUpdateOrThrow(meetingId);
    DiningRound round = findRoundOfDining(meetingId, roundId);
    round.validateNotClosed();
    deleteAttendance(round, userId, userId);
  }

  @Override
  public GetAttendanceResult getAttendance(Long meetingId, Long roundId, Long userId) {
    meetingRepository.findDiningByIdOrThrow(meetingId);
    findRoundOfDining(meetingId, roundId);
    Optional<DiningAttendance> attendance =
        diningAttendanceRepository.findByRoundIdAndUserId(roundId, userId);
    return new GetAttendanceResult(
        diningAttendanceRepository.countByRoundId(roundId),
        attendance.isPresent(),
        attendance.map(DiningAttendance::getPayerStatus).orElse(null));
  }

  @Override
  public GetAttendeeManagementResult getAttendees(
      Long meetingId, Long roundId, GetAttendeesQuery queryCommand, Long userId) {
    DiningMeeting dining = meetingRepository.findDiningByIdOrThrow(meetingId);
    DiningRound round = findRoundOfDining(meetingId, roundId);
    diningPermissionValidator.validateLeaderOrApprovedPayer(dining, round, userId);
    boolean ascending = "asc".equals(queryCommand.getDate());
    var pageable =
        new org.sopt.makers.core.pagination.PageQuery(
            queryCommand.getPage(), queryCommand.getTake());
    var page = diningAttendanceRepository.findAllByRoundId(roundId, pageable, ascending);
    PageMetaDto meta = new PageMetaDto(queryCommand, Math.toIntExact(page.totalElements()));
    if (page.content().isEmpty()) {
      return new GetAttendeeManagementResult(List.of(), meta);
    }
    List<DiningAttendance> attendances = page.content();
    Map<Long, DiningUser> users =
        userRepository
            .findAllByIdInOrThrow(attendances.stream().map(DiningAttendance::getUserId).toList())
            .stream()
            .collect(Collectors.toMap(DiningUser::getId, user -> user));
    return new GetAttendeeManagementResult(
        IntStream.range(0, attendances.size())
            .mapToObj(
                index ->
                    GetAttendeeManagementResult.Attendee.of(
                        users.get(attendances.get(index).getUserId()),
                        ascending
                            ? ((long) (pageable.page() - 1) * pageable.limit()) + index + 1
                            : page.totalElements()
                                - ((long) (pageable.page() - 1) * pageable.limit())
                                - index))
            .toList(),
        meta);
  }

  @Override
  public GetAttendeesCsvResult getAttendeesCsv(Long meetingId, Long roundId, Long userId) {
    DiningMeeting dining = meetingRepository.findDiningByIdOrThrow(meetingId);
    DiningRound round = findRoundOfDining(meetingId, roundId);
    diningPermissionValidator.validateLeaderOrApprovedPayer(dining, round, userId);

    List<DiningAttendance> attendances =
        diningAttendanceRepository.findAllByRoundIdOrderByCreatedTimestampAscIdAsc(roundId);
    Map<Long, DiningUser> users =
        attendances.isEmpty()
            ? Map.of()
            : userRepository
                .findAllByIdInOrThrow(
                    attendances.stream().map(DiningAttendance::getUserId).toList())
                .stream()
                .collect(Collectors.toMap(DiningUser::getId, Function.identity()));

    return new GetAttendeesCsvResult(
        diningCsvPort.upload(
            attendances.stream().map(attendance -> users.get(attendance.getUserId())).toList()));
  }

  @Override
  public GetAttendeesResult getAttendanceAttendees(Long meetingId, Long roundId, Long userId) {
    meetingRepository.findDiningByIdOrThrow(meetingId);
    findRoundOfDining(meetingId, roundId);
    // 참석 등록 시각 오름차순, 같으면 참석 기록 ID 오름차순 (참석자 관리 목록·CSV와 동일)
    List<Long> userIds =
        diningAttendanceRepository.findAllByRoundIdOrderByCreatedTimestampAscIdAsc(roundId).stream()
            .map(DiningAttendance::getUserId)
            .toList();
    if (userIds.isEmpty()) {
      return new GetAttendeesResult(List.of());
    }
    Map<Long, DiningUser> users =
        userRepository.findAllById(userIds).stream()
            .collect(Collectors.toMap(DiningUser::getId, Function.identity()));
    return new GetAttendeesResult(
        userIds.stream()
            .map(users::get)
            .filter(Objects::nonNull)
            .map(GetAttendeesResult.Attendee::of)
            .toList());
  }

  @Override
  @Transactional
  public void addAttendee(Long meetingId, Long roundId, Long attendeeUserId, Long userId) {
    DiningMeeting dining = meetingRepository.findDiningByIdForUpdateOrThrow(meetingId);
    DiningRound round = findRoundOfDining(meetingId, roundId);
    diningPermissionValidator.validateLeaderOrApprovedPayer(dining, round, userId);
    addAttendance(round, attendeeUserId);
    eventPublisher.publishEvent(
        new DiningAttendeeAddedEvent(
            meetingId,
            dining.getTitle(),
            getRounds(meetingId).getRoundNumber(roundId),
            attendeeUserId));
  }

  @Override
  @Transactional
  public void deleteAttendee(Long meetingId, Long roundId, Long attendeeUserId, Long userId) {
    DiningMeeting dining = meetingRepository.findDiningByIdForUpdateOrThrow(meetingId);
    DiningRound round = findRoundOfDining(meetingId, roundId);
    diningPermissionValidator.validateLeaderOrApprovedPayer(dining, round, userId);
    deleteAttendance(round, attendeeUserId, userId);
    // 관리자가 자기 자신을 삭제한 경우는 알리지 않는다.
    if (!attendeeUserId.equals(userId)) {
      eventPublisher.publishEvent(
          new DiningAttendeeRemovedEvent(
              meetingId,
              dining.getTitle(),
              getRounds(meetingId).getRoundNumber(roundId),
              attendeeUserId));
    }
  }

  @Override
  @Transactional
  public void requestPayer(Long meetingId, Long roundId, Long userId) {
    DiningMeeting dining = meetingRepository.findDiningByIdForUpdateOrThrow(meetingId);
    findRoundOfDining(meetingId, roundId);

    if (diningPermissionValidator.isLeader(dining, userId)) {
      throw new DiningException(CANNOT_REQUEST_DINING_PAYER_AS_LEADER);
    }

    DiningAttendance attendance = findAttendanceForPayerUpdate(roundId, userId);
    attendance.requestPayer(
        java.time.LocalDateTime.now(clock.withZone(java.time.ZoneId.of("Asia/Seoul"))));
    diningAttendanceRepository.save(attendance);
    DiningUser requester = userRepository.findByIdOrThrow(userId);
    eventPublisher.publishEvent(
        new DiningPayerRequestedEvent(
            meetingId,
            roundId,
            userId,
            dining.getUserId(),
            dining.getTitle(),
            requester.getName(),
            getRounds(meetingId).getRoundNumber(roundId)));
  }

  @Override
  @Transactional
  public void approvePayer(Long meetingId, Long roundId, Long requesterUserId, Long userId) {
    DiningMeeting dining = meetingRepository.findDiningByIdForUpdateOrThrow(meetingId);
    diningPermissionValidator.validateLeader(dining, userId);
    findRoundOfDining(meetingId, roundId);
    DiningAttendance attendance = findAttendanceForPayerUpdate(roundId, requesterUserId);
    attendance.approvePayer();
    diningAttendanceRepository.save(attendance);
    eventPublisher.publishEvent(
        new DiningPayerApprovedEvent(
            meetingId,
            roundId,
            requesterUserId,
            dining.getTitle(),
            getRounds(meetingId).getRoundNumber(roundId)));
  }

  @Override
  @Transactional
  public void rejectPayer(Long meetingId, Long roundId, Long requesterUserId, Long userId) {
    DiningMeeting dining = meetingRepository.findDiningByIdForUpdateOrThrow(meetingId);
    diningPermissionValidator.validateLeader(dining, userId);
    findRoundOfDining(meetingId, roundId);
    DiningAttendance attendance = findAttendanceForPayerUpdate(roundId, requesterUserId);
    attendance.validatePendingPayer();
    attendance.clearPayer();
    diningAttendanceRepository.save(attendance);
    eventPublisher.publishEvent(
        new DiningPayerRejectedEvent(
            meetingId,
            roundId,
            requesterUserId,
            dining.getTitle(),
            getRounds(meetingId).getRoundNumber(roundId)));
  }

  @Override
  @Transactional
  public void revokePayer(Long meetingId, Long roundId, Long requesterUserId, Long userId) {
    DiningMeeting dining = meetingRepository.findDiningByIdForUpdateOrThrow(meetingId);
    diningPermissionValidator.validateLeader(dining, userId);
    findRoundOfDining(meetingId, roundId);
    DiningAttendance attendance = findAttendanceForPayerUpdate(roundId, requesterUserId);
    attendance.validateApprovedPayer();
    attendance.clearPayer();
    diningAttendanceRepository.save(attendance);
    eventPublisher.publishEvent(
        new DiningPayerRevokedEvent(
            meetingId,
            roundId,
            requesterUserId,
            dining.getTitle(),
            getRounds(meetingId).getRoundNumber(roundId)));
  }

  @Override
  public GetPayerRequestsResult getPayerRequests(Long meetingId, Long userId) {
    DiningMeeting dining = meetingRepository.findDiningByIdOrThrow(meetingId);
    diningPermissionValidator.validateLeader(dining, userId);
    DiningRounds rounds = getRounds(meetingId);
    List<DiningAttendance> requests =
        diningAttendanceRepository.findAllByRoundIdInAndPayerStatusOrderByPayerRequestedAtAscIdAsc(
            rounds.getRoundIds(), DiningPayerStatus.PENDING);
    if (requests.isEmpty()) {
      return new GetPayerRequestsResult(List.of());
    }
    Map<Long, DiningUser> users =
        userRepository
            .findAllByIdInOrThrow(
                requests.stream().map(DiningAttendance::getUserId).distinct().toList())
            .stream()
            .collect(Collectors.toMap(DiningUser::getId, user -> user));
    return new GetPayerRequestsResult(
        requests.stream()
            .map(
                attendance ->
                    GetPayerRequestsResult.PayerRequest.of(
                        attendance,
                        users.get(attendance.getUserId()),
                        rounds.getRoundNumber(attendance.getRoundId())))
            .toList());
  }

  @Override
  public GetApprovedPayersResult getApprovedPayers(Long meetingId, Long userId) {
    DiningMeeting dining = meetingRepository.findDiningByIdOrThrow(meetingId);
    diningPermissionValidator.validateLeader(dining, userId);
    DiningRounds rounds = getRounds(meetingId);
    List<DiningAttendance> payers =
        diningAttendanceRepository.findAllByRoundIdInAndPayerStatusOrderByPayerRequestedAtAscIdAsc(
            rounds.getRoundIds(), DiningPayerStatus.APPROVED);
    if (payers.isEmpty()) {
      return new GetApprovedPayersResult(List.of());
    }
    Map<Long, DiningUser> users =
        userRepository.findAllByIdInOrThrow(toUserIds(payers)).stream()
            .collect(Collectors.toMap(DiningUser::getId, user -> user));
    return new GetApprovedPayersResult(
        payers.stream()
            .map(
                attendance ->
                    GetApprovedPayersResult.ApprovedPayer.of(
                        attendance,
                        users.get(attendance.getUserId()),
                        rounds.getRoundNumber(attendance.getRoundId())))
            .toList());
  }

  private DiningAttendance findAttendanceForPayerUpdate(Long roundId, Long userId) {
    return diningAttendanceRepository
        .findByRoundIdAndUserIdForUpdate(roundId, userId)
        .orElseThrow(() -> new DiningException(NOT_FOUND_DINING_ATTENDANCE));
  }

  private void attend(DiningRound round, Long userId) {
    diningAttendanceRepository.save(
        DiningAttendance.builder().roundId(round.getId()).userId(userId).build());
  }

  private List<Long> findAttendeeUserIds(Long roundId) {
    return diningAttendanceRepository.findDistinctUserIdsByRoundIdIn(List.of(roundId));
  }

  private static List<Long> toUserIds(List<DiningAttendance> attendances) {
    return attendances.stream().map(DiningAttendance::getUserId).distinct().toList();
  }

  private DiningRounds getRounds(Long meetingId) {
    return new DiningRounds(diningRoundRepository.findAllByMeetingIdOrderByIdAsc(meetingId));
  }

  /** 삭제 전용. 참석 추가와 직렬화하도록 회차를 id 순서로 잠근다. */
  private DiningRounds getRoundsForUpdate(Long meetingId) {
    return new DiningRounds(diningRoundRepository.findAllByMeetingIdForUpdate(meetingId));
  }

  private DiningRound findRoundOfDining(Long meetingId, Long roundId) {
    DiningRound round = diningRoundRepository.findByIdOrThrow(roundId);
    if (!round.getMeetingId().equals(meetingId)) {
      throw new DiningException(NOT_FOUND_DINING_ROUND);
    }
    return round;
  }

  private List<MeetingImage> getImageUrlsOrDefault(List<String> files) {
    if (files == null || files.isEmpty()) {
      if (defaultDiningImage == null || defaultDiningImage.isBlank()) {
        throw new DiningException(DINING_IMAGE_NOT_CONFIGURED);
      }
      return toImageUrls(List.of(defaultDiningImage));
    }
    return toImageUrls(files);
  }

  private void validateNotPastDiningDate(LocalDate diningDate) {
    if (diningDate.isBefore(
        java.time.LocalDateTime.now(clock.withZone(java.time.ZoneId.of("Asia/Seoul")))
            .toLocalDate())) {
      throw new DiningException(PAST_DINING_DATE);
    }
  }

  private void addAttendance(DiningRound round, Long userId) {
    userRepository.findByIdOrThrow(userId);
    if (diningAttendanceRepository.existsByRoundIdAndUserId(round.getId(), userId)) {
      throw new DiningException(ALREADY_ATTENDED_DINING_ROUND);
    }
    diningAttendanceRepository.saveAndFlush(
        DiningAttendance.builder().roundId(round.getId()).userId(userId).build());
  }

  private void deleteAttendance(DiningRound round, Long attendeeUserId, Long deletedByUserId) {
    DiningAttendance attendance =
        diningAttendanceRepository
            .findByRoundIdAndUserIdForUpdate(round.getId(), attendeeUserId)
            .orElseThrow(() -> new DiningException(NOT_FOUND_DINING_ATTENDANCE));
    diningDeletionHistoryRecorder.recordAttendanceDeletion(round, attendance, deletedByUserId);
    diningAttendanceRepository.delete(attendance);
  }

  /** 회식 메인 목록: 전체/내 회식, 진행 상태·파트 필터, 회식명 검색 정렬: 진행 중 우선 → 회식 일자 최신순 → 등록 최신순 */
  @Override
  public GetDiningsResult getDinings(GetDiningsQuery queryCommand, Long userId) {
    long totalCount = diningSearchRepository.countDinings(queryCommand, userId);
    PageMetaDto meta = new PageMetaDto(queryCommand, Math.toIntExact(totalCount));
    List<DiningMeeting> dinings =
        totalCount == 0 ? List.of() : diningSearchRepository.findDinings(queryCommand, userId);
    if (dinings.isEmpty()) {
      return new GetDiningsResult(List.of(), meta);
    }

    List<Long> meetingIds = dinings.stream().map(DiningMeeting::getId).toList();
    Map<Long, Long> attendeeCounts =
        diningAttendanceRepository.countDistinctUserIdsByMeetingIdIn(meetingIds).stream()
            .collect(
                Collectors.toMap(
                    DiningAttendeeCountProjection::getMeetingId,
                    DiningAttendeeCountProjection::getAttendeeCount));
    Map<Long, DiningUser> leaders =
        userRepository
            .findAllById(dinings.stream().map(DiningMeeting::getUserId).distinct().toList())
            .stream()
            .collect(Collectors.toMap(DiningUser::getId, Function.identity()));
    Set<Long> approvedPayerMeetingIds =
        new HashSet<>(
            diningAttendanceRepository.findMeetingIdsByUserIdAndPayerStatus(
                userId, DiningPayerStatus.APPROVED, meetingIds));

    List<DiningCard> cards =
        dinings.stream()
            .map(
                dining ->
                    new DiningCard(
                        dining.getId(),
                        dining.getTitle(),
                        dining.getmStartDate().toLocalDate(),
                        getFirstImageUrl(dining),
                        dining.isDiningEnded(),
                        dining.getJoinableParts(),
                        attendeeCounts.getOrDefault(dining.getId(), 0L),
                        toLeaderDto(leaders.get(dining.getUserId())),
                        dining.checkMeetingLeader(userId)
                            || approvedPayerMeetingIds.contains(dining.getId())))
            .toList();

    return new GetDiningsResult(cards, meta);
  }

  /** 회식 상세: 회식 정보와 회차 목록. 회차별 인원, 내 참석 상태, 결제자 상태는 참석 기록을 한 번에 읽어 계산한다. */
  @Override
  public GetDiningDetailResult getDiningDetail(Long meetingId, Long userId) {
    DiningMeeting dining = meetingRepository.findDiningByIdOrThrow(meetingId);
    DiningRounds rounds = getRounds(meetingId);
    List<DiningAttendance> attendances =
        rounds.getRoundIds().isEmpty()
            ? List.of()
            : diningAttendanceRepository.findAllByRoundIdIn(rounds.getRoundIds());

    Map<Long, Long> attendeeCounts =
        attendances.stream()
            .collect(Collectors.groupingBy(DiningAttendance::getRoundId, Collectors.counting()));
    Map<Long, DiningAttendance> myAttendances =
        attendances.stream()
            .filter(attendance -> attendance.getUserId().equals(userId))
            .collect(Collectors.toMap(DiningAttendance::getRoundId, Function.identity()));
    boolean isApprovedPayer =
        myAttendances.values().stream().anyMatch(DiningAttendance::isApprovedPayer);

    LocalDateTime now =
        java.time.LocalDateTime.now(clock.withZone(java.time.ZoneId.of("Asia/Seoul")));
    List<DiningRound> roundList = rounds.getRounds();
    List<GetDiningDetailResult.Round> roundDtos =
        IntStream.range(0, roundList.size())
            .mapToObj(index -> toRoundDto(rounds, index, attendeeCounts, myAttendances, now))
            .toList();

    return new GetDiningDetailResult(
        dining.getId(),
        dining.getTitle(),
        dining.getmStartDate().toLocalDate(),
        dining.isDateModified(),
        getFirstImageUrl(dining),
        dining.isDiningEnded(),
        dining.getJoinableParts(),
        dining.isJoinablePartsModified(),
        toLeaderDto(userRepository.findById(dining.getUserId()).orElse(null)),
        dining.checkMeetingLeader(userId),
        isApprovedPayer,
        roundDtos);
  }

  /**
   * 자동 확정: 회식 일자가 오늘 이전인 회식의 미마감 회차를 모두 마감한다. (스케줄러에서 매일 11:59 호출) 실행 시점의 회식 일자로 판단하므로 일자를 수정해도 확정
   * 시점이 따라간다.
   */
  @Override
  @Transactional
  public int closeExpiredRounds() {
    LocalDateTime now =
        java.time.LocalDateTime.now(clock.withZone(java.time.ZoneId.of("Asia/Seoul")));
    return diningRoundRepository.closeOpenRoundsOfDiningsBefore(
        DiningRoundStatus.CLOSED,
        DiningRoundStatus.OPEN,
        MeetingCategory.DINING,
        now.toLocalDate().atStartOfDay(),
        now);
  }

  private GetDiningDetailResult.Round toRoundDto(
      DiningRounds rounds,
      int index,
      Map<Long, Long> attendeeCounts,
      Map<Long, DiningAttendance> myAttendances,
      LocalDateTime now) {
    List<DiningRound> roundList = rounds.getRounds();
    DiningRound round = roundList.get(index);
    DiningAttendance myAttendance = myAttendances.get(round.getId());
    boolean isAttending = myAttendance != null;

    MyAttendanceStatus myAttendanceStatus =
        !isAttending
            ? MyAttendanceStatus.NOT_ATTENDING
            : round.isClosed() ? MyAttendanceStatus.ATTENDED : MyAttendanceStatus.ATTENDING;

    // 신규 회차 안내: 1차가 아닌 최신 회차가 마감 전이고, 이전 회차에는 참석했지만 이 회차는 미신청
    boolean isLatestRound = index == roundList.size() - 1;
    boolean attendedPreviousRound =
        roundList.subList(0, index).stream()
            .anyMatch(previous -> myAttendances.containsKey(previous.getId()));
    boolean showNewRoundGuide =
        index > 0 && isLatestRound && !round.isClosed() && !isAttending && attendedPreviousRound;

    return new GetDiningDetailResult.Round(
        round.getId(),
        index + 1,
        round.getPlace(),
        round.getNaverMapLink(),
        round.getKakaoMapLink(),
        round.getStartTime(),
        round.isClosed(),
        round.isPlaceModified(),
        round.isLinkModified(),
        round.isStartTimeModified(),
        rounds.isNewRound(round, now),
        attendeeCounts.getOrDefault(round.getId(), 0L),
        myAttendanceStatus,
        isAttending ? myAttendance.getPayerStatus() : null,
        showNewRoundGuide);
  }

  private String getFirstImageUrl(DiningMeeting dining) {
    List<MeetingImage> imageUrls = dining.getImageURL();
    return imageUrls == null || imageUrls.isEmpty() ? null : imageUrls.get(0).url();
  }

  /**
   * 개설자는 meeting."userId"(NOT NULL, user ON DELETE CASCADE)로 항상 존재한다. 조회 도중 탈퇴가 커밋된 경우에만 없으며, 이때는
   * 회식도 함께 삭제된 상태라 null 대신 예외로 끝낸다. DiningUserPort.findByIdOrThrow 는 외부 인증 서버를 호출하므로 크루 DB 조회 결과로
   * 판단한다.
   */
  private DiningLeader toLeaderDto(DiningUser leader) {
    if (leader == null) {
      throw new DiningException(NOT_FOUND_USER);
    }
    return DiningLeader.from(leader);
  }
}
