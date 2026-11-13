package org.sopt.makers.domain.crew.dining.model;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.crew.dining.model.enums.DiningDeletionTargetType;

/**
 * 회식, 회차, 참석 기록 삭제 시 보관하는 스냅샷 원본 삭제 이후에도 남아야 하므로 FK 를 두지 않는다. snapshot 형식: { "schemaVersion": 1,
 * "data": { ... } } 엔티티 구조가 바뀌어 snapshot 의 data 형식이 달라지면 SNAPSHOT_SCHEMA_VERSION 을 올린다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiningDeletionHistory {

  public static final int SNAPSHOT_SCHEMA_VERSION = 1;

  private Long id;

  /** 회식(meeting) id */
  private Long meetingId;

  /** 삭제 대상 종류 (회식 / 회차 / 참석) */
  private DiningDeletionTargetType targetType;

  /** 삭제 대상 id (meeting / dining_round / dining_attendance 의 id) */
  private Long targetId;

  /** 삭제한 유저 id */
  private Long deletedByUserId;

  /** 삭제 직전 데이터 스냅샷 (schemaVersion + data) */
  private Map<String, Object> snapshot;

  @Builder
  public DiningDeletionHistory(
      Long meetingId,
      DiningDeletionTargetType targetType,
      Long targetId,
      Long deletedByUserId,
      Map<String, Object> data) {
    this.meetingId = meetingId;
    this.targetType = targetType;
    this.targetId = targetId;
    this.deletedByUserId = deletedByUserId;
    this.snapshot = wrapSnapshot(data);
  }

  private static Map<String, Object> wrapSnapshot(Map<String, Object> data) {
    Map<String, Object> snapshot = new LinkedHashMap<>();
    snapshot.put("schemaVersion", SNAPSHOT_SCHEMA_VERSION);
    snapshot.put("data", data);
    return snapshot;
  }

  public DiningDeletionHistory(
      Long id,
      Long meetingId,
      DiningDeletionTargetType targetType,
      Long targetId,
      Long deletedByUserId,
      Map<String, Object> snapshot,
      java.time.LocalDateTime createdTimestamp) {
    this.id = id;
    this.meetingId = meetingId;
    this.targetType = targetType;
    this.targetId = targetId;
    this.deletedByUserId = deletedByUserId;
    this.snapshot = snapshot;
    this.createdTimestamp = createdTimestamp;
  }

  private java.time.LocalDateTime createdTimestamp;
}
