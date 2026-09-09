package org.sopt.makers.storage.db.playground.report.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "word")
public class WordEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "user_id")
  private Long memberId;

  @Column(name = "word")
  private String word;

  @Column(name = "room_id")
  private Long roomId;

  @Column(name = "created_at")
  private LocalDateTime createdAt;
}
