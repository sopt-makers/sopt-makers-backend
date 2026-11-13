package org.sopt.makers.domain.crew.dining.port;

import java.time.LocalDateTime;
import java.util.*;
import org.sopt.makers.domain.crew.dining.exception.*;
import org.sopt.makers.domain.crew.dining.model.DiningMeeting;
import org.sopt.makers.domain.crew.meeting.MeetingCategory;

public interface DiningMeetingPort {
  DiningMeeting save(DiningMeeting value);

  Optional<DiningMeeting> findById(Long id);

  Optional<DiningMeeting> findByIdForUpdate(Long id);

  default DiningMeeting findDiningByIdOrThrow(Long id) {
    return findById(id).orElseThrow(() -> new DiningException(DiningFailure.NOT_FOUND_DINING));
  }

  default DiningMeeting findDiningByIdForUpdateOrThrow(Long id) {
    return findByIdForUpdate(id)
        .orElseThrow(() -> new DiningException(DiningFailure.NOT_FOUND_DINING));
  }

  List<DiningMeeting> findOngoingDiningsByDateBetween(
      MeetingCategory category, LocalDateTime start, LocalDateTime end);

  void delete(DiningMeeting value);
}
