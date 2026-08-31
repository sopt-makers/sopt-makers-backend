package org.sopt.makers.api.controller.app.soptletter;

import static org.sopt.makers.api.controller.app.soptletter.SoptLetterSuccessCode.ADD_LIKE;
import static org.sopt.makers.api.controller.app.soptletter.SoptLetterSuccessCode.COMPLETE_ONBOARDING;
import static org.sopt.makers.api.controller.app.soptletter.SoptLetterSuccessCode.DELETE_MESSAGE;
import static org.sopt.makers.api.controller.app.soptletter.SoptLetterSuccessCode.GET_CTA;
import static org.sopt.makers.api.controller.app.soptletter.SoptLetterSuccessCode.GET_MESSAGE;
import static org.sopt.makers.api.controller.app.soptletter.SoptLetterSuccessCode.GET_ONBOARDING_PROFILE;
import static org.sopt.makers.api.controller.app.soptletter.SoptLetterSuccessCode.GET_REPORT_FORM;
import static org.sopt.makers.api.controller.app.soptletter.SoptLetterSuccessCode.GET_TOPIC;
import static org.sopt.makers.api.controller.app.soptletter.SoptLetterSuccessCode.GET_TOPICS;
import static org.sopt.makers.api.controller.app.soptletter.SoptLetterSuccessCode.GET_TOPIC_MESSAGES;
import static org.sopt.makers.api.controller.app.soptletter.SoptLetterSuccessCode.REMOVE_LIKE;
import static org.sopt.makers.api.controller.app.soptletter.SoptLetterSuccessCode.UPDATE_MESSAGE;
import static org.sopt.makers.api.controller.app.soptletter.SoptLetterSuccessCode.WRITE_MESSAGE;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.app.soptletter.dto.CtaResponse;
import org.sopt.makers.api.controller.app.soptletter.dto.MessageResponse;
import org.sopt.makers.api.controller.app.soptletter.dto.OnboardingProfileResponse;
import org.sopt.makers.api.controller.app.soptletter.dto.ReportFormResponse;
import org.sopt.makers.api.controller.app.soptletter.dto.TopicDetailResponse;
import org.sopt.makers.api.controller.app.soptletter.dto.TopicMessagesResponse;
import org.sopt.makers.api.controller.app.soptletter.dto.TopicsResponse;
import org.sopt.makers.api.controller.app.soptletter.dto.WriteMessageRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.generation.port.CurrentGenerationPort;
import org.sopt.makers.domain.app.soptletter.facade.SoptLetterFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/sopt-letter")
@RequiredArgsConstructor
@Validated
public class SoptLetterController implements SoptLetterApi {

  private final SoptLetterFacade soptLetterFacade;
  private final CurrentGenerationPort currentGenerationPort;

  @Override
  @GetMapping("/onboarding")
  public ResponseEntity<BaseResponse<?>> getOnboardingProfile(@CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_ONBOARDING_PROFILE,
        OnboardingProfileResponse.of(
            soptLetterFacade.getOrCreateProfile(userId),
            currentGenerationPort.getCurrentGeneration()));
  }

  @Override
  @PostMapping("/onboarding/complete")
  public ResponseEntity<BaseResponse<?>> completeOnboarding(@CurrentUserId Long userId) {
    return ResponseFactory.success(
        COMPLETE_ONBOARDING,
        OnboardingProfileResponse.of(
            soptLetterFacade.completeOnboarding(userId),
            currentGenerationPort.getCurrentGeneration()));
  }

  @Override
  @GetMapping("/report-form")
  public ResponseEntity<BaseResponse<?>> getReportForm() {
    return ResponseFactory.success(
        GET_REPORT_FORM, ReportFormResponse.of(soptLetterFacade.getReportFormUrl()));
  }

  @Override
  @GetMapping("/cta")
  public ResponseEntity<BaseResponse<?>> getCta() {
    return ResponseFactory.success(GET_CTA, CtaResponse.of(soptLetterFacade.findActiveCta()));
  }

  @Override
  @GetMapping("/topics")
  public ResponseEntity<BaseResponse<?>> getTopics(@RequestParam(required = false) String type) {
    return ResponseFactory.success(GET_TOPICS, TopicsResponse.of(soptLetterFacade.getTopics(type)));
  }

  @Override
  @GetMapping("/topics/{topicId}")
  public ResponseEntity<BaseResponse<?>> getTopic(@PathVariable Long topicId) {
    return ResponseFactory.success(
        GET_TOPIC,
        TopicDetailResponse.of(soptLetterFacade.getTopic(topicId), soptLetterFacade.now()));
  }

  @Override
  @GetMapping("/topics/default/messages")
  public ResponseEntity<BaseResponse<?>> getDefaultTopicMessages(
      @CurrentUserId Long userId,
      @RequestParam(required = false) Long cursor,
      @Min(1) @Max(100) @RequestParam(defaultValue = "20") int size) {
    return ResponseFactory.success(
        GET_TOPIC_MESSAGES,
        TopicMessagesResponse.of(soptLetterFacade.getDefaultTopicMessages(userId, cursor, size)));
  }

  @Override
  @GetMapping("/topics/{topicId}/messages")
  public ResponseEntity<BaseResponse<?>> getTopicMessages(
      @CurrentUserId Long userId,
      @PathVariable Long topicId,
      @RequestParam(required = false) Long cursor,
      @Min(1) @Max(100) @RequestParam(defaultValue = "20") int size) {
    return ResponseFactory.success(
        GET_TOPIC_MESSAGES,
        TopicMessagesResponse.of(soptLetterFacade.getTopicMessages(userId, topicId, cursor, size)));
  }

  @Override
  @GetMapping("/topics/{topicId}/messages/{messageId}")
  public ResponseEntity<BaseResponse<?>> getMessage(
      @CurrentUserId Long userId, @PathVariable Long topicId, @PathVariable Long messageId) {
    return ResponseFactory.success(
        GET_MESSAGE, MessageResponse.of(soptLetterFacade.getMessage(userId, topicId, messageId)));
  }

  @Override
  @PostMapping("/topics/{topicId}/messages")
  public ResponseEntity<BaseResponse<?>> writeMessage(
      @CurrentUserId Long userId,
      @PathVariable Long topicId,
      @Valid @RequestBody WriteMessageRequest request) {
    return ResponseFactory.success(
        WRITE_MESSAGE,
        MessageResponse.of(soptLetterFacade.createMessage(userId, topicId, request.content())));
  }

  @Override
  @PatchMapping("/topics/{topicId}/messages/{messageId}")
  public ResponseEntity<BaseResponse<?>> updateMessage(
      @CurrentUserId Long userId,
      @PathVariable Long topicId,
      @PathVariable Long messageId,
      @Valid @RequestBody WriteMessageRequest request) {
    return ResponseFactory.success(
        UPDATE_MESSAGE,
        MessageResponse.of(
            soptLetterFacade.updateMessage(userId, topicId, messageId, request.content())));
  }

  @Override
  @DeleteMapping("/topics/{topicId}/messages/{messageId}")
  public ResponseEntity<BaseResponse<?>> deleteMessage(
      @CurrentUserId Long userId, @PathVariable Long topicId, @PathVariable Long messageId) {
    soptLetterFacade.deleteMessage(userId, topicId, messageId);
    return ResponseFactory.success(DELETE_MESSAGE);
  }

  @Override
  @PostMapping("/topics/{topicId}/messages/{messageId}/likes")
  public ResponseEntity<BaseResponse<?>> addLike(
      @CurrentUserId Long userId, @PathVariable Long topicId, @PathVariable Long messageId) {
    soptLetterFacade.addLike(userId, topicId, messageId);
    return ResponseFactory.success(ADD_LIKE);
  }

  @Override
  @DeleteMapping("/topics/{topicId}/messages/{messageId}/likes")
  public ResponseEntity<BaseResponse<?>> removeLike(
      @CurrentUserId Long userId, @PathVariable Long topicId, @PathVariable Long messageId) {
    soptLetterFacade.removeLike(userId, topicId, messageId);
    return ResponseFactory.success(REMOVE_LIKE);
  }
}
