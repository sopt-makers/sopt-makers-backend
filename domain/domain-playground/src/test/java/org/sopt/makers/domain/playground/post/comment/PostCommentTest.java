package org.sopt.makers.domain.playground.post.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.sopt.makers.domain.playground.post.exception.PostFailure.DELETED_COMMENT;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.playground.post.exception.PostException;

class PostCommentTest {

  @Test
  @DisplayName("댓글 삭제 시 내용과 받은 좋아요를 유지하는 soft delete를 수행한다")
  void softDeletesComment() {
    PostComment comment = new PostComment(1L, 10L, 20L, "내용", null, 0, 0, 3, false, null, null);

    PostComment deleted = comment.markDeleted();

    assertThat(deleted.isDeleted()).isTrue();
    assertThat(deleted.contents()).isEqualTo("삭제된 댓글입니다.");
    assertThat(deleted.writerId()).isNull();
    assertThat(deleted.likeCount()).isEqualTo(3);
  }

  @Test
  @DisplayName("삭제된 댓글은 수정할 수 없다")
  void cannotUpdateDeletedComment() {
    PostComment deleted = PostComment.createParent(10L, 20L, "내용").markDeleted();

    assertThatThrownBy(() -> deleted.update("수정"))
        .isInstanceOf(PostException.class)
        .extracting("error")
        .isEqualTo(DELETED_COMMENT);
  }
}
