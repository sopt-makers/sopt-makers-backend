package org.sopt.makers.storage.db.admin.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.admin.banner.PublishPeriod;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class PublishPeriodEmbeddable {

  private LocalDate startDate;
  private LocalDate endDate;

  @Builder
  private PublishPeriodEmbeddable(LocalDate startDate, LocalDate endDate) {
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public static PublishPeriodEmbeddable from(PublishPeriod period) {
    return PublishPeriodEmbeddable.builder()
        .startDate(period.startDate())
        .endDate(period.endDate())
        .build();
  }

  public PublishPeriod toDomain() {
    return PublishPeriod.builder().startDate(startDate).endDate(endDate).build();
  }
}
