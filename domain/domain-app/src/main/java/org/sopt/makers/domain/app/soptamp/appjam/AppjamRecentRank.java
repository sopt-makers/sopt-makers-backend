package org.sopt.makers.domain.app.soptamp.appjam;

import java.time.LocalDateTime;

public record AppjamRecentRank(
    Long stampId,
    Long missionId,
    Long userId,
    String imageUrl,
    LocalDateTime createdAt,
    String ownerNickname,
    String userName,
    String userProfileImage,
    String teamName,
    TeamNumber teamNumber) {}
