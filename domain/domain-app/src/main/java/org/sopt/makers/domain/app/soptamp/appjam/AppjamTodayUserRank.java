package org.sopt.makers.domain.app.soptamp.appjam;

import java.time.LocalDateTime;

public record AppjamTodayUserRank(
    Long userId, long todayPoints, LocalDateTime firstCertifiedAtToday) {}
