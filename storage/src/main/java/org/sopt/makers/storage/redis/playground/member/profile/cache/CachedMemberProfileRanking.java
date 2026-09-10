package org.sopt.makers.storage.redis.playground.member.profile.cache;

import java.util.List;

public record CachedMemberProfileRanking(List<Long> topUserIds, int totalCount) {}
