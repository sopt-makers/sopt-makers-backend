package org.sopt.makers.storage.db.app.soptletter.querydsl;

import java.util.List;

public interface SoptLetterQuerydslRepository {

  List<TopicUnreadCount> countUnreadByTopic(Long userId, Long excludedAuthorProfileId);
}
