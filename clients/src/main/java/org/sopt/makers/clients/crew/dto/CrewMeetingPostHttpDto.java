package org.sopt.makers.clients.crew.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CrewMeetingPostHttpDto(
    long id,
    String title,
    String contents,
    LocalDateTime createdDate,
    List<String> images,
    CrewMeetingUserHttpDto user,
    int likeCount,
    boolean isLiked,
    int viewCount,
    int commentCount,
    Long meetingId) {

  public record CrewMeetingUserHttpDto(
      long id, long orgId, String name, String profileImage, CrewMeetingPartInfoHttpDto partInfo) {}

  public record CrewMeetingPartInfoHttpDto(String part, int generation) {}
}
