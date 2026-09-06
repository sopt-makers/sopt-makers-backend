package org.sopt.makers.storage.db.crew.mumu.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.crew.mumu.MumuPostWriteHistory;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "mumu_post_write_history",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_mumu_post_write_history_user_date",
            columnNames = {"user_id", "written_date"}))
public class MumuPostWriteHistoryEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "written_date", nullable = false)
  private LocalDate writtenDate;

  @Builder(access = PRIVATE)
  private MumuPostWriteHistoryEntity(Long id, Long userId, LocalDate writtenDate) {
    this.id = id;
    this.userId = userId;
    this.writtenDate = writtenDate;
  }

  public MumuPostWriteHistory toDomain() {
    return new MumuPostWriteHistory(id, userId, writtenDate, getCreatedAt(), getUpdatedAt());
  }

  public static MumuPostWriteHistoryEntity fromDomain(MumuPostWriteHistory history) {
    return MumuPostWriteHistoryEntity.builder()
        .id(history.id())
        .userId(history.userId())
        .writtenDate(history.writtenDate())
        .build();
  }
}
