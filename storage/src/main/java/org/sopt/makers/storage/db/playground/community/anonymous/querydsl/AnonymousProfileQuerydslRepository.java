package org.sopt.makers.storage.db.playground.community.anonymous.querydsl;

import java.util.List;
import org.sopt.makers.storage.db.playground.community.anonymous.entity.AnonymousProfileEntity;

public interface AnonymousProfileQuerydslRepository {

  List<AnonymousProfileEntity> findRecentOrderByCreatedAtDesc(int limit);
}
