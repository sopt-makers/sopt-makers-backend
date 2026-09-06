package org.sopt.makers.domain.playground.post;

import java.util.List;

public record PostNotification(
    List<Long> userIds, String title, String content, Long postId, Long meetingId) {

  public PostNotification {
    userIds = userIds == null ? List.of() : List.copyOf(userIds);
  }
}
