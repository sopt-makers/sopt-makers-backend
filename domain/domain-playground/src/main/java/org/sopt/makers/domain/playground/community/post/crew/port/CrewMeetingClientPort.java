package org.sopt.makers.domain.playground.community.post.crew.port;

import java.util.List;
import org.sopt.makers.domain.playground.community.post.crew.CrewMeetingPost;

/** 외부 Crew 서비스로부터 모임 게시글 한 페이지를 조회하는 Port. 캐싱 전략은 {@link CrewMeetingPostPort}가 담당한다. */
public interface CrewMeetingClientPort {

  CrewMeetingPage fetchPosts(Long userId, int page, int take);

  record CrewMeetingPage(List<CrewMeetingPost> posts, boolean hasNextPage, boolean fetchFailed) {

    public static CrewMeetingPage failed() {
      return new CrewMeetingPage(List.of(), false, true);
    }
  }
}
