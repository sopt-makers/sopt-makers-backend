package org.sopt.makers.api.controller.playground.wordchaingame;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.playground.wordchaingame.dto.WordChainGameGenerateRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "끝말잇기 게임 API", description = "끝말잇기 게임과 관련 API들")
@SecurityRequirement(name = "Authorization")
public interface WordChainGameApi {

  @Operation(summary = "단어 보내기")
  ResponseEntity<BaseResponse<?>> createWord(Long userId, WordChainGameGenerateRequest request);

  @Operation(summary = "게임 전체 조회", description = "cursor: 처음에는 null 또는 0, 이후 마지막 room의 id")
  ResponseEntity<BaseResponse<?>> getAllGameRooms(Integer limit, Long cursor);

  @Operation(summary = "새 게임 생성")
  ResponseEntity<BaseResponse<?>> createGameRoom(Long userId);

  @Operation(summary = "명예의 전당 목록")
  ResponseEntity<BaseResponse<?>> getGameWinners(Integer limit, int cursor);
}
