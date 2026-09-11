package org.sopt.makers.api.controller.app.poke;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.sopt.makers.api.controller.app.poke.dto.IsNew;
import org.sopt.makers.api.controller.app.poke.dto.PokeMessageList;
import org.sopt.makers.api.controller.app.poke.dto.PokeMessageRequest;
import org.sopt.makers.api.controller.app.poke.dto.PokeToMeHistoryList;
import org.sopt.makers.api.controller.app.poke.dto.RecommendedFriendsRequest;
import org.sopt.makers.api.controller.app.poke.dto.SimplePokeProfile;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.poke.FriendRecommendType;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "찌르기", description = "앱 콕 찌르기 API")
public interface PokeApi {

  @Operation(summary = "신규 유저인지 조회")
  @ApiResponse(responseCode = "200", description = "신규 유저 여부 조회에 성공했습니다.")
  ResponseEntity<BaseResponse<IsNew>> getIsNewUser(@Parameter(hidden = true) Long userId);

  @Operation(summary = "찌르기 메시지 조회")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "찌르기 메시지 조회에 성공했습니다."),
    @ApiResponse(
        responseCode = "404",
        description = "해당 찌르기 메시지 타입은 존재하지 않습니다.",
        content = @Content)
  })
  ResponseEntity<BaseResponse<PokeMessageList>> getPokeMessages(
      @Parameter(description = "찌르기 메시지 분류", example = "pokeSomeone") String messageType);

  @Operation(summary = "찌르기")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "찌르기에 성공했습니다."),
    @ApiResponse(responseCode = "400", description = "본인을 찌를 수 없습니다.", content = @Content),
    @ApiResponse(
        responseCode = "404",
        description = "존재하지 않는 유저이거나, 찌르기 내역을 찾을 수 없습니다.",
        content = @Content),
    @ApiResponse(responseCode = "409", description = "이미 찌르기를 보낸 친구입니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<SimplePokeProfile>> pokeFriend(
      @Parameter(hidden = true) Long userId,
      @Parameter(description = "찌를 상대 유저 아이디", example = "2") Long pokedUserId,
      PokeMessageRequest request);

  @Operation(summary = "친구를 찔러보세요 조회")
  @ApiResponse(responseCode = "200", description = "찔러볼 친구 조회에 성공했습니다.")
  ResponseEntity<BaseResponse<List<SimplePokeProfile>>> getFriend(
      @Parameter(hidden = true) Long userId);

  @Operation(
      summary = "누가 나를 찔렀어요 조회 - 단일 랜덤",
      description = "답장하지 않은 찌르기 내역이 없으면 data가 null인 200을 준다.")
  @ApiResponse(responseCode = "200", description = "나를 찌른 친구 단일 조회에 성공했습니다.")
  ResponseEntity<BaseResponse<SimplePokeProfile>> getRandomUnRepliedPokeMe(
      @Parameter(hidden = true) Long userId);

  @Operation(summary = "누가 나를 찔렀어요 조회 - 리스트", description = "최신순으로 고정된다.")
  @ApiResponse(responseCode = "200", description = "나를 찌른 친구 목록 조회에 성공했습니다.")
  ResponseEntity<BaseResponse<PokeToMeHistoryList>> getAllOfPokeMe(
      @Parameter(hidden = true) Long userId, Pageable pageable);

  @Operation(
      summary = "친구 조회 - 리스트 (전체 카테고리)",
      description =
          "type을 주지 않으면 관계별로 묶은 형태로 주고(newFriend, bestFriend, soulmate와 각 전체 수, totalSize), "
              + "주면 그 관계만 페이지 형태로 준다(friendList, totalSize, totalPageSize, pageSize, pageNum). "
              + "두 형태로 갈려 아래 응답 스키마에는 나오지 않는다. "
              + "정렬은 콕 횟수 내림차순, 같으면 friendUserId 오름차순으로 고정된다.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "친구 목록 조회에 성공했습니다."),
    @ApiResponse(responseCode = "404", description = "해당 친구관계는 존재하지 않습니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<?>> getFriendsForEachRelation(
      @Parameter(hidden = true) Long userId,
      @Parameter(
              description =
                  "친구 관계. nonfriend, new, bestfriend, soulmate 가운데 하나. 생략하면 전체 관계를 묶어서 준다",
              example = "new")
          String type,
      Pageable pageable);

  @Operation(summary = "친구 추천 통합 API")
  @ApiResponse(responseCode = "200", description = "친구 추천 조회에 성공했습니다.")
  ResponseEntity<BaseResponse<RecommendedFriendsRequest>> getRandomFriendsByFriendRecommendType(
      @Parameter(hidden = true) Long userId,
      @Parameter(description = "추천 분류 목록") List<FriendRecommendType> typeList,
      @Parameter(description = "분류마다 뽑을 인원 수", example = "2") int size);
}
