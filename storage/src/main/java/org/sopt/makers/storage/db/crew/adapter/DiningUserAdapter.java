package org.sopt.makers.storage.db.crew.adapter;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.dining.model.DiningUser;
import org.sopt.makers.domain.crew.dining.port.DiningUserPort;
import org.sopt.makers.domain.crew.meeting.MeetingUser;
import org.sopt.makers.domain.crew.meeting.port.MeetingUserPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiningUserAdapter implements DiningUserPort {
  private final MeetingUserPort users;

  public Optional<DiningUser> findById(Long id) {
    return users.findById(id).map(this::map);
  }

  public List<DiningUser> findAllById(List<Long> ids) {
    return users.findAllById(ids).stream().map(this::map).toList();
  }

  private DiningUser map(MeetingUser user) {
    return new DiningUser(
        user.id(),
        user.name(),
        user.profileImage(),
        user.phone(),
        user.findLatestActivity().map(a -> a.generation()).orElse(null));
  }
}
