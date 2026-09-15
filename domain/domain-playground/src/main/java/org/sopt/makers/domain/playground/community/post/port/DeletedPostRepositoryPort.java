package org.sopt.makers.domain.playground.community.post.port;

import org.sopt.makers.domain.playground.community.post.DeletedPost;

public interface DeletedPostRepositoryPort {

  DeletedPost save(DeletedPost deletedPost);
}
