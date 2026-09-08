package org.sopt.makers.api.controller.playground.user;

import static org.sopt.makers.api.controller.playground.user.PlaygroundUserQuestionSuccessCode.CREATE_ANSWER;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserQuestionSuccessCode.CREATE_QUESTION;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserQuestionSuccessCode.DELETE_ANSWER;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserQuestionSuccessCode.DELETE_QUESTION;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserQuestionSuccessCode.GET_LATEST_ANSWERED_QUESTIONS;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserQuestionSuccessCode.GET_MY_LATEST_ANSWERED_QUESTION_LOCATION;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserQuestionSuccessCode.GET_QUESTIONS;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserQuestionSuccessCode.GET_QUESTION_LOCATION;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserQuestionSuccessCode.GET_UNANSWERED_COUNT;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserQuestionSuccessCode.REPORT_QUESTION;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserQuestionSuccessCode.TOGGLE_ANSWER_REACTION;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserQuestionSuccessCode.TOGGLE_QUESTION_REACTION;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserQuestionSuccessCode.UPDATE_ANSWER;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserQuestionSuccessCode.UPDATE_QUESTION;

import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.playground.user.dto.AnswerSaveRequest;
import org.sopt.makers.api.controller.playground.user.dto.AnswerUpdateRequest;
import org.sopt.makers.api.controller.playground.user.dto.LatestAnsweredQuestionsResponse;
import org.sopt.makers.api.controller.playground.user.dto.MyLatestAnsweredQuestionLocationResponse;
import org.sopt.makers.api.controller.playground.user.dto.QuestionLocationResponse;
import org.sopt.makers.api.controller.playground.user.dto.QuestionReportRequest;
import org.sopt.makers.api.controller.playground.user.dto.QuestionSaveRequest;
import org.sopt.makers.api.controller.playground.user.dto.QuestionUpdateRequest;
import org.sopt.makers.api.controller.playground.user.dto.QuestionsResponse;
import org.sopt.makers.api.controller.playground.user.dto.UnansweredCountResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.playground.member.ask.QuestionTab;
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
public class PlaygroundUserQuestionController implements PlaygroundUserQuestionApi {

  private final UserAskCommandService userAskCommandService;
  private final UserAskQueryService userAskQueryService;

  @Override
  @PostMapping("/questions/{receiverId}")
  public ResponseEntity<BaseResponse<?>> createQuestion(
      @CurrentUserId Long userId,
      @PathVariable Long receiverId,
      @RequestBody @Valid QuestionSaveRequest request) {
    Long questionId =
        userAskCommandService
            .createAsk(userId, receiverId, request.content(), request.isAnonymous())
            .id();
    return ResponseFactory.success(CREATE_QUESTION, Map.of("questionId", questionId));
  }

  @Override
  @PutMapping("/questions/{questionId}")
  public ResponseEntity<BaseResponse<?>> updateQuestion(
      @CurrentUserId Long userId,
      @PathVariable Long questionId,
      @RequestBody @Valid QuestionUpdateRequest request) {
    userAskCommandService.updateAsk(userId, questionId, request.content(), request.isAnonymous());
    return ResponseFactory.success(UPDATE_QUESTION, Map.of("success", true));
  }

  @Override
  @DeleteMapping("/questions/{questionId}")
  public ResponseEntity<BaseResponse<?>> deleteQuestion(
      @CurrentUserId Long userId, @PathVariable Long questionId) {
    userAskCommandService.deleteAsk(userId, questionId);
    return ResponseFactory.success(DELETE_QUESTION, Map.of("success", true));
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
  public ResponseEntity<BaseResponse<?>> toggleQuestionReaction(
      @CurrentUserId Long userId, @PathVariable Long questionId) {
    userAskCommandService.toggleAskReaction(userId, questionId);
    return ResponseFactory.success(TOGGLE_QUESTION_REACTION, Map.of("success", true));
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
  public ResponseEntity<BaseResponse<?>> reportQuestion(
      @CurrentUserId Long userId,
      @PathVariable Long questionId,
      @RequestBody QuestionReportRequest request) {
    userAskCommandService.reportAsk(userId, questionId, request.reason());
    return ResponseFactory.success(REPORT_QUESTION, Map.of("success", true));
  }

  @Override
  @GetMapping("/{memberId}/questions")
  public ResponseEntity<BaseResponse<?>> getQuestions(
      @CurrentUserId Long userId,
      @PathVariable Long memberId,
      @RequestParam(value = "tab", required = false) QuestionTab tab,
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size) {
    return ResponseFactory.success(
        GET_QUESTIONS,
        QuestionsResponse.from(userAskQueryService.getAsks(userId, memberId, tab, page, size)));
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
  public ResponseEntity<BaseResponse<?>> getMyLatestAnsweredQuestionLocation(
      @CurrentUserId Long userId, @PathVariable Long memberId) {
    return ResponseFactory.success(
        GET_MY_LATEST_ANSWERED_QUESTION_LOCATION,
        MyLatestAnsweredQuestionLocationResponse.from(
            userAskQueryService.getMyLatestAnsweredAskLocation(userId, memberId)));
  }

  @Override
  @GetMapping("/{memberId}/questions/{questionId}/location")
  public ResponseEntity<BaseResponse<?>> getQuestionLocation(
      @PathVariable Long memberId, @PathVariable Long questionId) {
    return ResponseFactory.success(
        GET_QUESTION_LOCATION,
        QuestionLocationResponse.from(userAskQueryService.getAskLocation(memberId, questionId)));
  }

  @Override
  @GetMapping("/questions/latest")
  public ResponseEntity<BaseResponse<?>> getLatestAnsweredQuestions() {
    return ResponseFactory.success(
        GET_LATEST_ANSWERED_QUESTIONS,
        LatestAnsweredQuestionsResponse.from(userAskQueryService.getLatestAnsweredAsks()));
  }
}
