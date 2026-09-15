package org.sopt.makers.domain.app.soptamp.appjam;

public record AppjamTodayTeamRank(
    int rank, TeamNumber teamNumber, String teamName, long todayPoints, long totalPoints) {}
