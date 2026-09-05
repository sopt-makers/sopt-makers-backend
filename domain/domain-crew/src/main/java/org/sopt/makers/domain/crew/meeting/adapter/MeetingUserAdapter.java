package org.sopt.makers.domain.crew.meeting.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.meeting.MeetingUser;
import org.sopt.makers.domain.crew.meeting.port.MeetingUserPort;
import org.sopt.makers.domain.user.ActivityList;
import org.sopt.makers.domain.user.Profile;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.port.CrewMeetingUserPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MeetingUserAdapter implements MeetingUserPort {

  private final CrewMeetingUserPort crewMeetingUserPort;

  @Override
  public Optional<MeetingUser> findById(Long userId) {
    return crewMeetingUserPort.findWithActivitiesById(userId).map(this::toMeetingUser);
  }

  @Override
  public List<MeetingUser> findAllById(List<Long> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return List.of();
    }
    return crewMeetingUserPort.findAllWithActivitiesByIds(userIds).stream()
        .map(this::toMeetingUser)
        .toList();
  }

  private MeetingUser toMeetingUser(User user) {
    Profile profile = user.profile();
    ActivityList activities = user.activities();
    return new MeetingUser(
        user.id(),
        profile == null ? null : profile.name(),
        profile == null ? null : profile.profileImage(),
        profile == null ? null : profile.phone(),
        activities == null ? List.of() : activities.activities());
  }
}
