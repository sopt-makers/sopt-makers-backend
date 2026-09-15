package org.sopt.makers.domain.app.poke;

import java.util.List;
import org.sopt.makers.domain.user.PokeUserProfile;

public record RecommendedFriendsByType(
    FriendRecommendType randomType, String randomTitle, List<PokeUserProfile> userInfoList) {}
