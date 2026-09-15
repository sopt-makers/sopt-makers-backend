package org.sopt.makers.domain.playground.community.comment.port;

import java.util.List;
import org.sopt.makers.domain.playground.community.comment.DeletedComment;

public interface DeletedCommentRepositoryPort {

  DeletedComment save(DeletedComment deletedComment);

  List<DeletedComment> saveAll(List<DeletedComment> deletedComments);
}
