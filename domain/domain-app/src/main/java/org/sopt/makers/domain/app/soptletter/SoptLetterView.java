package org.sopt.makers.domain.app.soptletter;

public record SoptLetterView(
    SoptLetter letter, String authorNickname, boolean likedByMe, boolean mine) {}
