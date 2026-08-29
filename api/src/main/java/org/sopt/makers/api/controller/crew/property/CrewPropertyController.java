package org.sopt.makers.api.controller.crew.property;

import static org.sopt.makers.api.controller.crew.property.CrewPropertySuccessCode.GET_CREW_PROPERTY;
import static org.sopt.makers.api.controller.crew.property.CrewPropertySuccessCode.GET_HOME_PROPERTY;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.crew.property.dto.HomePropertyResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.crew.property.service.CrewPropertyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/property/v2")
@RequiredArgsConstructor
public class CrewPropertyController implements CrewPropertyApi {

  private final CrewPropertyService crewPropertyService;

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getProperty(@RequestParam(required = false) String key) {
    Object result =
        key == null ? crewPropertyService.getAllValues() : crewPropertyService.getValues(key);
    return ResponseFactory.success(GET_CREW_PROPERTY, result);
  }

  @Override
  @GetMapping("/home")
  public ResponseEntity<BaseResponse<?>> getHomeProperty() {
    return ResponseFactory.success(
        GET_HOME_PROPERTY, HomePropertyResponse.from(crewPropertyService.getHomeContents()));
  }
}
