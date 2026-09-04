package org.sopt.makers.storage.redis.playground.cache;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.playground.community.post.crew.CrewMeetingPost;

public record CachedCrewMeetingPost(
    Long id,
    String title,
    String content,
    LocalDateTime createdAt,
    List<String> images,
    Long writerId,
    Long writerOrgId,
    String writerName,
    String writerProfileImage,
    String writerPart,
    int writerGeneration,
    int likeCount,
    boolean isLiked,
    int viewCount,
    int commentCount,
    Long meetingId) {

  public static CachedCrewMeetingPost from(CrewMeetingPost post) {
    return new CachedCrewMeetingPost(
        post.id(),
        post.title(),
        post.content(),
        post.createdAt(),
        post.images() == null ? List.of() : post.images(),
        post.writerId(),
        post.writerOrgId(),
        post.writerName(),
        post.writerProfileImage(),
        post.writerPart(),
        post.writerGeneration(),
        post.likeCount(),
        post.isLiked(),
        post.viewCount(),
        post.commentCount(),
        post.meetingId());
  }

  public CrewMeetingPost toDomain() {
    return new CrewMeetingPost(
        id,
        title,
        content,
        createdAt,
        images == null ? List.of() : images,
        writerId,
        writerOrgId,
        writerName,
        writerProfileImage,
        writerPart,
        writerGeneration,
        likeCount,
        isLiked,
        viewCount,
        commentCount,
        meetingId);
  }
}
