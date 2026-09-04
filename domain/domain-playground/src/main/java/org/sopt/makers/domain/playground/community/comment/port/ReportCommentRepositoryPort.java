package org.sopt.makers.domain.playground.community.comment.port;

import java.util.List;
import org.sopt.makers.domain.playground.community.comment.ReportComment;

public interface ReportCommentRepositoryPort {

  ReportComment save(ReportComment reportComment);

  void deleteAllByCommentIds(List<Long> commentIds);
}
