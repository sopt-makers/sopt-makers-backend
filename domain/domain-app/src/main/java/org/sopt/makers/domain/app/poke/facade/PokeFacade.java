package org.sopt.makers.domain.app.poke.facade;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.poke.EachRelationFriendListData;
import org.sopt.makers.domain.app.poke.Friend;
import org.sopt.makers.domain.app.poke.FriendRecommendType;
import org.sopt.makers.domain.app.poke.Friendship;
import org.sopt.makers.domain.app.poke.PokeDetail;
import org.sopt.makers.domain.app.poke.PokeHistory;
import org.sopt.makers.domain.app.poke.PokeMessage;
import org.sopt.makers.domain.app.poke.PokeToMeHistoryData;
import org.sopt.makers.domain.app.poke.PokedUserInfo;
import org.sopt.makers.domain.app.poke.RecommendedFriends;
import org.sopt.makers.domain.app.poke.Relationship;
import org.sopt.makers.domain.app.poke.SimplePokeProfileData;
import org.sopt.makers.domain.app.poke.exception.PokeException;
import org.sopt.makers.domain.app.poke.exception.PokeFailure;
import org.sopt.makers.domain.app.poke.service.FriendRecommender;
import org.sopt.makers.domain.app.poke.service.FriendService;
import org.sopt.makers.domain.app.poke.service.PokeHistoryService;
import org.sopt.makers.domain.app.poke.service.PokeMessageService;
import org.sopt.makers.domain.app.poke.service.PokeService;
import org.sopt.makers.domain.user.PokeUserProfile;
import org.sopt.makers.domain.user.port.AppPokeUserPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PokeFacade {

  private final FriendService friendService;
  private final FriendRecommender friendRecommender;
  private final PokeService pokeService;
  private final PokeHistoryService pokeHistoryService;
  private final PokeMessageService pokeMessageService;
  private final AppPokeUserPort pokeUserPort;

  public List<PokeMessage> getPokingMessages(String type) {
    List<PokeMessage> messages =
        new ArrayList<>(pokeMessageService.pickRandomMessageByTypeOf(type));
    messages.add(pokeMessageService.getFixedMessage());
    return messages;
  }

  public String getPokingMessageHeader(String type) {
    return pokeMessageService.getMessagesHeaderComment(type);
  }

  public SimplePokeProfileData getRandomUnRepliedPokeMeHistory(Long userId) {
    return pokeHistoryService
        .getRandomUnRepliedPokeMeHistory(userId)
        .map(pokeHistory -> getPokeHistoryProfile(userId, pokeHistory.pokerId(), pokeHistory.id()))
        .orElse(null);
  }

  public PokeToMeHistoryData getAllPokeMeHistory(Long userId, Pageable pageable) {
    List<Long> pokeMeUserIds = pokeHistoryService.getPokeMeUserIds(userId);
    Set<Long> existingPokeMeUserIds = Set.copyOf(pokeUserPort.filterExisting(pokeMeUserIds));
    List<Long> latestHistoryIds =
        pokeMeUserIds.stream()
            .filter(existingPokeMeUserIds::contains)
            .map(
                pokeMeUserId ->
                    pokeHistoryService.getAllLatestPokeHistoryFromTo(pokeMeUserId, userId).stream()
                        .findFirst()
                        .map(PokeHistory::id)
                        .orElse(null))
            .filter(Objects::nonNull)
            .toList();

    Page<PokeHistory> pokedHistories =
        pokeHistoryService.getAllLatestPokeHistoryIn(latestHistoryIds, pageable);
    int totalPageSize = pokedHistories.getTotalPages();
    List<SimplePokeProfileData> pokeToMeHistories =
        pokedHistories.stream()
            .map(
                pokeHistory ->
                    getPokeHistoryProfile(userId, pokeHistory.pokerId(), pokeHistory.id()))
            .distinct()
            .toList();
    return new PokeToMeHistoryData(
        pokeToMeHistories, totalPageSize, pageable.getPageSize(), pokedHistories.getNumber());
  }

  @Transactional
  public Long pokeFriend(
      Long pokerUserId, Long pokedUserId, String pokeMessage, boolean isAnonymous) {
    if (Objects.equals(pokerUserId, pokedUserId)) {
      throw new PokeException(PokeFailure.SELF_POKE_NOT_ALLOWED);
    }
    if (!pokeUserPort.exists(pokedUserId)) {
      throw new PokeException(PokeFailure.NOT_FOUND_USER);
    }

    pokeHistoryService.checkDuplicate(pokerUserId, pokedUserId);
    PokeHistory newPoke = pokeService.poke(pokerUserId, pokedUserId, pokeMessage, isAnonymous);

    applyFriendship(pokerUserId, pokedUserId);
    return newPoke.id();
  }

  private void applyFriendship(Long pokerUserId, Long pokedUserId) {
    if (friendService.isFriendEachOther(pokerUserId, pokedUserId)) {
      friendService.applyPokeCount(pokerUserId, pokedUserId);
      return;
    }
    boolean userNotPokeBefore =
        pokeHistoryService.getAllOfPokeBetween(pokerUserId, pokedUserId).isEmpty();
    if (!userNotPokeBefore) {
      friendService.registerFriendshipOf(pokerUserId, pokedUserId);
    }
  }

  public List<SimplePokeProfileData> getFriend(Long userId) {
    // 나와 친구인 사용자들 중 랜덤으로 1명 뽑기
    Long friendId = friendService.getPokeFriendIdRandomly(userId);
    if (friendId == null || !pokeUserPort.exists(friendId)) {
      return List.of();
    }

    PokedUserInfo friendUserInfo = getFriendUserInfo(userId, friendId);
    return List.of(
        SimplePokeProfileData.of(
            friendUserInfo,
            "",
            false,
            getIsAlreadyPoke(userId, friendId, userId),
            getIsAnonymous(userId, friendId, userId)));
  }

  public List<SimplePokeProfileData> getTwoFriendByFriendship(Long userId, Friendship friendship) {
    List<Friend> friendsOfFriendship =
        friendService.findAllFriendsByFriendship(
            userId, friendship.getLowerLimit(), friendship.getUpperLimit());

    return friendsOfFriendship.stream()
        .map(friend -> getLatestPokeProfileWith(userId, friend))
        .filter(Objects::nonNull)
        .limit(2)
        .toList();
  }

  public int getFriendSizeByFriendship(Long userId, Friendship friendship) {
    return friendService
        .findAllFriendsByFriendship(userId, friendship.getLowerLimit(), friendship.getUpperLimit())
        .size();
  }

  public EachRelationFriendListData getAllFriendByFriendship(
      Long userId, Friendship friendship, Pageable pageable) {
    Page<Friend> friends =
        friendService.findAllFriendsByFriendship(
            userId, friendship.getLowerLimit(), friendship.getUpperLimit(), pageable);
    Set<Long> existingFriendIds =
        Set.copyOf(
            pokeUserPort.filterExisting(
                friends.getContent().stream().map(Friend::friendUserId).toList()));

    List<SimplePokeProfileData> allOfPokeWithFriends =
        friends.getContent().stream()
            .filter(friend -> existingFriendIds.contains(friend.friendUserId()))
            .map(friend -> getLatestPokeProfileWith(userId, friend))
            .filter(Objects::nonNull)
            .toList();

    int totalSize =
        friendService.findAllFriendSizeByFriendship(
            userId, friendship.getLowerLimit(), friendship.getUpperLimit());
    int totalPageSize = Math.ceilDiv(totalSize, pageable.getPageSize());
    return new EachRelationFriendListData(
        allOfPokeWithFriends,
        totalSize,
        totalPageSize,
        pageable.getPageSize(),
        friends.getNumber());
  }

  public SimplePokeProfileData getPokeHistoryProfile(Long userId, Long friendId, Long pokeId) {
    PokeDetail pokeDetail = pokeService.getPokeDetail(pokeId);
    PokedUserInfo friendUserInfo = getFriendUserInfo(userId, friendId);

    return SimplePokeProfileData.of(
        friendUserInfo,
        pokeDetail.message(),
        friendUserInfo.isFirstMeet(),
        getIsAlreadyPoke(pokeDetail.pokerId(), pokeDetail.pokedId(), userId),
        getIsAnonymous(pokeDetail.pokerId(), pokeDetail.pokedId(), userId));
  }

  public RecommendedFriends getRecommendedFriendsByTypeList(
      List<FriendRecommendType> typeList, int size, Long userId) {
    return friendRecommender.recommendFriendsByTypeList(typeList, size, userId);
  }

  public boolean getIsNewUser(Long userId) {
    return friendService.getIsNewUser(userId);
  }

  public Long getUserPokeCount(Long userId) {
    return pokeService.getUserPokeCount(userId);
  }

  private SimplePokeProfileData getLatestPokeProfileWith(Long userId, Friend friend) {
    return pokeHistoryService.getAllOfPokeBetween(friend.userId(), friend.friendUserId()).stream()
        .findFirst()
        .map(poke -> getPokeHistoryProfile(userId, friend.friendUserId(), poke.id()))
        .orElse(null);
  }

  private PokedUserInfo getFriendUserInfo(Long userId, Long friendUserId) {
    PokeUserProfile profile = pokeUserPort.findProfile(friendUserId);
    List<Long> mutualFriendIds = friendService.getMutualFriendIds(userId, friendUserId);
    List<String> mutualFriendNames =
        mutualFriendIds.isEmpty()
            ? List.of()
            : pokeUserPort.findProfiles(mutualFriendIds).stream()
                .map(PokeUserProfile::name)
                .toList();
    Relationship relationInfo = friendService.getRelationInfo(userId, friendUserId);

    return new PokedUserInfo(
        friendUserId,
        profile.name(),
        profile.profileImage(),
        profile.generation(),
        profile.part(),
        relationInfo,
        mutualFriendNames);
  }

  private boolean getIsAlreadyPoke(Long pokerId, Long pokedId, Long userId) {
    return pokeHistoryService.getAllPokeHistoryByUsers(pokerId, pokedId).stream()
        .filter(pokeHistory -> pokeHistory.pokerId().equals(userId))
        .anyMatch(pokeHistory -> !pokeHistory.isReply());
  }

  private boolean getIsAnonymous(Long pokerId, Long pokedId, Long userId) {
    return pokeHistoryService.getAllPokeHistoryByUsers(pokerId, pokedId).stream()
        .filter(pokeHistory -> pokeHistory.pokedId().equals(userId))
        .max(Comparator.comparing(PokeHistory::createdAt))
        .map(PokeHistory::isAnonymous)
        .orElse(false);
  }
}
