package org.sopt.makers.api.controller.playground.coffeechat;

import static org.sopt.makers.api.controller.playground.coffeechat.CoffeeChatSuccessCode.CREATE_COFFEE_CHAT_DETAILS;
import static org.sopt.makers.api.controller.playground.coffeechat.CoffeeChatSuccessCode.CREATE_COFFEE_CHAT_REVIEW;
import static org.sopt.makers.api.controller.playground.coffeechat.CoffeeChatSuccessCode.DELETE_COFFEE_CHAT_DETAILS;
import static org.sopt.makers.api.controller.playground.coffeechat.CoffeeChatSuccessCode.GET_COFFEE_CHAT_ACTIVATE;
import static org.sopt.makers.api.controller.playground.coffeechat.CoffeeChatSuccessCode.GET_COFFEE_CHAT_DETAIL;
import static org.sopt.makers.api.controller.playground.coffeechat.CoffeeChatSuccessCode.GET_COFFEE_CHAT_HISTORIES;
import static org.sopt.makers.api.controller.playground.coffeechat.CoffeeChatSuccessCode.GET_RANDOM_COFFEE_CHAT_LIST;
import static org.sopt.makers.api.controller.playground.coffeechat.CoffeeChatSuccessCode.GET_RECENT_COFFEE_CHAT_LIST;
import static org.sopt.makers.api.controller.playground.coffeechat.CoffeeChatSuccessCode.GET_RECENT_COFFEE_CHAT_REVIEWS;
import static org.sopt.makers.api.controller.playground.coffeechat.CoffeeChatSuccessCode.GET_SEARCH_COFFEE_CHAT_LIST;
import static org.sopt.makers.api.controller.playground.coffeechat.CoffeeChatSuccessCode.SEND_COFFEE_CHAT_REQUEST;
import static org.sopt.makers.api.controller.playground.coffeechat.CoffeeChatSuccessCode.UPDATE_COFFEE_CHAT_DETAILS;
import static org.sopt.makers.api.controller.playground.coffeechat.CoffeeChatSuccessCode.UPDATE_COFFEE_CHAT_OPEN;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.playground.coffeechat.dto.CoffeeChatDetailResponse;
import org.sopt.makers.api.controller.playground.coffeechat.dto.CoffeeChatDetailsRequest;
import org.sopt.makers.api.controller.playground.coffeechat.dto.CoffeeChatHistoryItemResponse;
import org.sopt.makers.api.controller.playground.coffeechat.dto.CoffeeChatHistoryTitleResponse;
import org.sopt.makers.api.controller.playground.coffeechat.dto.CoffeeChatListResponse;
import org.sopt.makers.api.controller.playground.coffeechat.dto.CoffeeChatOpenRequest;
import org.sopt.makers.api.controller.playground.coffeechat.dto.CoffeeChatRequest;
import org.sopt.makers.api.controller.playground.coffeechat.dto.CoffeeChatReviewInfoResponse;
import org.sopt.makers.api.controller.playground.coffeechat.dto.CoffeeChatReviewListResponse;
import org.sopt.makers.api.controller.playground.coffeechat.dto.CoffeeChatReviewRequest;
import org.sopt.makers.api.controller.playground.coffeechat.dto.CoffeeChatVoResponse;
import org.sopt.makers.api.controller.playground.coffeechat.dto.RandomCoffeeChatResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.playground.coffeechat.service.CoffeeChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members/coffeechat")
public class CoffeeChatController implements CoffeeChatApi {

  private final CoffeeChatService coffeeChatService;

  @Override
  @GetMapping("/random")
  public ResponseEntity<BaseResponse<?>> getRandomCoffeeChatList(@CurrentUserId Long userId) {
    List<RandomCoffeeChatResponse> responses =
        coffeeChatService.getRandomCoffeeChatList(userId).stream()
            .map(RandomCoffeeChatResponse::from)
            .toList();
    return ResponseFactory.success(GET_RANDOM_COFFEE_CHAT_LIST, responses);
  }

  @Override
  @GetMapping("/recent")
  public ResponseEntity<BaseResponse<?>> getRecentCoffeeChatList() {
    List<CoffeeChatVoResponse> responses =
        coffeeChatService.getRecentCoffeeChatList().stream()
            .map(CoffeeChatVoResponse::from)
            .toList();
    return ResponseFactory.success(GET_RECENT_COFFEE_CHAT_LIST, new CoffeeChatListResponse(responses));
  }

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getSearchCoffeeChatList(
      @CurrentUserId Long userId,
      @RequestParam(required = false) String section,
      @RequestParam(required = false) String topicType,
      @RequestParam(required = false) String career,
      @RequestParam(required = false) String part,
      @RequestParam(required = false) String search) {
    List<CoffeeChatVoResponse> responses =
        coffeeChatService.getSearchCoffeeChatList(userId, section, topicType, career, part, search)
            .stream()
            .map(CoffeeChatVoResponse::from)
            .toList();
    return ResponseFactory.success(GET_SEARCH_COFFEE_CHAT_LIST, new CoffeeChatListResponse(responses));
  }

  @Override
  @GetMapping("/{memberId}")
  public ResponseEntity<BaseResponse<?>> getCoffeeChatDetail(
      @CurrentUserId Long userId, @PathVariable Long memberId) {
    return ResponseFactory.success(
        GET_COFFEE_CHAT_DETAIL,
        CoffeeChatDetailResponse.from(coffeeChatService.getCoffeeChatDetail(userId, memberId)));
  }

  @Override
  @PostMapping
  public ResponseEntity<BaseResponse<?>> sendCoffeeChatRequest(
      @CurrentUserId Long userId, @Valid @RequestBody CoffeeChatRequest request) {
    coffeeChatService.sendCoffeeChatRequest(
        request.receiverId(), request.senderPhone(), request.category(), request.content(), userId);
    return ResponseFactory.success(SEND_COFFEE_CHAT_REQUEST);
  }

  @Override
  @GetMapping("/open")
  public ResponseEntity<BaseResponse<?>> getCoffeeChatActivate(@CurrentUserId Long userId) {
    return ResponseFactory.success(GET_COFFEE_CHAT_ACTIVATE, coffeeChatService.getCoffeeChatActivate(userId));
  }

  @Override
  @PatchMapping("/open")
  public ResponseEntity<BaseResponse<?>> updateCoffeeChatOpen(
      @CurrentUserId Long userId, @Valid @RequestBody CoffeeChatOpenRequest request) {
    coffeeChatService.updateCoffeeChatOpen(userId, request.open());
    return ResponseFactory.success(UPDATE_COFFEE_CHAT_OPEN);
  }

  @Override
  @GetMapping("/history")
  public ResponseEntity<BaseResponse<?>> getCoffeeChatHistories(@CurrentUserId Long userId) {
    List<CoffeeChatHistoryItemResponse> items =
        coffeeChatService.getCoffeeChatHistories(userId).stream()
            .map(CoffeeChatHistoryItemResponse::from)
            .toList();
    return ResponseFactory.success(GET_COFFEE_CHAT_HISTORIES, new CoffeeChatHistoryTitleResponse(items));
  }

  @Override
  @PostMapping("/details")
  public ResponseEntity<BaseResponse<?>> createCoffeeChatDetails(
      @CurrentUserId Long userId, @Valid @RequestBody CoffeeChatDetailsRequest request) {
    coffeeChatService.createCoffeeChatDetails(
        userId,
        request.memberInfo().career(),
        request.memberInfo().introduction(),
        request.coffeeChatInfo().sections(),
        request.coffeeChatInfo().bio(),
        request.coffeeChatInfo().topicTypes(),
        request.coffeeChatInfo().topic(),
        request.coffeeChatInfo().meetingType(),
        request.coffeeChatInfo().guideline());
    return ResponseFactory.success(CREATE_COFFEE_CHAT_DETAILS);
  }

  @Override
  @PutMapping("/details")
  public ResponseEntity<BaseResponse<?>> updateCoffeeChatDetails(
      @CurrentUserId Long userId, @Valid @RequestBody CoffeeChatDetailsRequest request) {
    coffeeChatService.updateCoffeeChatDetails(
        userId,
        request.memberInfo().career(),
        request.memberInfo().introduction(),
        request.coffeeChatInfo().sections(),
        request.coffeeChatInfo().bio(),
        request.coffeeChatInfo().topicTypes(),
        request.coffeeChatInfo().topic(),
        request.coffeeChatInfo().meetingType(),
        request.coffeeChatInfo().guideline());
    return ResponseFactory.success(UPDATE_COFFEE_CHAT_DETAILS);
  }

  @Override
  @DeleteMapping("/details")
  public ResponseEntity<BaseResponse<?>> deleteCoffeeChatDetails(@CurrentUserId Long userId) {
    coffeeChatService.deleteCoffeeChatDetails(userId);
    return ResponseFactory.success(DELETE_COFFEE_CHAT_DETAILS);
  }

  @Override
  @PostMapping("/review")
  public ResponseEntity<BaseResponse<?>> createCoffeeChatReview(
      @CurrentUserId Long userId, @Valid @RequestBody CoffeeChatReviewRequest request) {
    coffeeChatService.createCoffeeChatReview(
        userId, request.coffeeChatId(), request.nickname(), request.content());
    return ResponseFactory.success(CREATE_COFFEE_CHAT_REVIEW);
  }

  @Override
  @GetMapping("/reviews")
  public ResponseEntity<BaseResponse<?>> getRecentCoffeeChatReviews() {
    List<CoffeeChatReviewInfoResponse> reviews =
        coffeeChatService.getRecentCoffeeChatReviews().stream()
            .map(CoffeeChatReviewInfoResponse::from)
            .toList();
    return ResponseFactory.success(GET_RECENT_COFFEE_CHAT_REVIEWS, new CoffeeChatReviewListResponse(reviews));
  }
}
