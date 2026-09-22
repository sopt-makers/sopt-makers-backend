package org.sopt.makers.storage.db.app.home.adapter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.playground.PlaygroundPopularPost;
import org.sopt.makers.domain.app.playground.PlaygroundRecentPost;
import org.sopt.makers.domain.app.playground.port.AppHomePlaygroundPostQueryPort;
import org.sopt.makers.domain.playground.community.post.service.CommunityPostQueryService;
import org.sopt.makers.domain.playground.community.post.service.CommunityPostQueryService.InternalPopularPostSummary;
import org.sopt.makers.domain.playground.community.post.service.CommunityPostQueryService.InternalPostSummary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * domain-app(home)과 domain-playground(community post) 간 모듈 경계를 이어주는 브릿지 어댑터.
 *
 * <p>플레이그라운드가 이 레포로 이관되기 전에는 InternalOpenApiController(GET
 * /internal/api/v1/community/posts/{latest,popular})를 헤더 없이 호출하는 별도 HTTP 어댑터였다. 이관 완료 후에는 그 컨트롤러가
 * 호출하는 것과 같은 CommunityPostQueryService를 같은 프로세스에서 직접 호출해, 자기 자신에게 보내던 HTTP 왕복을 없앤다.
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppHomePlaygroundPostQueryAdapter implements AppHomePlaygroundPostQueryPort {

  private static final DateTimeFormatter CREATED_AT_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
  private static final int POPULAR_POST_LIMIT = 3;

  private final CommunityPostQueryService communityPostQueryService;

  @Override
  public List<PlaygroundRecentPost> getPlaygroundRecentPosts() {
    return communityPostQueryService.getInternalLatestPosts().stream()
        .map(this::toRecentPost)
        .toList();
  }

  @Override
  public List<PlaygroundPopularPost> getPlaygroundPopularPosts() {
    return communityPostQueryService.getInternalPopularPosts(POPULAR_POST_LIMIT).stream()
        .map(this::toPopularPost)
        .toList();
  }

  private PlaygroundRecentPost toRecentPost(InternalPostSummary summary) {
    return new PlaygroundRecentPost(
        summary.id(),
        summary.userId(),
        summary.profileImage(),
        summary.name(),
        summary.generationAndPart(),
        summary.categoryName(),
        summary.title(),
        summary.content(),
        summary.webLink(),
        summary.createdAt().format(CREATED_AT_FORMATTER),
        false);
  }

  private PlaygroundPopularPost toPopularPost(InternalPopularPostSummary summary) {
    return new PlaygroundPopularPost(
        summary.id(),
        summary.userId(),
        summary.profileImage(),
        summary.name(),
        summary.generationAndPart(),
        summary.rank(),
        summary.categoryName(),
        summary.title(),
        summary.content(),
        summary.webLink());
  }
}
