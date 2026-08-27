package org.sopt.makers.storage.db.playground.wordchaingame.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.playground.wordchaingame.Word;

@Entity
@Table(name = "word")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WordEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Column(name = "user_id")
  private Long memberId;

  @Column
  private String word;

  @Column(name = "room_id")
  private Long roomId;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  public static WordEntity from(Word word) {
    WordEntity entity = new WordEntity();
    entity.id = word.id();
    entity.memberId = word.memberId();
    entity.word = word.word();
    entity.roomId = word.roomId();
    entity.createdAt = word.createdAt();
    return entity;
  }

  public Word toDomain() {
    return new Word(id, memberId, word, roomId, createdAt);
  }
}
