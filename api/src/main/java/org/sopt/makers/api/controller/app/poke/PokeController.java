package org.sopt.makers.api.controller.app.poke;

import static org.sopt.makers.api.controller.app.poke.PokeSuccessCode.GET_ALL_POKE_ME;
import static org.sopt.makers.api.controller.app.poke.PokeSuccessCode.GET_FRIEND;
import static org.sopt.makers.api.controller.app.poke.PokeSuccessCode.GET_FRIEND_LIST;
import static org.sopt.makers.api.controller.app.poke.PokeSuccessCode.GET_IS_NEW_USER;
import static org.sopt.makers.api.controller.app.poke.PokeSuccessCode.GET_POKE_MESSAGES;
import static org.sopt.makers.api.controller.app.poke.PokeSuccessCode.GET_RANDOM_UNREPLIED_POKE_ME;
import static org.sopt.makers.api.controller.app.poke.PokeSuccessCode.GET_RECOMMENDED_FRIENDS;
import static org.sopt.makers.api.controller.app.poke.PokeSuccessCode.POKE_FRIEND;
import static org.sopt.makers.domain.app.poke.Friendship.BEST_FRIEND;
import static org.sopt.makers.domain.app.poke.Friendship.NEW_FRIEND;
import static org.sopt.makers.domain.app.poke.Friendship.SOULMATE;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.app.poke.dto.AllRelationFriendList;
import org.sopt.makers.api.controller.app.poke.dto.EachRelationFriendList;
import org.sopt.makers.api.controller.app.poke.dto.IsNew;
import org.sopt.makers.api.controller.app.poke.dto.PokeMessageList;
import org.sopt.makers.api.controller.app.poke.dto.PokeMessageRequest;
import org.sopt.makers.api.controller.app.poke.dto.PokeToMeHistoryList;
import org.sopt.makers.api.controller.app.poke.dto.RecommendedFriendsRequest;
import org.sopt.makers.api.controller.app.poke.dto.SimplePokeProfile;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.poke.FriendRecommendType;
import org.sopt.makers.domain.app.poke.Friendship;
import org.sopt.makers.domain.app.poke.SimplePokeProfileData;
import org.sopt.makers.domain.app.poke.facade.PokeFacade;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/poke")
@RequiredArgsConstructor
public class PokeController implements PokeApi {

  private final PokeFacade pokeFacade;

  @Override
  @GetMapping("/new")
  public ResponseEntity<BaseResponse<?>> getIsNewUser(@CurrentUserId Long userId) {
    return ResponseFactory.success(GET_IS_NEW_USER, new IsNew(pokeFacade.getIsNewUser(userId)));
  }

  @Override
  @GetMapping("/message")
  public ResponseEntity<BaseResponse<?>> getPokeMessages(
      @RequestParam("messageType") String messageType) {
    return ResponseFactory.success(
        GET_POKE_MESSAGES,
        PokeMessageList.of(
            pokeFacade.getPokingMessageHeader(messageType),
            pokeFacade.getPokingMessages(messageType)));
  }

  @Override
  @PutMapping("/{userId}")
  public ResponseEntity<BaseResponse<?>> pokeFriend(
      @CurrentUserId Long userId,
      @PathVariable("userId") Long pokedUserId,
      @RequestBody PokeMessageRequest request) {
    Long pokeHistoryId =
        pokeFacade.pokeFriend(userId, pokedUserId, request.message(), request.isAnonymous());
    return ResponseFactory.success(
        POKE_FRIEND,
        SimplePokeProfile.of(pokeFacade.getPokeHistoryProfile(userId, pokedUserId, pokeHistoryId)));
  }

  @Override
  @GetMapping("/friend")
  public ResponseEntity<BaseResponse<?>> getFriend(@CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_FRIEND, pokeFacade.getFriend(userId).stream().map(SimplePokeProfile::of).toList());
  }

  @Override
  @GetMapping("/to/me")
  public ResponseEntity<BaseResponse<?>> getRandomUnRepliedPokeMe(@CurrentUserId Long userId) {
    SimplePokeProfileData history = pokeFacade.getRandomUnRepliedPokeMeHistory(userId);
    return ResponseFactory.success(
        GET_RANDOM_UNREPLIED_POKE_ME,
        Objects.isNull(history) ? null : SimplePokeProfile.of(history));
  }

  @Override
  @GetMapping("/to/me/list")
  public ResponseEntity<BaseResponse<?>> getAllOfPokeMe(
      @CurrentUserId Long userId,
      @PageableDefault(size = 25, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return ResponseFactory.success(
        GET_ALL_POKE_ME, PokeToMeHistoryList.of(pokeFacade.getAllPokeMeHistory(userId, pageable)));
  }

  @Override
  @GetMapping("/friend/list")
  public ResponseEntity<BaseResponse<?>> getFriendsForEachRelation(
      @CurrentUserId Long userId,
      @RequestParam(value = "type", required = false) String type,
      @PageableDefault(size = 25) Pageable pageable) {
    if (Objects.isNull(type)) {
      return ResponseFactory.success(GET_FRIEND_LIST, getAllRelationFriendList(userId));
    }
    Friendship targetFriendship = Friendship.getFriendshipByValue(type);
    return ResponseFactory.success(
        GET_FRIEND_LIST,
        EachRelationFriendList.of(
            pokeFacade.getAllFriendByFriendship(userId, targetFriendship, pageable)));
  }

  @Override
  @GetMapping("/random")
  public ResponseEntity<BaseResponse<?>> getRandomFriendsByFriendRecommendType(
      @CurrentUserId Long userId,
      @RequestParam(value = "randomType", required = false) List<FriendRecommendType> typeList,
      @RequestParam("size") int size) {
    return ResponseFactory.success(
        GET_RECOMMENDED_FRIENDS,
        RecommendedFriendsRequest.of(
            pokeFacade.getRecommendedFriendsByTypeList(typeList, size, userId)));
  }

  private AllRelationFriendList getAllRelationFriendList(Long userId) {
    return AllRelationFriendList.of(
        pokeFacade.getTwoFriendByFriendship(userId, NEW_FRIEND),
        pokeFacade.getFriendSizeByFriendship(userId, NEW_FRIEND),
        pokeFacade.getTwoFriendByFriendship(userId, BEST_FRIEND),
        pokeFacade.getFriendSizeByFriendship(userId, BEST_FRIEND),
        pokeFacade.getTwoFriendByFriendship(userId, SOULMATE),
        pokeFacade.getFriendSizeByFriendship(userId, SOULMATE));
  }
}
