package org.sopt.makers.storage.redis.playground.cache;

import java.util.List;

public record CachedCrewMeetingFeed(List<CachedCrewMeetingPost> posts, boolean hasMorePage) {

  public static CachedCrewMeetingFeed empty() {
    return new CachedCrewMeetingFeed(List.of(), true);
  }

  public List<CachedCrewMeetingPost> safePosts() {
    return posts == null ? List.of() : posts;
  }
}
