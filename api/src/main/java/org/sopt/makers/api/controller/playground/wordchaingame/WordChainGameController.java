package org.sopt.makers.api.controller.playground.wordchaingame;

import static org.sopt.makers.api.controller.playground.wordchaingame.WordChainGameSuccessCode.CREATE_GAME_ROOM;
import static org.sopt.makers.api.controller.playground.wordchaingame.WordChainGameSuccessCode.CREATE_WORD;
import static org.sopt.makers.api.controller.playground.wordchaingame.WordChainGameSuccessCode.GET_GAME_ROOMS;
import static org.sopt.makers.api.controller.playground.wordchaingame.WordChainGameSuccessCode.GET_WINNERS;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.common.util.InfiniteScrollUtil;
import org.sopt.makers.api.controller.playground.wordchaingame.dto.WordChainGameAllResponse;
import org.sopt.makers.api.controller.playground.wordchaingame.dto.WordChainGameGenerateRequest;
import org.sopt.makers.api.controller.playground.wordchaingame.dto.WordChainGameGenerateResponse;
import org.sopt.makers.api.controller.playground.wordchaingame.dto.WordChainGameRoomResponse;
import org.sopt.makers.api.controller.playground.wordchaingame.dto.WordChainGameWinnerAllResponse;
import org.sopt.makers.api.controller.playground.wordchaingame.dto.WordChainGameWinnerResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.playground.wordchaingame.service.WordChainGameService;
import org.sopt.makers.domain.playground.wordchaingame.service.WordChainGameService.RoomResult;
import org.sopt.makers.domain.playground.wordchaingame.service.WordChainGameService.WinnerResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chainWordGame")
public class WordChainGameController implements WordChainGameApi {

  private final WordChainGameService wordChainGameService;
  private final InfiniteScrollUtil infiniteScrollUtil;

  @Override
  @PostMapping("/wordGame")
  public ResponseEntity<BaseResponse<?>> createWord(
      @CurrentUserId Long userId, @RequestBody WordChainGameGenerateRequest request) {
    return ResponseFactory.success(
        CREATE_WORD,
        WordChainGameGenerateResponse.from(
            wordChainGameService.createWord(userId, request.roomId(), request.word())));
  }

  @Override
  @GetMapping("/gameRoom")
  public ResponseEntity<BaseResponse<?>> getAllGameRooms(
      @RequestParam(required = false) Integer limit, @RequestParam(required = false) Long cursor) {
    List<RoomResult> rooms =
        wordChainGameService.getAllRooms(infiniteScrollUtil.checkLimitForPagination(limit), cursor);
    boolean hasNext = infiniteScrollUtil.checkHasNextElement(limit, rooms);
    List<WordChainGameRoomResponse> responses =
        infiniteScrollUtil.removeNextElementIfExist(limit, rooms).stream()
            .map(WordChainGameRoomResponse::from)
            .toList();
    return ResponseFactory.success(
        GET_GAME_ROOMS, new WordChainGameAllResponse(responses, hasNext));
  }

  @Override
  @PostMapping("/newGame")
  public ResponseEntity<BaseResponse<?>> createGameRoom(@CurrentUserId Long userId) {
    return ResponseFactory.success(
        CREATE_GAME_ROOM,
        WordChainGameGenerateResponse.from(wordChainGameService.createWordGameRoom(userId)));
  }

  @Override
  @GetMapping("/winners")
  public ResponseEntity<BaseResponse<?>> getGameWinners(
      @RequestParam(required = false) Integer limit, @RequestParam(defaultValue = "0") int cursor) {
    List<WinnerResult> winners =
        wordChainGameService.getAllWinners(
            infiniteScrollUtil.checkLimitForPagination(limit), cursor);
    boolean hasNext = infiniteScrollUtil.checkHasNextElement(limit, winners);
    List<WordChainGameWinnerResponse> responses =
        infiniteScrollUtil.removeNextElementIfExist(limit, winners).stream()
            .map(WordChainGameWinnerResponse::from)
            .toList();
    return ResponseFactory.success(
        GET_WINNERS, new WordChainGameWinnerAllResponse(responses, hasNext));
  }
}
