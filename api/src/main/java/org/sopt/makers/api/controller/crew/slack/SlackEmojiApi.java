package org.sopt.makers.api.controller.crew.slack;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.crew.slack.dto.AddSlackEmojiRequest;
import org.sopt.makers.api.controller.crew.slack.dto.DeleteSlackEmojiRequest;
import org.sopt.makers.api.controller.crew.slack.dto.UpdateSlackEmojiRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "CREW Slack", description = "CREW Slack 호출 이모지 관리 API")
public interface SlackEmojiApi {

  @Operation(summary = "Slack 이모지 이벤트 생성")
  ResponseEntity<BaseResponse<?>> addEmoji(AddSlackEmojiRequest request);

  @Operation(summary = "Slack 이모지 이벤트 수정")
  ResponseEntity<BaseResponse<?>> updateEmoji(UpdateSlackEmojiRequest request);

  @Operation(summary = "Slack 이모지 이벤트 삭제")
  ResponseEntity<BaseResponse<?>> deleteEmoji(DeleteSlackEmojiRequest request);
}
