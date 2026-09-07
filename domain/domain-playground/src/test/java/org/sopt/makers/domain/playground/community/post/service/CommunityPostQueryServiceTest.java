package org.sopt.makers.domain.playground.community.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.sopt.makers.domain.playground.community.anonymous.service.AnonymousProfileRetriever;
import org.sopt.makers.domain.playground.community.comment.service.CommentQueryService;
import org.sopt.makers.domain.playground.community.member.port.CommunityMemberPort;
import org.sopt.makers.domain.playground.community.member.service.CommunityMemberAssembler;
import org.sopt.makers.domain.playground.community.post.Post;
import org.sopt.makers.domain.playground.community.post.PostFeedResult;
import org.sopt.makers.domain.playground.community.post.crew.port.CrewMeetingPostPort;
import org.sopt.makers.domain.playground.community.post.port.PostLikeRepositoryPort;
import org.sopt.makers.domain.playground.community.post.port.PostRepositoryPort;
import org.sopt.makers.domain.playground.community.service.CategoryQueryService;
import org.sopt.makers.domain.playground.community.service.CommunityCategoryPolicy;
import org.sopt.makers.domain.playground.community.utils.CommunityPostWebLinkBuilder;
import org.sopt.makers.domain.playground.community.vote.service.VoteQueryService;
import org.sopt.makers.domain.playground.member.relation.port.UserBlockRepositoryPort;

/**
 * 1-B 회귀 검증: 차단 작성자 필터링이 페이지네이션 쿼리(NOT IN) 단계에서 이미 끝나 있다는 전제 하에, 서비스가 결과를 추가로 솎아내
 * limit/hasNext를 왜곡시키지 않는지 확인한다.
 */
class CommunityPostQueryServiceTest {

  private static final Long USER_ID = 1L;
  private static final Long BLOCKED_WRITER_ID = 999L;

  private final PostRepositoryPort postRepositoryPort = mock(PostRepositoryPort.class);
  private final PostLikeRepositoryPort postLikeRepositoryPort = mock(PostLikeRepositoryPort.class);
  private final CategoryQueryService categoryQueryService = mock(CategoryQueryService.class);
  private final CommunityCategoryPolicy communityCategoryPolicy = new CommunityCategoryPolicy();
  private final CommunityMemberAssembler communityMemberAssembler = mock(CommunityMemberAssembler.class);
  private final AnonymousProfileRetriever anonymousProfileRetriever = mock(AnonymousProfileRetriever.class);
  private final CrewMeetingPostPort crewMeetingPostPort = mock(CrewMeetingPostPort.class);
  private final CommunityFeedCursorCodec communityFeedCursorCodec = new CommunityFeedCursorCodec();
  private final CommentQueryService commentQueryService = mock(CommentQueryService.class);
  private final VoteQueryService voteQueryService = mock(VoteQueryService.class);
  private final UserBlockRepositoryPort userBlockRepositoryPort = mock(UserBlockRepositoryPort.class);
  private final CommunityMemberPort communityMemberPort = mock(CommunityMemberPort.class);
  private final CommunityPostWebLinkBuilder communityPostWebLinkBuilder = mock(CommunityPostWebLinkBuilder.class);

  private final CommunityPostQueryService service =
      new CommunityPostQueryService(
          postRepositoryPort,
          postLikeRepositoryPort,
          categoryQueryService,
          communityCategoryPolicy,
          communityMemberAssembler,
          anonymousProfileRetriever,
          crewMeetingPostPort,
          communityFeedCursorCodec,
          commentQueryService,
          voteQueryService,
          userBlockRepositoryPort,
          communityMemberPort,
          communityPostWebLinkBuilder);

  @Test
  @DisplayName("차단 작성자 ID가 쿼리에 그대로 전달되고, 쿼리가 이미 가득 채워 반환하면 사후 필터링 없이 요청한 limit만큼 그대로 반환한다")
  void forwardsExcludedWriterIdsAndReturnsFullPageWithoutPostHocFiltering() {
    int limit = 3;
    Set<Long> blockedIds = Set.of(BLOCKED_WRITER_ID);
    when(userBlockRepositoryPort.findBlockedUserIdsInvolving(USER_ID)).thenReturn(blockedIds);

    List<Post> fullyFilledPage = List.of(post(1L, 10L), post(2L, 11L), post(3L, 12L));
    when(postRepositoryPort.findByCategoryCodesWithCursor(
            any(), any(), any(), any(), eq(limit + 1), eq(blockedIds)))
        .thenReturn(fullyFilledPage);

    PostFeedResult result =
        service.getPosts(USER_ID, CommunityCategoryCode.PROMOTION, null, null, true, limit, null);

    ArgumentCaptor<Set<Long>> excludedCaptor = ArgumentCaptor.forClass(Set.class);
    verify(postRepositoryPort)
        .findByCategoryCodesWithCursor(any(), any(), any(), any(), eq(limit + 1), excludedCaptor.capture());
    assertThat(excludedCaptor.getValue()).isEqualTo(blockedIds);

    assertThat(result.items()).hasSize(limit);
    assertThat(result.hasNext()).isFalse();
  }

  @Test
  @DisplayName("쿼리가 limit+1건을 반환하면 hasNext가 true이고 결과는 limit개로 정확히 슬라이스된다")
  void slicesToLimitAndSetsHasNextWhenQueryReturnsOneExtraRow() {
    int limit = 3;
    when(userBlockRepositoryPort.findBlockedUserIdsInvolving(USER_ID)).thenReturn(Set.of());

    List<Post> pageWithExtraRow =
        List.of(post(1L, 10L), post(2L, 11L), post(3L, 12L), post(4L, 13L));
    when(postRepositoryPort.findByCategoryCodesWithCursor(any(), any(), any(), any(), anyInt(), any()))
        .thenReturn(pageWithExtraRow);

    PostFeedResult result =
        service.getPosts(USER_ID, CommunityCategoryCode.PROMOTION, null, null, true, limit, null);

    assertThat(result.items()).hasSize(limit);
    assertThat(result.hasNext()).isTrue();
  }

  private Post post(Long id, Long writerId) {
    return new Post(
        id,
        writerId,
        100L,
        "title-" + id,
        "content-" + id,
        0,
        List.of(),
        false,
        false,
        false,
        false,
        null,
        null,
        LocalDateTime.now().minusMinutes(id),
        null);
  }
}
