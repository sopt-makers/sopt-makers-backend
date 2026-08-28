package org.sopt.makers.domain.crew.advertisement.port;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.crew.advertisement.Advertisement;
import org.sopt.makers.domain.crew.advertisement.AdvertisementCategory;

public interface AdvertisementRepositoryPort {

  Optional<Advertisement> findById(Integer advertisementId);

  void lockAllByCategory(AdvertisementCategory category);

  List<Advertisement> findSponsoredInPeriod(
      AdvertisementCategory category, LocalDateTime now, int limit);

  List<Advertisement> findDefault(AdvertisementCategory category, int limit);

  List<Advertisement> findDisplayedMeetingTop(LocalDateTime now);

  boolean existsOtherDisplayed(AdvertisementCategory category, Integer advertisementId);

  Advertisement update(Advertisement advertisement);
}
