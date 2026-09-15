package org.sopt.makers.domain.playground.community.comment.service;

import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousProfile;
import org.sopt.makers.domain.playground.community.anonymous.service.AnonymousProfileRetriever;
import org.sopt.makers.domain.playground.community.comment.Comment;
import org.sopt.makers.domain.playground.community.comment.port.CommentRepositoryPort;
import org.springframework.stereotype.Component;

/** 댓글 삭제 시 해당 댓글을 멘션한 답글들의 멘션 표기를 익명화(@_)한다. */
@Component
@RequiredArgsConstructor
public class CommentMentionAnonymizer {

  private final CommentRepositoryPort commentRepositoryPort;
  private final AnonymousProfileRetriever anonymousProfileRetriever;

  public void anonymizeMentionsInReplies(Comment deletedComment) {
    List<Comment> replies = commentRepositoryPort.findAllByParentCommentId(deletedComment.id());

    if (replies.isEmpty()) {
      return;
    }

    AnonymousProfile deletedAnonymousProfile =
        deletedComment.anonymousProfileId() == null
            ? null
            : anonymousProfileRetriever.findById(deletedComment.anonymousProfileId()).orElse(null);

    for (Comment reply : replies) {
      String updatedContent =
          anonymizeMentionInContent(
              reply.content(), deletedComment.writerId(), deletedAnonymousProfile);

      if (!updatedContent.equals(reply.content())) {
        commentRepositoryPort.save(reply.withContent(updatedContent));
      }
    }
  }

  private String anonymizeMentionInContent(
      String content, Long deletedUserId, AnonymousProfile deletedAnonymousProfile) {
    String result = content;

    // 실명 사용자 언급 익명화: @이름[userId] -> @_
    String realNamePattern = "@.*?\\[" + deletedUserId + "\\]";
    result = result.replaceAll(realNamePattern, "@_");

    // 익명 사용자 언급 익명화: @익명닉네임[-1] -> @_
    if (deletedAnonymousProfile != null && deletedAnonymousProfile.nickname() != null) {
      String anonymousNickname = deletedAnonymousProfile.nickname().nickname();
      String anonymousPattern = "@" + Pattern.quote(anonymousNickname) + "\\[-1\\]";
      result = result.replaceAll(anonymousPattern, "@_");
    }

    return result;
  }
}
