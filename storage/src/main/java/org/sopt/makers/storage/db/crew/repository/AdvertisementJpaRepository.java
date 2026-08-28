package org.sopt.makers.storage.db.crew.repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.crew.advertisement.AdvertisementCategory;
import org.sopt.makers.storage.db.crew.entity.AdvertisementEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdvertisementJpaRepository extends JpaRepository<AdvertisementEntity, Integer> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
      SELECT advertisement FROM AdvertisementEntity advertisement
      WHERE advertisement.category = :category
      ORDER BY advertisement.id ASC
      """)
  List<AdvertisementEntity> findAllByCategoryForUpdate(
      @Param("category") AdvertisementCategory category);

  @Query(
      """
      SELECT advertisement FROM AdvertisementEntity advertisement
      WHERE advertisement.sponsoredContent = true
        AND advertisement.category = :category
        AND advertisement.startDate <= :now
        AND advertisement.endDate >= :now
      ORDER BY advertisement.priority ASC
      """)
  List<AdvertisementEntity> findSponsoredInPeriod(
      @Param("category") AdvertisementCategory category,
      @Param("now") LocalDateTime now,
      Pageable pageable);

  @Query(
      """
      SELECT advertisement FROM AdvertisementEntity advertisement
      WHERE advertisement.sponsoredContent = false
        AND advertisement.category = :category
      ORDER BY advertisement.priority ASC
      """)
  List<AdvertisementEntity> findDefault(
      @Param("category") AdvertisementCategory category, Pageable pageable);

  @Query(
      """
      SELECT advertisement FROM AdvertisementEntity advertisement
      WHERE advertisement.category = :category
        AND advertisement.display = true
        AND advertisement.startDate <= :now
        AND advertisement.endDate >= :now
      ORDER BY advertisement.priority ASC
      """)
  List<AdvertisementEntity> findDisplayedMeetingTop(
      @Param("category") AdvertisementCategory category, @Param("now") LocalDateTime now);

  boolean existsByCategoryAndDisplayTrueAndIdNot(
      AdvertisementCategory category, Integer advertisementId);
}
