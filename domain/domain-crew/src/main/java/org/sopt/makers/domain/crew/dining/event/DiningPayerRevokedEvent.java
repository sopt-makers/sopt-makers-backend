package org.sopt.makers.domain.crew.dining.event;

/** 승인 해제 트랜잭션에서 확정한 수신자, 회식명과 현재 회차 번호. */
public record DiningPayerRevokedEvent(
    Long meetingId, Long roundId, Long requesterUserId, String diningTitle, int roundNumber) {}
