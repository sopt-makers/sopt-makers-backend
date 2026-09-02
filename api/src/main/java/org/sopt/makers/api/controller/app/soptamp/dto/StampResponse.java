package org.sopt.makers.api.controller.app.soptamp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.app.soptamp.facade.SoptampFacade;
import org.sopt.makers.domain.app.soptamp.stamp.Stamp;

public final class StampResponse {

  private StampResponse() {}

  public record StampMain(
      Long id,
      String contents,
      List<String> images,
      String activityDate,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      Long missionId,
      int clapCount,
      int viewCount) {

    public static StampMain of(Stamp stamp) {
      return new StampMain(
          stamp.id(),
          stamp.contents(),
          stamp.images(),
          stamp.activityDate(),
          stamp.createdAt(),
          stamp.updatedAt(),
          stamp.missionId(),
          stamp.clapCount(),
          stamp.viewCount());
    }
  }

  public record StampView(
      Long id,
      String contents,
      List<String> images,
      String activityDate,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      Long missionId,
      String ownerNickname,
      int clapCount,
      int viewCount,
      @JsonProperty("isMine") boolean isMine,
      int myClapCount) {

    public static StampView of(SoptampFacade.StampView view) {
      Stamp stamp = view.stamp();
      return new StampView(
          stamp.id(),
          stamp.contents(),
          stamp.images(),
          stamp.activityDate(),
          stamp.createdAt(),
          stamp.updatedAt(),
          stamp.missionId(),
          view.ownerNickname(),
          stamp.clapCount(),
          stamp.viewCount(),
          view.mine(),
          view.myClapCount());
    }
  }

  public record StampId(Long stampId) {

    public static StampId of(Stamp stamp) {
      return new StampId(stamp.id());
    }
  }

  public record SoptampReportResponse(String reportUrl) {

    public static SoptampReportResponse of(SoptampFacade.SoptampReport report) {
      return new SoptampReportResponse(report.reportUrl());
    }
  }
}
