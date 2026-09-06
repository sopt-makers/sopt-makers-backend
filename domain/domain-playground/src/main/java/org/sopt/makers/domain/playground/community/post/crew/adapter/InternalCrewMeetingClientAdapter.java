package org.sopt.makers.domain.playground.community.post.crew.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.playground.community.post.crew.CrewMeetingPost;
import org.sopt.makers.domain.playground.community.post.crew.port.CrewMeetingClientPort;
import org.sopt.makers.domain.playground.post.Post;
import org.sopt.makers.domain.playground.post.PostWriter;
import org.sopt.makers.domain.playground.post.port.PlaygroundPostQueryPort;
import org.sopt.makers.domain.playground.post.port.PlaygroundPostQueryPort.PostInfo;
import org.sopt.makers.domain.playground.post.service.PostService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * 해당 모임 게시글 조회 기능이 이 백엔드의 domain-playground 자체({@link PostService}/
 * {@link PlaygroundPostQueryPort})로 이관되면서, 커뮤니티 자유피드 병합 경로는 같은 프로세스 내 호출로 대체한다. 두
 * Port 모두 domain-playground 소속이라 모듈 경계를 넘지 않는다. 레거시 HTTP 기반
 * {@code clients.crew.CrewMeetingClientAdapter}는 다른 소비자가 있을 수 있어 그대로 남겨두되, 이 어댑터를
 * {@code @Primary}로 지정해 실제 호출 경로는 내부 Port를 타도록 한다.
 */
@Primary
@Component
@RequiredArgsConstructor
public class InternalCrewMeetingClientAdapter implements CrewMeetingClientPort {

  private final PlaygroundPostQueryPort playgroundPostQueryPort;

  @Override
  public CrewMeetingPage fetchPosts(Long userId, int page, int take) {
    PageResult<PostInfo> result = playgroundPostQueryPort.findPosts(userId, page, take);
    List<CrewMeetingPost> posts = result.content().stream().map(this::toDomain).toList();
    return new CrewMeetingPage(posts, result.hasNext(), false);
  }

  private CrewMeetingPost toDomain(PostInfo info) {
    Post post = info.post();
    PostWriter writer = info.writer();

    return new CrewMeetingPost(
        post.id(),
        post.title(),
        post.contents(),
        post.createdAt(),
        post.images(),
        writer == null ? null : writer.id(),
        writer == null ? null : writer.id(),
        writer == null ? null : writer.name(),
        writer == null ? null : writer.profileImage(),
        writer == null ? null : writer.part(),
        writer == null || writer.generation() == null ? 0 : writer.generation(),
        post.likeCount(),
        info.liked(),
        post.viewCount(),
        post.commentCount(),
        post.meetingId());
  }
}
