package org.sopt.makers.domain.crew.soptmap.service;

import static org.sopt.makers.domain.crew.soptmap.exception.SoptMapFailure.DUPLICATE_SOPT_MAP_PLACE;
import static org.sopt.makers.domain.crew.soptmap.exception.SoptMapFailure.INVALID_SOPT_MAP_EVENT;
import static org.sopt.makers.domain.crew.soptmap.exception.SoptMapFailure.INVALID_SOPT_MAP_VALUE;
import static org.sopt.makers.domain.crew.soptmap.exception.SoptMapFailure.NOT_FOUND_EVENT_GIFT;
import static org.sopt.makers.domain.crew.soptmap.exception.SoptMapFailure.NOT_FOUND_EVENT_POLICY;
import static org.sopt.makers.domain.crew.soptmap.exception.SoptMapFailure.NOT_FOUND_SOPT_MAP;

import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.pagination.PageQuery;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.crew.soptmap.EventGift;
import org.sopt.makers.domain.crew.soptmap.MapRecommend;
import org.sopt.makers.domain.crew.soptmap.MapTag;
import org.sopt.makers.domain.crew.soptmap.SoptMap;
import org.sopt.makers.domain.crew.soptmap.SoptMapEventPolicy;
import org.sopt.makers.domain.crew.soptmap.SoptMapSearchResult;
import org.sopt.makers.domain.crew.soptmap.SoptMapSortType;
import org.sopt.makers.domain.crew.soptmap.SoptMapUser;
import org.sopt.makers.domain.crew.soptmap.SubwayStation;
import org.sopt.makers.domain.crew.soptmap.exception.SoptMapException;
import org.sopt.makers.domain.crew.soptmap.port.EventGiftRepositoryPort;
import org.sopt.makers.domain.crew.soptmap.port.MapRecommendRepositoryPort;
import org.sopt.makers.domain.crew.soptmap.port.SoptMapEventPolicyPort;
import org.sopt.makers.domain.crew.soptmap.port.SoptMapRepositoryPort;
import org.sopt.makers.domain.crew.soptmap.port.SoptMapUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoptMapService {

  private static final int MAX_TAG_COUNT = 2;
  private static final int MAX_STATION_COUNT = 3;

  private final SoptMapRepositoryPort soptMapRepositoryPort;
  private final MapRecommendRepositoryPort recommendRepositoryPort;
  private final EventGiftRepositoryPort giftRepositoryPort;
  private final SoptMapEventPolicyPort eventPolicyPort;
  private final SoptMapUserPort userPort;
  private final SubwayStationService subwayStationService;

  @Transactional
  public CreatedSoptMap create(CreateSoptMapCommand command, Long userId) {
    validateUser(userId);
    validateValues(command.values(), command.stationNames());
    validateUniquePlaceName(command.values().placeName());
    boolean firstRegistered = !soptMapRepositoryPort.existsByCreatorId(userId);
    List<Long> stationIds = subwayStationService.resolveStationIds(command.stationNames());
    SoptMap saved =
        soptMapRepositoryPort.save(SoptMap.create(userId, command.values(), stationIds));
    return new CreatedSoptMap(saved.id(), firstRegistered);
  }

  @Transactional
  public Long update(Long soptMapId, UpdateSoptMapCommand command, Long userId) {
    validateValues(command.values(), command.stationNames());
    SoptMap soptMap = getSoptMap(soptMapId);
    soptMap.validateCreator(userId);
    if (!soptMap.placeName().equals(command.values().placeName())) {
      validateUniquePlaceName(command.values().placeName());
    }
    List<Long> stationIds = subwayStationService.resolveStationIds(command.stationNames());
    return soptMapRepositoryPort.save(soptMap.update(command.values(), stationIds)).id();
  }

  @Transactional
  public void delete(Long soptMapId, Long userId) {
    SoptMap soptMap = getSoptMap(soptMapId);
    soptMap.validateCreator(userId);
    recommendRepositoryPort.deleteAllBySoptMapId(soptMapId);
    soptMapRepositoryPort.delete(soptMap);
  }

  public PageResult<SoptMapView> search(
      Long userId,
      List<MapTag> mapTags,
      SoptMapSortType sortType,
      String stationKeyword,
      PageQuery pageQuery) {
    List<Long> stationIds = resolveSearchStationIds(stationKeyword);
    if (stationKeyword != null && !stationKeyword.isBlank() && stationIds.isEmpty()) {
      return emptyPage(pageQuery);
    }
    PageResult<SoptMapSearchResult> page =
        soptMapRepositoryPort.search(userId, mapTags, sortType, stationIds, pageQuery);
    return toViewPage(page, userId);
  }

  public SoptMapView getDetail(Long soptMapId, Long userId) {
    SoptMapSearchResult result =
        soptMapRepositoryPort
            .findDetail(userId, soptMapId)
            .orElseThrow(() -> new SoptMapException(NOT_FOUND_SOPT_MAP));
    return toViews(List.of(result), userId).getFirst();
  }

  public List<SubwayStation> searchStations(String keyword) {
    return subwayStationService.search(keyword);
  }

  @Transactional
  public ToggleRecommendResult toggleRecommend(Long soptMapId, Long userId) {
    if (!soptMapRepositoryPort.existsById(soptMapId)) {
      throw new SoptMapException(NOT_FOUND_SOPT_MAP);
    }
    MapRecommend recommend =
        recommendRepositoryPort
            .findByUserIdAndSoptMapId(userId, soptMapId)
            .map(MapRecommend::toggle)
            .orElseGet(() -> MapRecommend.create(userId, soptMapId));
    MapRecommend saved = recommendRepositoryPort.save(recommend);
    return new ToggleRecommendResult(saved.soptMapId(), saved.active());
  }

  @Transactional
  public boolean checkEventWinning(Long soptMapId, Long userId) {
    if (!soptMapRepositoryPort.existsByCreatorIdAndId(userId, soptMapId)) {
      throw new SoptMapException(INVALID_SOPT_MAP_EVENT);
    }
    SoptMapEventPolicy policy =
        eventPolicyPort
            .findPolicy()
            .orElseThrow(() -> new SoptMapException(NOT_FOUND_EVENT_POLICY));
    List<SoptMap> eventMaps =
        soptMapRepositoryPort.findCreatedBetween(
            policy.startDate().atStartOfDay(), policy.endDate().atTime(LocalTime.MAX));
    boolean winner =
        policy.winnerOrders().stream()
            .map(order -> order - 1)
            .filter(index -> index >= 0 && index < eventMaps.size())
            .map(eventMaps::get)
            .anyMatch(map -> map.id().equals(soptMapId) && map.creatorId().equals(userId));
    return winner && assignGift(userId, soptMapId);
  }

  @Transactional
  public GiftResult getGift(Long soptMapId, Long userId) {
    EventGift gift =
        giftRepositoryPort
            .findActiveByUserIdAndMapId(userId, soptMapId)
            .orElseThrow(() -> new SoptMapException(NOT_FOUND_EVENT_GIFT));
    giftRepositoryPort.save(gift.use());
    return new GiftResult(gift.id(), gift.giftUrl());
  }

  private boolean assignGift(Long userId, Long soptMapId) {
    if (giftRepositoryPort.existsByUserId(userId)) {
      return false;
    }
    return giftRepositoryPort
        .findFirstClaimableForUpdate()
        .map(gift -> giftRepositoryPort.save(gift.claim(userId, soptMapId)))
        .isPresent();
  }

  private PageResult<SoptMapView> toViewPage(PageResult<SoptMapSearchResult> page, Long userId) {
    List<SoptMapView> views = toViews(page.content(), userId);
    return new PageResult<>(
        views,
        page.totalElements(),
        page.totalPages(),
        page.page(),
        page.limit(),
        page.hasNext(),
        page.hasPrevious());
  }

  private List<SoptMapView> toViews(List<SoptMapSearchResult> results, Long userId) {
    Set<Long> stationIds =
        results.stream()
            .flatMap(result -> result.soptMap().nearbyStationIds().stream())
            .collect(Collectors.toCollection(LinkedHashSet::new));
    Map<Long, SubwayStation> stationMap =
        subwayStationService.getStationMap(List.copyOf(stationIds));
    List<Long> creatorIds =
        results.stream().map(result -> result.soptMap().creatorId()).distinct().toList();
    Map<Long, SoptMapUser> userMap =
        userPort.findAllByIds(creatorIds).stream()
            .collect(
                Collectors.toMap(
                    SoptMapUser::id,
                    Function.identity(),
                    (left, right) -> left,
                    LinkedHashMap::new));
    return results.stream().map(result -> toView(result, userId, stationMap, userMap)).toList();
  }

  private SoptMapView toView(
      SoptMapSearchResult result,
      Long userId,
      Map<Long, SubwayStation> stationMap,
      Map<Long, SoptMapUser> userMap) {
    SoptMap map = result.soptMap();
    List<String> stationNames =
        map.nearbyStationIds().stream()
            .map(stationMap::get)
            .filter(station -> station != null)
            .map(SubwayStation::name)
            .toList();
    SoptMapUser creator = userMap.get(map.creatorId());
    return new SoptMapView(
        map,
        stationNames,
        result.recommendCount(),
        result.isRecommended(),
        creator == null ? null : creator.name(),
        map.creatorId().equals(userId));
  }

  private List<Long> resolveSearchStationIds(String stationKeyword) {
    if (stationKeyword == null || stationKeyword.isBlank()) {
      return List.of();
    }
    return subwayStationService.search(stationKeyword).stream().map(SubwayStation::id).toList();
  }

  private PageResult<SoptMapView> emptyPage(PageQuery query) {
    return new PageResult<>(List.of(), 0, 0, query.page(), query.limit(), false, query.page() > 1);
  }

  private SoptMap getSoptMap(Long soptMapId) {
    return soptMapRepositoryPort
        .findById(soptMapId)
        .orElseThrow(() -> new SoptMapException(NOT_FOUND_SOPT_MAP));
  }

  private void validateUser(Long userId) {
    if (userPort.findById(userId).isEmpty()) {
      throw new SoptMapException(INVALID_SOPT_MAP_VALUE);
    }
  }

  private void validateUniquePlaceName(String placeName) {
    if (soptMapRepositoryPort.existsByPlaceName(placeName)) {
      throw new SoptMapException(DUPLICATE_SOPT_MAP_PLACE);
    }
  }

  private void validateValues(SoptMap.Values values, List<String> stationNames) {
    if (values == null
        || values.placeName() == null
        || values.placeName().isBlank()
        || values.description() == null
        || values.description().isBlank()
        || values.mapTags() == null
        || values.mapTags().size() > MAX_TAG_COUNT
        || stationNames == null
        || stationNames.size() > MAX_STATION_COUNT) {
      throw new SoptMapException(INVALID_SOPT_MAP_VALUE);
    }
  }

  public record CreateSoptMapCommand(SoptMap.Values values, List<String> stationNames) {}

  public record UpdateSoptMapCommand(SoptMap.Values values, List<String> stationNames) {}

  public record CreatedSoptMap(Long soptMapId, boolean firstRegistered) {}

  public record ToggleRecommendResult(Long soptMapId, boolean isRecommended) {}

  public record GiftResult(Long giftId, String giftUrl) {}

  public record SoptMapView(
      SoptMap soptMap,
      List<String> stationNames,
      long recommendCount,
      boolean isRecommended,
      String creatorName,
      boolean isCreator) {}
}
