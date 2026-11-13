package org.sopt.makers.domain.crew.dining.event;

/** 상태 변경 트랜잭션에서 확정한 수신자, 회식명과 현재 회차 번호. */
public record DiningPayerApprovedEvent(
    Long meetingId, Long roundId, Long requesterUserId, String diningTitle, int roundNumber) {}
