package org.sopt.makers.api.controller.internal;

import static org.sopt.makers.api.controller.internal.InternalScrapSuccessCode.SCRAP_LINK;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.internal.dto.InternalScrapRequest;
import org.sopt.makers.api.controller.internal.dto.InternalScrapResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.official.scrap.port.ArticleScraperPort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal")
@RequiredArgsConstructor
@Validated
public class InternalScrapController implements InternalScrapApi {

  private final ArticleScraperPort articleScraperPort;

  @Override
  @PostMapping("/scrap")
  public ResponseEntity<BaseResponse<?>> scrap(
      @Valid @RequestBody InternalScrapRequest.Scrap request) {
    ArticleScraperPort.ScrapedArticle result = articleScraperPort.scrap(request.link());
    return ResponseFactory.success(SCRAP_LINK, InternalScrapResponse.Scrap.of(result));
  }
}
