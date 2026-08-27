package org.sopt.makers.domain.playground.wordchaingame.service;

import static org.sopt.makers.domain.playground.wordchaingame.exception.WordChainGameFailure.CANNOT_CREATE_GAME_AS_LAST_WRITER;
import static org.sopt.makers.domain.playground.wordchaingame.exception.WordChainGameFailure.CANNOT_USE_LAST_WRITERS_WORD;
import static org.sopt.makers.domain.playground.wordchaingame.exception.WordChainGameFailure.DUPLICATE_WORD;
import static org.sopt.makers.domain.playground.wordchaingame.exception.WordChainGameFailure.NOT_CHAINING_WORD;
import static org.sopt.makers.domain.playground.wordchaingame.exception.WordChainGameFailure.NOT_FOUND_ROOM;
import static org.sopt.makers.domain.playground.wordchaingame.exception.WordChainGameFailure.NOT_KOREAN_WORD;
import static org.sopt.makers.domain.playground.wordchaingame.exception.WordChainGameFailure.NOT_VALID_WORD;
import static org.sopt.makers.domain.playground.wordchaingame.exception.WordChainGameFailure.NO_WORD_IN_ROOM;
import static org.sopt.makers.domain.playground.wordchaingame.exception.WordChainGameFailure.WORD_NOT_IN_DICTIONARY;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.wordchaingame.Word;
import org.sopt.makers.domain.playground.wordchaingame.WordChainGameRoom;
import org.sopt.makers.domain.playground.wordchaingame.WordChainGameWinner;
import org.sopt.makers.domain.playground.wordchaingame.exception.WordChainGameException;
import org.sopt.makers.domain.playground.wordchaingame.port.DictionaryPort;
import org.sopt.makers.domain.playground.wordchaingame.port.WordChainGameRoomRepositoryPort;
import org.sopt.makers.domain.playground.wordchaingame.port.WordChainGameUserPort;
import org.sopt.makers.domain.playground.wordchaingame.port.WordChainGameUserPort.UserInfo;
import org.sopt.makers.domain.playground.wordchaingame.port.WordChainGameWinnerRepositoryPort;
import org.sopt.makers.domain.playground.wordchaingame.port.WordRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WordChainGameService {

  private final WordChainGameRoomRepositoryPort wordChainGameRoomRepositoryPort;
  private final WordRepositoryPort wordRepositoryPort;
  private final WordChainGameWinnerRepositoryPort wordChainGameWinnerRepositoryPort;
  private final WordChainGameUserPort wordChainGameUserPort;
  private final DictionaryPort dictionaryPort;

  private static final String[] INITIAL_CHS = {
    "ㄱ", "ㄲ", "ㄴ", "ㄷ", "ㄸ", "ㄹ", "ㅁ", "ㅂ", "ㅃ", "ㅅ", "ㅆ", "ㅇ", "ㅈ", "ㅉ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"
  };
  private static final String[] MEDIAL_CHS = {
    "ㅏ", "ㅐ", "ㅑ", "ㅒ", "ㅓ", "ㅔ", "ㅕ", "ㅖ", "ㅗ", "ㅘ", "ㅙ", "ㅚ", "ㅛ", "ㅜ", "ㅝ", "ㅞ", "ㅟ", "ㅠ", "ㅡ",
    "ㅢ", "ㅣ"
  };
  private static final String[] FINAL_CHS = {
    " ", "ㄱ", "ㄲ", "ㄳ", "ㄴ", "ㄵ", "ㄶ", "ㄷ", "ㄹ", "ㄺ", "ㄻ", "ㄼ", "ㄽ", "ㄾ", "ㄿ", "ㅀ", "ㅁ", "ㅂ", "ㅄ",
    "ㅅ", "ㅆ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"
  };
  private static final List<String> GAME_START_WORDS =
      List.of(
          "메이커스", "고솝트", "플레이그라운드", "버디버디", "개발", "피그마", "솝트마인드", "마라탕", "음악", "디자이너", "애자일",
          "퇴사", "햇살티미단", "종무식", "서울", "제주", "감자", "휴지", "물고기", "책상", "햄버거", "선물", "미소", "맛집",
          "가방", "의자", "열정", "운동", "성장", "일기", "추억", "이야기");

  @Transactional
  public CreateWordResult createWord(Long userId, Long roomId, String word) {
    checkWordIsOneLetter(word);
    checkWordIsKoreanLetter(word);
    checkRoomIsValid(roomId);
    checkIsChainingWord(roomId, word);
    checkIsInDictionary(word);
    checkIsLastWordWriterIsMakingNextWord(roomId, userId);
    checkDuplicateWord(roomId, word);
    wordRepositoryPort.save(new Word(null, userId, word, roomId, LocalDateTime.now()));
    UserInfo userInfo = wordChainGameUserPort.getUserInfosByIds(List.of(userId)).get(0);
    return new CreateWordResult(roomId, word, userInfo);
  }

  @Transactional
  public CreateRoomResult createWordGameRoom(Long userId) {
    boolean isGameCreatedBefore = wordChainGameRoomRepositoryPort.existsAny();
    Long createdUserId = isGameCreatedBefore ? userId : null;
    if (isGameCreatedBefore) {
      WordChainGameRoom lastRoom =
          wordChainGameRoomRepositoryPort
              .findLatestRoom()
              .orElseThrow(() -> new WordChainGameException(NOT_FOUND_ROOM));
      checkInputWordIsNone(lastRoom);
      checkLastWordWriterIsMakingNewGame(lastRoom.id(), userId);
      insertGameWinner(lastRoom.id());
    }
    WordChainGameRoom room =
        wordChainGameRoomRepositoryPort.save(
            new WordChainGameRoom(null, getRandomStartWord(), LocalDateTime.now(), createdUserId, List.of()));
    UserInfo creatorInfo =
        isGameCreatedBefore
            ? wordChainGameUserPort.getUserInfosByIds(List.of(userId)).get(0)
            : null;
    return new CreateRoomResult(room.id(), room.startWord(), creatorInfo);
  }

  @Transactional
  public void insertGameWinner(Long lastRoomId) {
    Word lastWord =
        wordRepositoryPort
            .findLastWordByRoomId(lastRoomId)
            .orElseThrow(() -> new WordChainGameException(NO_WORD_IN_ROOM));
    int prevScore =
        wordChainGameWinnerRepositoryPort
            .findLatestByUserId(lastWord.memberId())
            .map(WordChainGameWinner::score)
            .orElse(0);
    wordChainGameWinnerRepositoryPort.save(
        new WordChainGameWinner(null, lastWord.memberId(), prevScore + 1, lastRoomId));
  }

  @Transactional(readOnly = true)
  public List<RoomResult> getAllRooms(Integer limit, Long cursor) {
    List<WordChainGameRoom> rooms = wordChainGameRoomRepositoryPort.findAllRooms(limit, cursor);

    Set<Long> allUserIds = new HashSet<>();
    rooms.forEach(
        room -> {
          if (room.createdUserId() != null) allUserIds.add(room.createdUserId());
          room.wordList().forEach(word -> allUserIds.add(word.memberId()));
        });

    Map<Long, UserInfo> userMap =
        allUserIds.isEmpty()
            ? Map.of()
            : wordChainGameUserPort.getUserInfosByIds(List.copyOf(allUserIds)).stream()
                .collect(Collectors.toMap(UserInfo::id, Function.identity()));

    return rooms.stream().map(room -> toRoomResult(room, userMap)).toList();
  }

  @Transactional(readOnly = true)
  public List<WinnerResult> getAllWinners(Integer limit, Integer cursor) {
    List<WordChainGameWinner> winners =
        (limit != null)
            ? wordChainGameWinnerRepositoryPort.findAllLimited(limit, cursor)
            : wordChainGameWinnerRepositoryPort.findAllDesc();

    List<Long> userIds = winners.stream().map(WordChainGameWinner::userId).toList();
    Map<Long, UserInfo> userMap =
        userIds.isEmpty()
            ? Map.of()
            : wordChainGameUserPort.getUserInfosByIds(userIds).stream()
                .collect(Collectors.toMap(UserInfo::id, Function.identity()));

    return winners.stream()
        .map(winner -> new WinnerResult(winner.roomId(), userMap.get(winner.userId())))
        .toList();
  }

  private RoomResult toRoomResult(WordChainGameRoom room, Map<Long, UserInfo> userMap) {
    UserInfo startUser =
        room.createdUserId() != null ? userMap.get(room.createdUserId()) : null;
    List<RoomResult.WordEntry> wordEntries =
        room.wordList().stream()
            .sorted(Comparator.comparing(Word::id))
            .map(word -> new RoomResult.WordEntry(word.word(), userMap.get(word.memberId())))
            .toList();
    return new RoomResult(room.id(), room.startWord(), startUser, wordEntries);
  }

  private String getRandomStartWord() {
    return GAME_START_WORDS.get(new Random().nextInt(GAME_START_WORDS.size()));
  }

  private void checkIsLastWordWriterIsMakingNextWord(Long roomId, Long userId) {
    wordRepositoryPort
        .findLastWordByRoomId(roomId)
        .ifPresent(
            lastWord -> {
              if (lastWord.memberId().equals(userId)) {
                throw new WordChainGameException(CANNOT_USE_LAST_WRITERS_WORD);
              }
            });
  }

  private void checkIsChainingWord(Long roomId, String word) {
    String prevWord =
        wordRepositoryPort
            .findLastWordByRoomId(roomId)
            .map(Word::word)
            .orElseGet(
                () ->
                    wordChainGameRoomRepositoryPort
                        .findById(roomId)
                        .map(WordChainGameRoom::startWord)
                        .orElseThrow(() -> new WordChainGameException(NOT_FOUND_ROOM)));
    checkIsChainingWordStr(prevWord, word);
  }

  private void checkLastWordWriterIsMakingNewGame(Long lastRoomId, Long userId) {
    Word lastWord =
        wordRepositoryPort
            .findLastWordByRoomId(lastRoomId)
            .orElseThrow(() -> new WordChainGameException(NO_WORD_IN_ROOM));
    if (lastWord.memberId().equals(userId)) {
      throw new WordChainGameException(CANNOT_CREATE_GAME_AS_LAST_WRITER);
    }
  }

  private void checkDuplicateWord(Long roomId, String word) {
    if (wordRepositoryPort.existsByWordAndRoomId(word, roomId)) {
      throw new WordChainGameException(DUPLICATE_WORD);
    }
  }

  private void checkRoomIsValid(Long roomId) {
    wordChainGameRoomRepositoryPort
        .findById(roomId)
        .orElseThrow(() -> new WordChainGameException(NOT_FOUND_ROOM));
  }

  private void checkInputWordIsNone(WordChainGameRoom lastRoom) {
    if (lastRoom.wordList().isEmpty()) {
      throw new WordChainGameException(NO_WORD_IN_ROOM);
    }
  }

  private void checkWordIsKoreanLetter(String word) {
    if (!word.matches("[ㄱ-ㅎㅏ-ㅣ가-힣]+")) {
      throw new WordChainGameException(NOT_KOREAN_WORD);
    }
  }

  private void checkWordIsOneLetter(String word) {
    if (word.length() < 2) {
      throw new WordChainGameException(NOT_VALID_WORD);
    }
  }

  private void checkIsInDictionary(String word) {
    if (!dictionaryPort.isValidWord(word)) {
      throw new WordChainGameException(WORD_NOT_IN_DICTIONARY);
    }
  }

  private void checkIsChainingWordStr(String lastWord, String nextWord) {
    if (isNotChainingWord(lastWord, nextWord)) {
      throw new WordChainGameException(NOT_CHAINING_WORD);
    }
  }

  private boolean isNotChainingWord(String lastWord, String nextWord) {
    if (checkInitialSoundIsDooemBubchik(
        lastWord.charAt(lastWord.length() - 1), nextWord.charAt(0))) {
      return false;
    }
    return nextWord.charAt(0) != lastWord.charAt(lastWord.length() - 1);
  }

  private boolean checkInitialSoundIsDooemBubchik(char lastChar, char firstChar) {
    final String neeunToEung = "ㅕㅛㅠㅣ";
    final String leeuelToEung = "ㅑㅕㅖㅛㅠㅣ";
    final String leeuelToNeeun = "ㅏㅐㅗㅚㅜㅡ";

    int preWord = lastChar - 0xAC00;
    int initialCh = preWord / (21 * 28);
    if (!Objects.equals(INITIAL_CHS[initialCh], "ㄹ")
        && !Objects.equals(INITIAL_CHS[initialCh], "ㄴ")) return false;
    int medialCh = (preWord % (28 * 21)) / 28;
    int finalCh = preWord % 28;

    int nextWord = firstChar - 0xAC00;
    int initialNextCh = nextWord / (21 * 28);
    int medialNextCh = (nextWord % (28 * 21)) / 28;
    int finalNextCh = nextWord % 28;

    if (Objects.equals(INITIAL_CHS[initialCh], "ㄴ")) {
      boolean canBeDooem = neeunToEung.contains(MEDIAL_CHS[medialCh]);
      if (canBeDooem) {
        boolean nextStartsWithEungOrNeeun =
            Objects.equals(INITIAL_CHS[initialNextCh], "ㅇ")
                || Objects.equals(INITIAL_CHS[initialNextCh], "ㄴ");
        if (nextStartsWithEungOrNeeun
            && Objects.equals(FINAL_CHS[finalCh], FINAL_CHS[finalNextCh])
            && Objects.equals(MEDIAL_CHS[medialCh], MEDIAL_CHS[medialNextCh])) {
          return true;
        }
      }
    }

    if (Objects.equals(INITIAL_CHS[initialCh], "ㄹ")) {
      boolean canBeDooem = leeuelToEung.contains(MEDIAL_CHS[medialCh]);
      if (canBeDooem) {
        boolean nextStartsWithEungOrLeeul =
            Objects.equals(INITIAL_CHS[initialNextCh], "ㅇ")
                || Objects.equals(INITIAL_CHS[initialNextCh], "ㄹ");
        if (nextStartsWithEungOrLeeul
            && Objects.equals(FINAL_CHS[finalCh], FINAL_CHS[finalNextCh])
            && Objects.equals(MEDIAL_CHS[medialCh], MEDIAL_CHS[medialNextCh])) {
          return true;
        }
      }
    }

    if (Objects.equals(INITIAL_CHS[initialCh], "ㄹ")) {
      boolean canBeDooem = leeuelToNeeun.contains(MEDIAL_CHS[medialCh]);
      if (canBeDooem) {
        boolean nextStartsWithNeeunOrEung =
            Objects.equals(INITIAL_CHS[initialNextCh], "ㅇ")
                || Objects.equals(INITIAL_CHS[initialNextCh], "ㄴ");
        if (nextStartsWithNeeunOrEung
            && Objects.equals(FINAL_CHS[finalCh], FINAL_CHS[finalNextCh])
            && Objects.equals(MEDIAL_CHS[medialCh], MEDIAL_CHS[medialNextCh])) {
          return true;
        }
      }
    }

    return false;
  }

  public record CreateWordResult(Long roomId, String word, UserInfo userInfo) {}

  public record CreateRoomResult(Long roomId, String startWord, UserInfo creatorInfo) {}

  public record RoomResult(Long id, String startWord, UserInfo startUser, List<WordEntry> words) {
    public record WordEntry(String word, UserInfo user) {}
  }

  public record WinnerResult(Long roomId, UserInfo winner) {}
}
