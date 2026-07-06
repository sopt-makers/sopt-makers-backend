package org.sopt.makers.domain.crew.meeting.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.crew.meeting.Meeting;

public interface MeetingRepositoryPort {

  Meeting save(Meeting meeting);

  Optional<Meeting> findById(Long meetingId);

  List<Meeting> findAll();

  List<Meeting> findAllByUserId(Long userId);

  void delete(Meeting meeting);
}
