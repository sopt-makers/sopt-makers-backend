package org.sopt.makers.api.controller.crew.slack;

import static org.sopt.makers.api.controller.crew.slack.SlackEmojiSuccessCode.ADD_SLACK_EMOJI;
import static org.sopt.makers.api.controller.crew.slack.SlackEmojiSuccessCode.DELETE_SLACK_EMOJI;
import static org.sopt.makers.api.controller.crew.slack.SlackEmojiSuccessCode.UPDATE_SLACK_EMOJI;
import static org.sopt.makers.domain.crew.slack.exception.SlackEmojiFailure.INVALID_API_AUTH_TOKEN;

import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.crew.slack.dto.AddSlackEmojiRequest;
import org.sopt.makers.api.controller.crew.slack.dto.DeleteSlackEmojiRequest;
import org.sopt.makers.api.controller.crew.slack.dto.UpdateSlackEmojiRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.crew.slack.exception.SlackEmojiException;
import org.sopt.makers.domain.crew.slack.service.SlackEmojiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("dev")
@RestController
@RequestMapping("/slack/emoji")
@RequiredArgsConstructor
public class SlackEmojiController implements SlackEmojiApi {

  private final SlackEmojiService slackEmojiService;

  @Value("${crew.slack.api-auth-token:}")
  private String apiAuthToken;

  @Override
  @PostMapping
  public ResponseEntity<BaseResponse<?>> addEmoji(
      @Valid @RequestBody AddSlackEmojiRequest request) {
    validateToken(request.identifiedPwd());
    slackEmojiService.addMapping(request.toCommand());
    return ResponseFactory.success(ADD_SLACK_EMOJI);
  }

  @Override
  @PatchMapping
  public ResponseEntity<BaseResponse<?>> updateEmoji(
      @Valid @RequestBody UpdateSlackEmojiRequest request) {
    validateToken(request.identifiedPwd());
    slackEmojiService.updateCallEmoji(request.originalCallEmoji(), request.updateCallEmoji());
    return ResponseFactory.success(UPDATE_SLACK_EMOJI);
  }

  @Override
  @DeleteMapping
  public ResponseEntity<BaseResponse<?>> deleteEmoji(
      @Valid @RequestBody DeleteSlackEmojiRequest request) {
    validateToken(request.identifiedPwd());
    slackEmojiService.deleteCallEmoji(request.callEmoji());
    return ResponseFactory.success(DELETE_SLACK_EMOJI);
  }

  private void validateToken(String providedToken) {
    if (apiAuthToken == null
        || apiAuthToken.isBlank()
        || providedToken == null
        || !MessageDigest.isEqual(
            apiAuthToken.getBytes(StandardCharsets.UTF_8),
            providedToken.getBytes(StandardCharsets.UTF_8))) {
      throw new SlackEmojiException(INVALID_API_AUTH_TOKEN);
    }
  }
}
