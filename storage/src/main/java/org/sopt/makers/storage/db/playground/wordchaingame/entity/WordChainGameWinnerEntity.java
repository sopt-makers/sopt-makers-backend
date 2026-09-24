package org.sopt.makers.storage.db.playground.wordchaingame.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.playground.wordchaingame.WordChainGameWinner;

@Entity
@Table(name = "word_chain_game_winner")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WordChainGameWinnerEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Column(name = "user_id")
  private Long userId;

  @Column private Integer score;

  @Column(name = "room_id")
  private Long roomId;

  public static WordChainGameWinnerEntity from(WordChainGameWinner winner) {
    WordChainGameWinnerEntity entity = new WordChainGameWinnerEntity();
    entity.id = winner.id();
    entity.userId = winner.userId();
    entity.score = winner.score();
    entity.roomId = winner.roomId();
    return entity;
  }

  public WordChainGameWinner toDomain() {
    return new WordChainGameWinner(id, userId, score, roomId);
  }
}
