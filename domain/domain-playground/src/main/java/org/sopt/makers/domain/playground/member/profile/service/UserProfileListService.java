package org.sopt.makers.domain.playground.member.profile.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.playground.member.ask.AskPreview;
import org.sopt.makers.domain.playground.member.ask.service.UserAskQueryService;
import org.sopt.makers.domain.playground.member.profile.UserProfileListItem;
import org.sopt.makers.domain.playground.member.profile.UserProfileListResult;
import org.sopt.makers.domain.playground.member.profile.UserProfileRanking;
import org.sopt.makers.domain.playground.member.profile.port.CoffeeChatActivationPort;
import org.sopt.makers.domain.playground.member.profile.port.UserProfileCardCachePort;
import org.sopt.makers.domain.playground.member.profile.port.UserProfileRankingCachePort;
import org.sopt.makers.domain.playground.member.profile.service.sorting.UserProfileFilter;
import org.sopt.makers.domain.playground.member.profile.service.sorting.UserSortingService;
import org.sopt.makers.domain.playground.member.profile.service.sorting.ProfileOrderBy;
import org.sopt.makers.domain.playground.member.profile.service.sorting.ProfileTeamFilter;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.port.PlaygroundProfileUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** `GET /api/v1/members/profile` 목록 조회. N+1 차단을 위해 후보 ID → 벌크 로드 → 인메모리 필터/정렬 순서로 처리한다. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileListService {

  private static final int DEFAULT_LIMIT = 30;
  private static final int TOP_RANK_SIZE = 50;
  private static final int EMPLOYED_TRUE = 1;

  private final PlaygroundProfileUserPort playgroundProfileUserPort;
  private final CoffeeChatActivationPort coffeeChatActivationPort;
  private final UserAskQueryService userAskQueryService;
  private final UserSortingService memberSortingService;
  private final UserProfileRankingCachePort rankingCachePort;
  private final UserProfileCardCachePort cardCachePort;

  public UserProfileListResult getProfiles(
      Integer filter,
      Integer limit,
      Integer offset,
      String search,
      Integer generation,
      Integer employed,
      Integer orderBy,
      String mbti,
      String team) {

    int offsetValue = (offset == null || offset < 0) ? 0 : offset;
    int limitValue = (limit == null || limit <= 0) ? DEFAULT_LIMIT : limit;

    boolean cacheable =
        isBlank(search)
            && filter == null
            && generation == null
            && employed == null
            && isBlank(mbti)
            && isBlank(team)
            && orderBy == null
            && (offsetValue + limitValue) <= TOP_RANK_SIZE;

    if (cacheable) {
      return getCachedDefaultPage(offsetValue, limitValue);
    }
    return getLivePage(filter, limitValue, offsetValue, search, generation, employed, orderBy, mbti, team);
  }

  private UserProfileListResult getCachedDefaultPage(int offset, int limit) {
    UserProfileRanking ranking = rankingCachePort.getTopRanking().orElseGet(this::recomputeAndCacheRanking);

    List<Long> pageIds = sliceIds(ranking.topUserIds(), offset, limit);
    if (pageIds.isEmpty()) {
      return UserProfileListResult.empty();
    }

    List<User> users = loadUsersWithCardCache(pageIds);
    boolean hasNext = (offset + limit) < ranking.totalCount();
    return buildResult(orderByIds(users, pageIds), hasNext, ranking.totalCount());
  }

  private UserProfileRanking recomputeAndCacheRanking() {
    List<Long> candidateIds = playgroundProfileUserPort.findCandidateUserIds(null, null);
    if (candidateIds.isEmpty()) {
      UserProfileRanking ranking = new UserProfileRanking(List.of(), 0);
      rankingCachePort.putTopRanking(ranking);
      return ranking;
    }

    List<User> users = playgroundProfileUserPort.findAllWithActivitiesByIds(candidateIds);
    List<User> sorted = users.stream().sorted(memberSortingService.createComparator(null, null)).toList();
    List<User> top = sorted.stream().limit(TOP_RANK_SIZE).toList();

    top.forEach(cardCachePort::put);

    UserProfileRanking ranking = new UserProfileRanking(top.stream().map(User::id).toList(), sorted.size());
    rankingCachePort.putTopRanking(ranking);
    return ranking;
  }

  private List<User> loadUsersWithCardCache(List<Long> ids) {
    Map<Long, User> cached = cardCachePort.getAllPresent(ids);
    List<Long> missingIds = ids.stream().filter(id -> !cached.containsKey(id)).toList();
    if (missingIds.isEmpty()) {
      return ids.stream().map(cached::get).toList();
    }

    List<User> loaded = playgroundProfileUserPort.findAllWithActivitiesByIds(missingIds);
    loaded.forEach(cardCachePort::put);
    Map<Long, User> merged = new HashMap<>(cached);
    loaded.forEach(u -> merged.put(u.id(), u));
    return ids.stream().map(merged::get).filter(Objects::nonNull).toList();
  }

  private UserProfileListResult getLivePage(
      Integer filter,
      int limitValue,
      int offsetValue,
      String search,
      Integer generation,
      Integer employed,
      Integer orderByCode,
      String mbti,
      String team) {

    Boolean employedFilter = employed == null ? null : employed == EMPLOYED_TRUE;
    List<Long> candidateIds = playgroundProfileUserPort.findCandidateUserIds(mbti, employedFilter);
    if (candidateIds.isEmpty()) {
      return UserProfileListResult.empty();
    }

    List<User> users = playgroundProfileUserPort.findAllWithActivitiesByIds(candidateIds);

    Part partFilter = UserProfileFilter.resolvePartFilter(filter);
    ProfileTeamFilter teamFilter = ProfileTeamFilter.fromRawCode(team);
    List<User> filteredByActivity =
        users.stream()
            .filter(u -> UserProfileFilter.matchesActivityConditions(u, partFilter, teamFilter, generation))
            .toList();
    if (filteredByActivity.isEmpty()) {
      return UserProfileListResult.empty();
    }

    List<User> filteredBySearch =
        filteredByActivity.stream().filter(u -> UserProfileFilter.matchesSearch(u, search)).toList();
    if (filteredBySearch.isEmpty()) {
      return UserProfileListResult.empty();
    }

    ProfileOrderBy orderBy = ProfileOrderBy.fromCode(orderByCode);
    List<User> sortedUsers =
        filteredBySearch.stream()
            .sorted(
                orderBy != null
                    ? memberSortingService.createComparatorByOrderCondition(orderBy, employed)
                    : memberSortingService.createComparator(employed, UserProfileFilter.toSortingTeam(teamFilter)))
            .toList();

    List<User> pagedUsers = sortedUsers.stream().skip(offsetValue).limit(limitValue).toList();
    if (pagedUsers.isEmpty()) {
      return UserProfileListResult.empty();
    }

    boolean hasNext = (offsetValue + limitValue) < sortedUsers.size();
    return buildResult(pagedUsers, hasNext, sortedUsers.size());
  }

  private UserProfileListResult buildResult(List<User> pagedUsers, boolean hasNext, int totalCount) {
    List<Long> pagedIds = pagedUsers.stream().map(User::id).toList();
    Set<Long> activeCoffeeChatIds = coffeeChatActivationPort.findActiveUserIds(pagedIds);
    Map<Long, AskPreview> previewByReceiverId = userAskQueryService.findRecentAskPreviews(pagedIds);

    List<UserProfileListItem> items =
        pagedUsers.stream()
            .map(
                user ->
                    new UserProfileListItem(
                        user, activeCoffeeChatIds.contains(user.id()), previewByReceiverId.get(user.id())))
            .toList();

    return new UserProfileListResult(items, hasNext, totalCount);
  }

  private List<Long> sliceIds(List<Long> ids, int offset, int limit) {
    if (offset >= ids.size()) {
      return List.of();
    }
    return ids.subList(offset, Math.min(offset + limit, ids.size()));
  }

  private List<User> orderByIds(List<User> users, List<Long> ids) {
    Map<Long, User> byId = users.stream().collect(Collectors.toMap(User::id, u -> u));
    return ids.stream().map(byId::get).filter(Objects::nonNull).toList();
  }

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
