package org.sopt.makers.domain.crew.meeting;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record MeetingApplies(Map<Long, List<MeetingApply>> appliesMap) {

  public MeetingApplies {
    appliesMap =
        appliesMap == null
            ? Map.of()
            : appliesMap.entrySet().stream()
                .collect(
                    Collectors.toUnmodifiableMap(
                        Map.Entry::getKey, entry -> List.copyOf(entry.getValue())));
  }

  public MeetingApplies(List<MeetingApply> applies) {
    this(
        (applies == null ? List.<MeetingApply>of() : applies)
            .stream().collect(Collectors.groupingBy(MeetingApply::meetingId)));
  }

  public long getAppliedCount(Long meetingId) {
    return appliesMap.getOrDefault(meetingId, List.of()).size();
  }

  public long getApprovedCount(Long meetingId) {
    return appliesMap.getOrDefault(meetingId, List.of()).stream()
        .filter(MeetingApply::isApproved)
        .count();
  }

  public boolean isApplied(Long meetingId, Long userId) {
    return appliesMap.getOrDefault(meetingId, List.of()).stream()
        .anyMatch(apply -> apply.userId().equals(userId));
  }

  public boolean isApproved(Long meetingId, Long userId) {
    return appliesMap.getOrDefault(meetingId, List.of()).stream()
        .anyMatch(apply -> apply.userId().equals(userId) && apply.isApproved());
  }
}
