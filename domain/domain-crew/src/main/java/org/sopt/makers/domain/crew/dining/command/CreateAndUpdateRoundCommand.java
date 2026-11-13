package org.sopt.makers.domain.crew.dining.command;

public record CreateAndUpdateRoundCommand(
    String place, String naverMapLink, String kakaoMapLink, String startTime) {}
