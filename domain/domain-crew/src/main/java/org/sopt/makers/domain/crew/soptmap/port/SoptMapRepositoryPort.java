package org.sopt.makers.domain.crew.soptmap.port;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.core.pagination.PageQuery;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.crew.soptmap.MapTag;
import org.sopt.makers.domain.crew.soptmap.SoptMap;
import org.sopt.makers.domain.crew.soptmap.SoptMapSearchResult;
import org.sopt.makers.domain.crew.soptmap.SoptMapSortType;

public interface SoptMapRepositoryPort {

  SoptMap save(SoptMap soptMap);

  Optional<SoptMap> findById(Long soptMapId);

  Optional<SoptMapSearchResult> findDetail(Long userId, Long soptMapId);

  PageResult<SoptMapSearchResult> search(
      Long userId,
      List<MapTag> mapTags,
      SoptMapSortType sortType,
      List<Long> stationIds,
      PageQuery pageQuery);

  boolean existsById(Long soptMapId);

  boolean existsByPlaceName(String placeName);

  boolean existsByCreatorId(Long creatorId);

  boolean existsByCreatorIdAndId(Long creatorId, Long soptMapId);

  List<SoptMap> findCreatedBetween(LocalDateTime startAt, LocalDateTime endAt);

  void delete(SoptMap soptMap);
}
