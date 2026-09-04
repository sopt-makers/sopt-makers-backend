package org.sopt.makers.domain.playground.community.post.crew.port;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.playground.community.post.crew.CrewMeetingPost;

/**
 * 커뮤니티 자유 피드에 병합할 Crew 모임 게시글을 캐시 우선으로 조회하는 Port. 외부 호출은 {@link CrewMeetingClientPort}로 격리하고, 이
 * Port의 구현체가 캐싱 전략(Redis)을 책임진다.
 */
public interface CrewMeetingPostPort {

  /** 무한스크롤 피드 병합용. 최소 {@code minimumRequiredCount}건을 확보할 때까지 필요 시 추가 페이지를 가져온다. */
  CrewMeetingFeedPage getFeed(Long userId, LocalDateTime snapshotTime, int minimumRequiredCount);

  /** 홈 미리보기(prefetch 없이 이미 확보된 캐시만 사용)용. */
  CrewMeetingFeedPage getPreview(Long userId, LocalDateTime snapshotTime, int minimumRequiredCount);

  /** 인기글 미리보기용. {@code since} 시점 이전 글에 도달할 때까지 최대 {@code maxPageCount} 페이지를 추가 조회한다. */
  CrewMeetingFeedPage getPopularPreview(
      Long userId, LocalDateTime snapshotTime, LocalDateTime since, int maxPageCount);

  record CrewMeetingFeedPage(List<CrewMeetingPost> posts, boolean hasMorePage) {

    public static CrewMeetingFeedPage empty() {
      return new CrewMeetingFeedPage(List.of(), true);
    }

    public List<CrewMeetingPost> safePosts() {
      return posts == null ? List.of() : posts;
    }
  }
}
