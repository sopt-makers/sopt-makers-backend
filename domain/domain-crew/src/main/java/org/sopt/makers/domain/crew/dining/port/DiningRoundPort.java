package org.sopt.makers.domain.crew.dining.port;

import java.time.LocalDateTime;
import java.util.*;
import org.sopt.makers.core.pagination.*;
import org.sopt.makers.domain.crew.dining.exception.*;
import org.sopt.makers.domain.crew.dining.model.*;
import org.sopt.makers.domain.crew.dining.model.enums.*;
import org.sopt.makers.domain.crew.dining.model.projection.*;
import org.sopt.makers.domain.crew.meeting.MeetingCategory;

public interface DiningRoundPort {
  List<DiningRound> findAllByMeetingIdOrderByIdAsc(Long meetingId);

  List<DiningRound> findAllByMeetingIdInOrderByIdAsc(List<Long> meetingIds);

  List<DiningRound> findAllByMeetingIdForUpdate(Long meetingId);

  int closeOpenRoundsOfDiningsBefore(
      DiningRoundStatus closed,
      DiningRoundStatus open,
      MeetingCategory category,
      LocalDateTime today,
      LocalDateTime now);

  DiningRound save(DiningRound value);

  void delete(DiningRound value);

  void deleteAllInBatch(List<DiningRound> values);

  Optional<DiningRound> findById(Long id);

  default DiningRound findByIdOrThrow(Long id) {
    return findById(id)
        .orElseThrow(() -> new DiningException(DiningFailure.NOT_FOUND_DINING_ROUND));
  }
}
