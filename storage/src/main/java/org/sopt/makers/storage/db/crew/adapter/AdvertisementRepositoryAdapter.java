package org.sopt.makers.storage.db.crew.adapter;

import static org.sopt.makers.domain.crew.advertisement.AdvertisementCategory.MEETING_TOP;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.advertisement.Advertisement;
import org.sopt.makers.domain.crew.advertisement.AdvertisementCategory;
import org.sopt.makers.domain.crew.advertisement.port.AdvertisementRepositoryPort;
import org.sopt.makers.storage.db.crew.entity.AdvertisementEntity;
import org.sopt.makers.storage.db.crew.repository.AdvertisementJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdvertisementRepositoryAdapter implements AdvertisementRepositoryPort {

  private final AdvertisementJpaRepository advertisementJpaRepository;

  @Override
  public Optional<Advertisement> findById(Integer advertisementId) {
    return advertisementJpaRepository.findById(advertisementId).map(AdvertisementEntity::toDomain);
  }

  @Override
  public void lockAllByCategory(AdvertisementCategory category) {
    advertisementJpaRepository.findAllByCategoryForUpdate(category);
  }

  @Override
  public List<Advertisement> findSponsoredInPeriod(
      AdvertisementCategory category, LocalDateTime now, int limit) {
    return advertisementJpaRepository
        .findSponsoredInPeriod(category, now, PageRequest.of(0, limit))
        .stream()
        .map(AdvertisementEntity::toDomain)
        .toList();
  }

  @Override
  public List<Advertisement> findDefault(AdvertisementCategory category, int limit) {
    return advertisementJpaRepository.findDefault(category, PageRequest.of(0, limit)).stream()
        .map(AdvertisementEntity::toDomain)
        .toList();
  }

  @Override
  public List<Advertisement> findDisplayedMeetingTop(LocalDateTime now) {
    return advertisementJpaRepository.findDisplayedMeetingTop(MEETING_TOP, now).stream()
        .map(AdvertisementEntity::toDomain)
        .toList();
  }

  @Override
  public boolean existsOtherDisplayed(AdvertisementCategory category, Integer advertisementId) {
    return advertisementJpaRepository.existsByCategoryAndDisplayTrueAndIdNot(
        category, advertisementId);
  }

  @Override
  @Transactional
  public Advertisement update(Advertisement advertisement) {
    AdvertisementEntity entity =
        advertisementJpaRepository
            .findById(advertisement.id())
            .orElseThrow(() -> new IllegalStateException("수정할 광고가 존재하지 않습니다."));
    entity.update(advertisement);
    return entity.toDomain();
  }
}
