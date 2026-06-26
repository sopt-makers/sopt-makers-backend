package org.sopt.makers.api.controller.official.visitor;

import static org.sopt.makers.api.controller.official.visitor.VisitorSuccessCode.GET_TODAY_VISITOR;
import static org.sopt.makers.api.controller.official.visitor.VisitorSuccessCode.VISITOR_COUNT_UP;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.official.visitor.dto.VisitorResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.official.visitor.service.VisitorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/visitor")
@RequiredArgsConstructor
public class VisitorController implements VisitorApi {

  private final VisitorService visitorService;

  @Override
  @PostMapping
  public ResponseEntity<BaseResponse<?>> visitorCountUp(HttpServletRequest request) {
    String ip = extractClientIp(request);
    String userAgent = request.getHeader("User-Agent");
    visitorService.markVisited(ip, userAgent != null ? userAgent : "");
    return ResponseFactory.success(VISITOR_COUNT_UP);
  }

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getTodayVisitor() {
    return ResponseFactory.success(
        GET_TODAY_VISITOR, VisitorResponse.TodayCount.of(visitorService.getTodayCount()));
  }

  private String extractClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Real-IP");
    if (ip == null) {
      String xff = request.getHeader("X-Forwarded-For");
      if (xff != null && !xff.isEmpty()) {
        ip = xff.split(",")[0].trim();
      }
    }
    if (ip == null) {
      ip = request.getRemoteAddr();
    }
    return ip != null ? ip : "";
  }
}
