package org.sopt.makers.api.controller.app.poke;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.sopt.makers.api.controller.app.poke.dto.PokeMessageRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.poke.FriendRecommendType;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "찌르기", description = "앱 콕 찌르기 API")
public interface PokeApi {

  @Operation(summary = "신규 유저인지 조회")
  ResponseEntity<BaseResponse<?>> getIsNewUser(@Parameter(hidden = true) Long userId);

  @Operation(summary = "찌르기 메시지 조회")
  ResponseEntity<BaseResponse<?>> getPokeMessages(String messageType);

  @Operation(summary = "찌르기")
  ResponseEntity<BaseResponse<?>> pokeFriend(
      @Parameter(hidden = true) Long userId, Long pokedUserId, PokeMessageRequest request);

  @Operation(summary = "친구를 찔러보세요 조회")
  ResponseEntity<BaseResponse<?>> getFriend(@Parameter(hidden = true) Long userId);

  @Operation(summary = "누가 나를 찔렀어요 조회 - 단일 랜덤")
  ResponseEntity<BaseResponse<?>> getRandomUnRepliedPokeMe(@Parameter(hidden = true) Long userId);

  @Operation(summary = "누가 나를 찔렀어요 조회 - 리스트")
  ResponseEntity<BaseResponse<?>> getAllOfPokeMe(
      @Parameter(hidden = true) Long userId, Pageable pageable);

  @Operation(summary = "친구 조회 - 리스트 (전체 카테고리)")
  ResponseEntity<BaseResponse<?>> getFriendsForEachRelation(
      @Parameter(hidden = true) Long userId, String type, Pageable pageable);

  @Operation(summary = "친구 추천 통합 API")
  ResponseEntity<BaseResponse<?>> getRandomFriendsByFriendRecommendType(
      @Parameter(hidden = true) Long userId, List<FriendRecommendType> typeList, int size);
}
