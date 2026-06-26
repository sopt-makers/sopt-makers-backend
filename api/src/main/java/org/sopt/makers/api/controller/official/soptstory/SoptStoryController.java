package org.sopt.makers.api.controller.official.soptstory;

import static org.sopt.makers.api.controller.official.soptstory.SoptStorySuccessCode.CREATE_SOPT_STORY;
import static org.sopt.makers.api.controller.official.soptstory.SoptStorySuccessCode.GET_SOPT_STORIES;
import static org.sopt.makers.api.controller.official.soptstory.SoptStorySuccessCode.LIKE_SOPT_STORY;
import static org.sopt.makers.api.controller.official.soptstory.SoptStorySuccessCode.UNLIKE_SOPT_STORY;
import static org.sopt.makers.domain.official.soptstory.exception.SoptStoryFailure.INVALID_CLIENT_IP;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.official.soptstory.dto.CreateSoptStoryRequest;
import org.sopt.makers.api.controller.official.soptstory.dto.CreateSoptStoryResponse;
import org.sopt.makers.api.controller.official.soptstory.dto.GetSoptStoryListRequest;
import org.sopt.makers.api.controller.official.soptstory.dto.LikeSoptStoryResponse;
import org.sopt.makers.api.controller.official.soptstory.dto.PaginatedSoptStoryResponse;
import org.sopt.makers.api.controller.official.soptstory.dto.SoptStoryResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.official.soptstory.ScrapedArticle;
import org.sopt.makers.domain.official.soptstory.SoptStory;
import org.sopt.makers.domain.official.soptstory.exception.SoptStoryException;
import org.sopt.makers.domain.official.soptstory.service.SoptStoryService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "솝트스토리", description = "공식 홈페이지 솝트스토리 API")
@RestController
@RequestMapping("/api/v1/official/soptstories")
@RequiredArgsConstructor
public class SoptStoryController {

  private final SoptStoryService soptStoryService;

  @PostMapping
  @Operation(summary = "솝트스토리 생성", description = "솝트스토리를 생성합니다.")
  public ResponseEntity<BaseResponse<?>> createSoptStory(
      @Valid @RequestBody CreateSoptStoryRequest request) {
    ScrapedArticle article = soptStoryService.createSoptStory(request.link());

    return ResponseFactory.success(CREATE_SOPT_STORY, CreateSoptStoryResponse.from(article));
  }

  @PostMapping("/{id}/like")
  @Operation(summary = "솝트스토리 좋아요 누르기")
  public ResponseEntity<BaseResponse<?>> likeSoptStory(
      @PathVariable Long id, HttpServletRequest request) {
    String ip = extractAndValidateIp(request);
    Long likeId = soptStoryService.like(id, ip);

    return ResponseFactory.success(LIKE_SOPT_STORY, new LikeSoptStoryResponse(likeId, id, ip));
  }

  @PostMapping("/{id}/unlike")
  @Operation(summary = "솝트스토리 좋아요 취소하기")
  public ResponseEntity<BaseResponse<?>> unlikeSoptStory(
      @PathVariable Long id, HttpServletRequest request) {
    String ip = extractAndValidateIp(request);
    Long likeId = soptStoryService.unlike(id, ip);

    return ResponseFactory.success(UNLIKE_SOPT_STORY, new LikeSoptStoryResponse(likeId, id, ip));
  }

  @GetMapping
  @Operation(summary = "솝트스토리 리스트 조회")
  public ResponseEntity<BaseResponse<?>> getSoptStoryList(
      @ParameterObject @Valid @ModelAttribute GetSoptStoryListRequest request,
      HttpServletRequest httpRequest) {
    Page<SoptStory> page =
        soptStoryService.getSoptStoryList(
            request.sort(), request.pageNoOrDefault(), request.limitOrDefault());

    String ip = extractClientIp(httpRequest);
    Set<Long> likedIds = soptStoryService.getLikedSoptStoryIds(ip, page.getContent());
    List<SoptStoryResponse> content =
        page.getContent().stream()
            .map(story -> SoptStoryResponse.from(story, likedIds.contains(story.id())))
            .toList();

    PaginatedSoptStoryResponse response =
        PaginatedSoptStoryResponse.of(
            content,
            (int) page.getTotalElements(),
            request.limitOrDefault(),
            request.pageNoOrDefault());

    return ResponseFactory.success(GET_SOPT_STORIES, response);
  }

  private String extractAndValidateIp(HttpServletRequest request) {
    String ip = extractClientIp(request);
    if (ip == null || ip.isBlank()) {
      throw new SoptStoryException(INVALID_CLIENT_IP);
    }
    return ip;
  }

  private String extractClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Real-IP");
    if (ip == null || ip.isBlank()) {
      String xff = request.getHeader("X-Forwarded-For");
      if (xff != null && !xff.isBlank()) {
        ip = xff.split(",")[0].trim();
      }
    }
    if (ip == null || ip.isBlank()) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }
}
