package org.sopt.makers.domain.crew.dining.event;

/** 참석자 직접 추가 트랜잭션에서 확정한 수신자, 회식명과 현재 회차 번호. */
public record DiningAttendeeAddedEvent(
    Long meetingId, String diningTitle, int roundNumber, Long attendeeUserId) {}
