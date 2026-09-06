package org.sopt.makers.api.controller.crew.comment.dto;

import java.time.LocalDateTime;
import org.sopt.makers.domain.playground.post.comment.PostComment;
import org.sopt.makers.domain.playground.post.service.PostCommentService;

public record ReplyResponse(
    Long id,
    String contents,
    CommentWriterResponse user,
    LocalDateTime createdDate,
    int likeCount,
    Boolean isLiked,
    Boolean isWriter,
    int order,
    Boolean isBlockedComment,
    Boolean isDeleted) {

  public static ReplyResponse from(PostCommentService.CommentItem item) {
    PostComment comment = item.comment();
    return new ReplyResponse(
        comment.id(),
        comment.contents(),
        CommentWriterResponse.from(item.writer()),
        comment.createdAt(),
        comment.likeCount(),
        item.liked(),
        item.writerOwned(),
        comment.order(),
        item.blocked(),
        comment.isDeleted());
  }
}
