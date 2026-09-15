package org.sopt.makers.storage.db.playground.wordchaingame.entity;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.playground.wordchaingame.WordChainGameRoom;

@Entity
@Table(name = "word_chain_gameroom")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WordChainGameRoomEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Column(name = "start_word")
  private String startWord;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "created_user_id")
  private Long createdUserId;

  @OneToMany(fetch = EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "room_id")
  private List<WordEntity> wordList = new ArrayList<>();

  public static WordChainGameRoomEntity from(WordChainGameRoom room) {
    WordChainGameRoomEntity entity = new WordChainGameRoomEntity();
    entity.id = room.id();
    entity.startWord = room.startWord();
    entity.createdAt = room.createdAt();
    entity.createdUserId = room.createdUserId();
    return entity;
  }

  public WordChainGameRoom toDomain() {
    return new WordChainGameRoom(
        id,
        startWord,
        createdAt,
        createdUserId,
        wordList.stream().map(WordEntity::toDomain).toList());
  }
}
