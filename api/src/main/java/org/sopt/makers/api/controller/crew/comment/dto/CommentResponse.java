package org.sopt.makers.api.controller.crew.comment.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.playground.post.comment.PostComment;
import org.sopt.makers.domain.playground.post.service.PostCommentService;

public record CommentResponse(
    Long id,
    String contents,
    CommentWriterResponse user,
    LocalDateTime createdDate,
    int likeCount,
    Boolean isLiked,
    Boolean isWriter,
    int order,
    List<ReplyResponse> replies,
    Boolean isBlockedComment,
    Boolean isDeleted) {

  public static CommentResponse from(PostCommentService.CommentView view) {
    PostCommentService.CommentItem item = view.parent();
    PostComment comment = item.comment();
    return new CommentResponse(
        comment.id(),
        comment.contents(),
        CommentWriterResponse.from(item.writer()),
        comment.createdAt(),
        comment.likeCount(),
        item.liked(),
        item.writerOwned(),
        comment.order(),
        view.replies().stream().map(ReplyResponse::from).toList(),
        item.blocked(),
        comment.isDeleted());
  }
}
