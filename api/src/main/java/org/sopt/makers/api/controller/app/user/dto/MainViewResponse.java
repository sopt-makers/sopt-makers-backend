package org.sopt.makers.api.controller.app.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.sopt.makers.domain.app.home.MainView;

public record MainViewResponse(
    Playground user, Operation operation, @JsonProperty("isAllConfirm") boolean isAllConfirm) {

  private static final Double DEFAULT_ATTENDANCE_SCORE = 0D;
  private static final String DEFAULT_ANNOUNCEMENT = "";

  public static MainViewResponse of(MainView mainView) {
    return new MainViewResponse(
        new Playground(
            mainView.status().name(),
            mainView.name(),
            mainView.profileImage(),
            mainView.part(),
            mainView.generationList()),
        new Operation(DEFAULT_ATTENDANCE_SCORE, DEFAULT_ANNOUNCEMENT),
        mainView.isAllConfirm());
  }

  public record Playground(
      String status, String name, String profileImage, String part, List<Long> generationList) {}

  public record Operation(Double attendanceScore, String announcement) {}
}
