package org.sopt.makers.api.controller.app.home.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.makers.domain.app.playground.PlaygroundPopularPost;

public record PlaygroundPopularPostsResponse(
    @Schema(description = "플레이그라운드 인기 게시글 목록. 캐시가 비어 있으면 빈 배열") List<PopularPost> popularPosts) {

  public static PlaygroundPopularPostsResponse of(List<PlaygroundPopularPost> posts) {
    return new PlaygroundPopularPostsResponse(posts.stream().map(PopularPost::of).toList());
  }

  public record PopularPost(
      @Schema(description = "게시글 아이디", example = "1") Long id,
      @Schema(description = "작성자 아이디", example = "1") Long userId,
      @Schema(description = "작성자 프로필 이미지 주소", example = "https://s3.sopt.org/profile.png")
          String profileImage,
      @Schema(description = "작성자 이름", example = "김앱짱") String name,
      @Schema(description = "작성자 기수와 파트", example = "35기 서버") String generationAndPart,
      @Schema(description = "인기 순위. 1부터 시작", example = "1") int rank,
      @Schema(description = "게시글 분류", example = "자유") String category,
      @Schema(description = "게시글 제목", example = "오늘 세미나 어땠나요") String title,
      @Schema(description = "게시글 본문", example = "다들 고생 많으셨습니다") String content,
      @Schema(description = "플레이그라운드에서 열 주소", example = "https://playground.sopt.org/feed/1")
          String webLink) {

    private static PopularPost of(PlaygroundPopularPost post) {
      return new PopularPost(
          post.id(),
          post.userId(),
          post.profileImage(),
          post.name(),
          post.generationAndPart(),
          post.rank(),
          post.category(),
          post.title(),
          post.content(),
          post.webLink());
    }
  }
}
