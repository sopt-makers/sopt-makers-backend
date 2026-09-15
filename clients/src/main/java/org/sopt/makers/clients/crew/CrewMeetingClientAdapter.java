package org.sopt.makers.clients.crew;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.clients.crew.dto.CrewMeetingPageMetaHttpResponse;
import org.sopt.makers.clients.crew.dto.CrewMeetingPostHttpDto;
import org.sopt.makers.clients.crew.dto.CrewMeetingPostHttpResponse;
import org.sopt.makers.domain.playground.community.post.crew.CrewMeetingPost;
import org.sopt.makers.domain.playground.community.post.crew.port.CrewMeetingClientPort;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrewMeetingClientAdapter implements CrewMeetingClientPort {

  private final RestTemplate restTemplate;
  private final CrewMeetingClientProperty crewMeetingClientProperty;

  @Override
  public CrewMeetingPage fetchPosts(Long userId, int page, int take) {
    try {
      String url =
          UriComponentsBuilder.fromUriString(crewMeetingClientProperty.url())
              .path("/internal/post/{orgId}")
              .queryParam("page", page)
              .queryParam("take", take)
              .buildAndExpand(userId)
              .toUriString();

      CrewMeetingPostHttpResponse response =
          restTemplate.getForObject(url, CrewMeetingPostHttpResponse.class);

      if (response == null || response.posts() == null) {
        return CrewMeetingPage.failed();
      }

      List<CrewMeetingPost> posts = response.posts().stream().map(this::toDomain).toList();
      boolean hasNextPage = hasNextPage(response.pageMeta());

      return new CrewMeetingPage(posts, hasNextPage, false);
    } catch (RestClientException e) {
      log.warn("Crew 모임 게시글 조회 실패. userId: {}, page: {}", userId, page, e);
      return CrewMeetingPage.failed();
    }
  }

  private boolean hasNextPage(CrewMeetingPageMetaHttpResponse pageMeta) {
    return pageMeta != null && Boolean.TRUE.equals(pageMeta.hasNextPage());
  }

  private CrewMeetingPost toDomain(CrewMeetingPostHttpDto dto) {
    CrewMeetingPostHttpDto.CrewMeetingUserHttpDto user = dto.user();
    CrewMeetingPostHttpDto.CrewMeetingPartInfoHttpDto partInfo = user.partInfo();

    return new CrewMeetingPost(
        dto.id(),
        dto.title(),
        dto.contents(),
        dto.createdDate(),
        dto.images() == null ? List.of() : dto.images(),
        user.id(),
        user.orgId(),
        user.name(),
        user.profileImage(),
        partInfo.part(),
        partInfo.generation(),
        dto.likeCount(),
        dto.isLiked(),
        dto.viewCount(),
        dto.commentCount(),
        dto.meetingId());
  }
}
