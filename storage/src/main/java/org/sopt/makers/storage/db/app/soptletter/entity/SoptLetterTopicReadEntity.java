package org.sopt.makers.storage.db.app.soptletter.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "sopt_letter_topic_read",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_sopt_letter_topic_read_user_topic",
            columnNames = {"user_id", "topic_id"}))
public class SoptLetterTopicReadEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "topic_id", nullable = false)
  private Long topicId;

  @Column(name = "last_read_letter_id", nullable = false)
  private Long lastReadLetterId;
}
