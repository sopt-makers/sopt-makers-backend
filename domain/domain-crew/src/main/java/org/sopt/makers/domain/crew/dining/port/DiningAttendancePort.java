package org.sopt.makers.domain.crew.dining.port;

import java.util.*;
import org.sopt.makers.core.pagination.*;
import org.sopt.makers.domain.crew.dining.exception.*;
import org.sopt.makers.domain.crew.dining.model.*;
import org.sopt.makers.domain.crew.dining.model.enums.*;
import org.sopt.makers.domain.crew.dining.model.projection.*;

public interface DiningAttendancePort {
  boolean existsByRoundIdAndUserId(Long roundId, Long userId);

  Optional<DiningAttendance> findByRoundIdAndUserId(Long roundId, Long userId);

  Optional<DiningAttendance> findByRoundIdAndUserIdForUpdate(Long roundId, Long userId);

  List<DiningAttendance> findAllByRoundIdInForUpdate(List<Long> roundIds);

  List<DiningAttendance> findAllByRoundIdInAndPayerStatusOrderByPayerRequestedAtAscIdAsc(
      List<Long> roundIds, DiningPayerStatus payerStatus);

  List<DiningAttendance> findAllByRoundIdIn(List<Long> roundIds);

  List<DiningAttendance> findAllByRoundId(Long roundId);

  List<DiningAttendance> findAllByRoundIdOrderByCreatedTimestampAscIdAsc(Long roundId);

  PageResult<DiningAttendance> findAllByRoundId(Long roundId, PageQuery page, boolean ascending);

  List<Long> findDistinctUserIdsByRoundIdIn(List<Long> roundIds);

  long countByRoundId(Long roundId);

  boolean existsByRoundIdAndUserIdAndPayerStatus(
      Long roundId, Long userId, DiningPayerStatus payerStatus);

  List<Long> findMeetingIdsByUserIdAndPayerStatus(
      Long userId, DiningPayerStatus payerStatus, List<Long> meetingIds);

  long countDistinctUserIdsByMeetingId(Long meetingId);

  List<DiningAttendeeCountProjection> countDistinctUserIdsByMeetingIdIn(List<Long> meetingIds);

  DiningAttendance save(DiningAttendance value);

  void delete(DiningAttendance value);

  void deleteAllInBatch(List<DiningAttendance> values);

  DiningAttendance saveAndFlush(DiningAttendance value);
}
