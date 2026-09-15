package org.sopt.makers.domain.app.soptamp.clap;

public record ClapUserProfile(
    String nickname, String profileImageUrl, String profileMessage, int clapCount) {}
