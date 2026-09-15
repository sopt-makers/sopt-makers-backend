package org.sopt.makers.api.controller.playground.user;

import static org.sopt.makers.api.controller.playground.user.PlaygroundUserAskSuccessCode.CREATE_ANSWER;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserAskSuccessCode.CREATE_ASK;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserAskSuccessCode.DELETE_ANSWER;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserAskSuccessCode.DELETE_ASK;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserAskSuccessCode.GET_ASKS;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserAskSuccessCode.GET_ASK_LOCATION;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserAskSuccessCode.GET_LATEST_ANSWERED_ASKS;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserAskSuccessCode.GET_MY_LATEST_ANSWERED_ASK_LOCATION;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserAskSuccessCode.GET_UNANSWERED_COUNT;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserAskSuccessCode.REPORT_ASK;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserAskSuccessCode.TOGGLE_ANSWER_REACTION;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserAskSuccessCode.TOGGLE_ASK_REACTION;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserAskSuccessCode.UPDATE_ANSWER;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserAskSuccessCode.UPDATE_ASK;

import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.playground.user.dto.AnswerSaveRequest;
import org.sopt.makers.api.controller.playground.user.dto.AnswerUpdateRequest;
import org.sopt.makers.api.controller.playground.user.dto.AskLocationResponse;
import org.sopt.makers.api.controller.playground.user.dto.AskReportRequest;
import org.sopt.makers.api.controller.playground.user.dto.AskSaveRequest;
import org.sopt.makers.api.controller.playground.user.dto.AskUpdateRequest;
import org.sopt.makers.api.controller.playground.user.dto.AsksResponse;
import org.sopt.makers.api.controller.playground.user.dto.LatestAnsweredAsksResponse;
import org.sopt.makers.api.controller.playground.user.dto.MyLatestAnsweredAskLocationResponse;
import org.sopt.makers.api.controller.playground.user.dto.UnansweredCountResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.playground.member.ask.AskTab;
import org.sopt.makers.domain.playground.member.ask.service.UserAskCommandService;
import org.sopt.makers.domain.playground.member.ask.service.UserAskQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class PlaygroundUserAskController implements PlaygroundUserAskApi {

  private final UserAskCommandService userAskCommandService;
  private final UserAskQueryService userAskQueryService;

  @Override
  @PostMapping("/questions/{receiverId}")
  public ResponseEntity<BaseResponse<?>> createAsk(
      @CurrentUserId Long userId,
      @PathVariable Long receiverId,
      @RequestBody @Valid AskSaveRequest request) {
    Long askId =
        userAskCommandService
            .createAsk(userId, receiverId, request.content(), request.isAnonymous())
            .id();
    return ResponseFactory.success(CREATE_ASK, Map.of("questionId", askId));
  }

  @Override
  @PutMapping("/questions/{questionId}")
  public ResponseEntity<BaseResponse<?>> updateAsk(
      @CurrentUserId Long userId,
      @PathVariable Long questionId,
      @RequestBody @Valid AskUpdateRequest request) {
    userAskCommandService.updateAsk(userId, questionId, request.content(), request.isAnonymous());
    return ResponseFactory.success(UPDATE_ASK, Map.of("success", true));
  }

  @Override
  @DeleteMapping("/questions/{questionId}")
  public ResponseEntity<BaseResponse<?>> deleteAsk(
      @CurrentUserId Long userId, @PathVariable Long questionId) {
    userAskCommandService.deleteAsk(userId, questionId);
    return ResponseFactory.success(DELETE_ASK, Map.of("success", true));
  }

  @Override
  @PostMapping("/questions/{questionId}/answer")
  public ResponseEntity<BaseResponse<?>> createAnswer(
      @CurrentUserId Long userId,
      @PathVariable Long questionId,
      @RequestBody @Valid AnswerSaveRequest request) {
    Long answerId = userAskCommandService.createAnswer(userId, questionId, request.content()).id();
    return ResponseFactory.success(CREATE_ANSWER, Map.of("answerId", answerId));
  }

  @Override
  @PutMapping("/answers/{answerId}")
  public ResponseEntity<BaseResponse<?>> updateAnswer(
      @CurrentUserId Long userId,
      @PathVariable Long answerId,
      @RequestBody @Valid AnswerUpdateRequest request) {
    userAskCommandService.updateAnswer(userId, answerId, request.content());
    return ResponseFactory.success(UPDATE_ANSWER, Map.of("success", true));
  }

  @Override
  @DeleteMapping("/answers/{answerId}")
  public ResponseEntity<BaseResponse<?>> deleteAnswer(
      @CurrentUserId Long userId, @PathVariable Long answerId) {
    userAskCommandService.deleteAnswer(userId, answerId);
    return ResponseFactory.success(DELETE_ANSWER, Map.of("success", true));
  }

  @Override
  @PostMapping("/questions/{questionId}/reactions")
  public ResponseEntity<BaseResponse<?>> toggleAskReaction(
      @CurrentUserId Long userId, @PathVariable Long questionId) {
    userAskCommandService.toggleAskReaction(userId, questionId);
    return ResponseFactory.success(TOGGLE_ASK_REACTION, Map.of("success", true));
  }

  @Override
  @PostMapping("/answers/{answerId}/reactions")
  public ResponseEntity<BaseResponse<?>> toggleAnswerReaction(
      @CurrentUserId Long userId, @PathVariable Long answerId) {
    userAskCommandService.toggleAnswerReaction(userId, answerId);
    return ResponseFactory.success(TOGGLE_ANSWER_REACTION, Map.of("success", true));
  }

  @Override
  @PostMapping("/questions/{questionId}/report")
  public ResponseEntity<BaseResponse<?>> reportAsk(
      @CurrentUserId Long userId,
      @PathVariable Long questionId,
      @RequestBody AskReportRequest request) {
    userAskCommandService.reportAsk(userId, questionId, request.reason());
    return ResponseFactory.success(REPORT_ASK, Map.of("success", true));
  }

  @Override
  @GetMapping("/{memberId}/questions")
  public ResponseEntity<BaseResponse<?>> getAsks(
      @CurrentUserId Long userId,
      @PathVariable Long memberId,
      @RequestParam(value = "tab", required = false) AskTab tab,
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size) {
    return ResponseFactory.success(
        GET_ASKS,
        AsksResponse.from(userAskQueryService.getAsks(userId, memberId, tab, page, size)));
  }

  @Override
  @GetMapping("/me/questions/unanswered-count")
  public ResponseEntity<BaseResponse<?>> getUnansweredCount(@CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_UNANSWERED_COUNT,
        UnansweredCountResponse.from(userAskQueryService.getUnansweredCount(userId)));
  }

  @Override
  @GetMapping("/{memberId}/questions/my-latest-answered")
  public ResponseEntity<BaseResponse<?>> getMyLatestAnsweredAskLocation(
      @CurrentUserId Long userId, @PathVariable Long memberId) {
    return ResponseFactory.success(
        GET_MY_LATEST_ANSWERED_ASK_LOCATION,
        MyLatestAnsweredAskLocationResponse.from(
            userAskQueryService.getMyLatestAnsweredAskLocation(userId, memberId)));
  }

  @Override
  @GetMapping("/{memberId}/questions/{questionId}/location")
  public ResponseEntity<BaseResponse<?>> getAskLocation(
      @PathVariable Long memberId, @PathVariable Long questionId) {
    return ResponseFactory.success(
        GET_ASK_LOCATION,
        AskLocationResponse.from(userAskQueryService.getAskLocation(memberId, questionId)));
  }

  @Override
  @GetMapping("/questions/latest")
  public ResponseEntity<BaseResponse<?>> getLatestAnsweredAsks() {
    return ResponseFactory.success(
        GET_LATEST_ANSWERED_ASKS,
        LatestAnsweredAsksResponse.from(userAskQueryService.getLatestAnsweredAsks()));
  }
}
