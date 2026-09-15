package org.sopt.makers.api.controller.app.config;

import static org.sopt.makers.api.controller.app.config.AppConfigSuccessCode.GET_AVAILABILITY;

import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.app.config.dto.AvailabilityResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/config")
public class AppConfigController implements AppConfigApi {

  private final boolean available;

  public AppConfigController(@Value("${sopt.app.available}") boolean available) {
    this.available = available;
  }

  @Override
  @GetMapping("/availability")
  public ResponseEntity<BaseResponse<AvailabilityResponse>> getAvailability() {
    return ResponseFactory.typedSuccess(GET_AVAILABILITY, AvailabilityResponse.of(available));
  }
}
