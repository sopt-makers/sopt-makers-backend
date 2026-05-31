package org.sopt.makers.storage.redis.user.cache;

public record CachedUserActivity(
    long activityId, int generation, String part, String team, String role, boolean isSopt) {}
