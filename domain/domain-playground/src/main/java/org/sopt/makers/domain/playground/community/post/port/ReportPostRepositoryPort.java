package org.sopt.makers.domain.playground.community.post.port;

import org.sopt.makers.domain.playground.community.post.ReportPost;

public interface ReportPostRepositoryPort {

  ReportPost save(ReportPost reportPost);

  void deleteAllByPostId(Long postId);
}
