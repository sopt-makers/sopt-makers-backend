package org.sopt.makers.domain.crew.dining.model;

import static org.sopt.makers.domain.crew.dining.exception.DiningFailure.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.crew.dining.exception.DiningException;
import org.sopt.makers.domain.crew.dining.model.enums.DiningRoundStatus;

/** 회식 회차 (1차, 2차, ...) 회차 번호는 저장하지 않고 같은 회식(meetingId) 내 id 오름차순으로 계산한다. */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiningRound {

  private Long id;

  /** 회식(meeting) id */
  private Long meetingId;

  /** 회식 장소 (null 이면 장소 미정) */
  private String place;

  /** 네이버지도 링크 */
  private String naverMapLink;

  /** 카카오맵 링크 */
  private String kakaoMapLink;

  /** 시작 시간 (null 이면 미노출) */
  private LocalTime startTime;

  /** 회차 상태 (진행중 / 마감) */
  private DiningRoundStatus status;

  /** 마감 시각 */
  private LocalDateTime closedAt;

  /** 장소 수정 여부 */
  private boolean isPlaceModified;

  /** 장소 링크 수정 여부 (네이버지도, 카카오맵 공통) */
  private boolean isLinkModified;

  /** 시작 시간 수정 여부 */
  private boolean isStartTimeModified;

  @Builder
  public DiningRound(
      Long meetingId, String place, String naverMapLink, String kakaoMapLink, LocalTime startTime) {
    this.meetingId = meetingId;
    this.place = place;
    this.naverMapLink = naverMapLink;
    this.kakaoMapLink = kakaoMapLink;
    this.startTime = startTime;
    this.status = DiningRoundStatus.OPEN;
    this.isPlaceModified = false;
    this.isLinkModified = false;
    this.isStartTimeModified = false;
  }

  /** 회차 정보 수정. 값이 실제로 바뀐 항목만 수정됨 표시를 남긴다. 마감된 회차도 수정할 수 있다. */
  /**
   * @return 이번 요청으로 장소, 링크, 시작 시간 중 하나라도 실제로 바뀌었는지 (수정 알림 발송 기준)
   */
  public boolean update(
      String place, String naverMapLink, String kakaoMapLink, LocalTime startTime) {
    boolean placeChanged = !Objects.equals(this.place, place);
    boolean linkChanged =
        !Objects.equals(this.naverMapLink, naverMapLink)
            || !Objects.equals(this.kakaoMapLink, kakaoMapLink);
    boolean startTimeChanged = !Objects.equals(this.startTime, startTime);

    if (placeChanged) {
      this.isPlaceModified = true;
    }
    if (linkChanged) {
      this.isLinkModified = true;
    }
    if (startTimeChanged) {
      this.isStartTimeModified = true;
    }

    this.place = place;
    this.naverMapLink = naverMapLink;
    this.kakaoMapLink = kakaoMapLink;
    this.startTime = startTime;
    return placeChanged || linkChanged || startTimeChanged;
  }

  public void close(LocalDateTime now) {
    validateNotClosed();
    this.status = DiningRoundStatus.CLOSED;
    this.closedAt = now;
  }

  public void validateNotClosed() {
    if (isClosed()) {
      throw new DiningException(ALREADY_CLOSED_DINING_ROUND);
    }
  }

  public boolean isClosed() {
    return this.status == DiningRoundStatus.CLOSED;
  }

  /** "수정된 정보가 있어요" 표시 여부. 한 번 수정되면 계속 표시한다. */
  public boolean hasModifiedInfo() {
    return this.isPlaceModified || this.isLinkModified || this.isStartTimeModified;
  }

  public DiningRound(
      Long id,
      Long meetingId,
      String place,
      String naverMapLink,
      String kakaoMapLink,
      LocalTime startTime,
      DiningRoundStatus status,
      LocalDateTime closedAt,
      boolean isPlaceModified,
      boolean isLinkModified,
      boolean isStartTimeModified,
      java.time.LocalDateTime createdTimestamp) {
    this.id = id;
    this.meetingId = meetingId;
    this.place = place;
    this.naverMapLink = naverMapLink;
    this.kakaoMapLink = kakaoMapLink;
    this.startTime = startTime;
    this.status = status;
    this.closedAt = closedAt;
    this.isPlaceModified = isPlaceModified;
    this.isLinkModified = isLinkModified;
    this.isStartTimeModified = isStartTimeModified;
    this.createdTimestamp = createdTimestamp;
  }

  private java.time.LocalDateTime createdTimestamp;
}
