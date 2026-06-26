package org.sopt.makers.domain.official.soptstory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.sopt.makers.domain.official.soptstory.exception.SoptStoryFailure.ALREADY_LIKED;
import static org.sopt.makers.domain.official.soptstory.exception.SoptStoryFailure.DUPLICATE_SOPT_STORY_URL;
import static org.sopt.makers.domain.official.soptstory.exception.SoptStoryFailure.NOT_FOUND_SOPT_STORY;
import static org.sopt.makers.domain.official.soptstory.exception.SoptStoryFailure.NOT_LIKED;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.official.soptstory.ScrapedArticle;
import org.sopt.makers.domain.official.soptstory.SoptStory;
import org.sopt.makers.domain.official.soptstory.SoptStoryLike;
import org.sopt.makers.domain.official.soptstory.SoptStorySortType;
import org.sopt.makers.domain.official.soptstory.exception.SoptStoryException;
import org.sopt.makers.domain.official.soptstory.port.SoptStoryLikeRepositoryPort;
import org.sopt.makers.domain.official.soptstory.port.SoptStoryRepositoryPort;
import org.sopt.makers.domain.official.soptstory.port.SoptStoryScraperPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@DisplayName("SoptStoryService 테스트")
class SoptStoryServiceTest {

  private SoptStoryService soptStoryService;
  private InMemorySoptStoryRepositoryPort soptStoryRepositoryPort;

  @BeforeEach
  void setUp() {
    soptStoryRepositoryPort = new InMemorySoptStoryRepositoryPort();
    soptStoryService =
        new SoptStoryService(
            soptStoryRepositoryPort,
            new InMemorySoptStoryLikeRepositoryPort(),
            new FakeSoptStoryScraperPort());
  }

  @Test
  @DisplayName("유효한 값으로 SoptStory 생성 성공")
  void createSoptStorySuccess() {
    Long id =
        soptStoryService.createSoptStory(
            "SOPT 34기 모집",
            "IT 벤처 창업 동아리 SOPT가 34기를 모집합니다.",
            "https://example.com/thumbnail.jpg",
            "https://blog.sopt.org/2024/recruit");

    SoptStory saved = soptStoryRepositoryPort.findById(id).orElseThrow();
    assertThat(saved.title()).isEqualTo("SOPT 34기 모집");
    assertThat(saved.description()).isEqualTo("IT 벤처 창업 동아리 SOPT가 34기를 모집합니다.");
    assertThat(saved.thumbnailUrl()).isEqualTo("https://example.com/thumbnail.jpg");
    assertThat(saved.url()).isEqualTo("https://blog.sopt.org/2024/recruit");
    assertThat(saved.likeCount()).isZero();
  }

  @Test
  @DisplayName("스크래핑 결과로 SoptStory 생성 성공")
  void createSoptStoryWithScrapedArticleSuccess() {
    ScrapedArticle result = soptStoryService.createSoptStory("https://example.com/scraped");

    assertThat(result.title()).isEqualTo("스크랩 제목");
    assertThat(soptStoryRepositoryPort.findById(1L).orElseThrow().url())
        .isEqualTo("https://example.com/scraped");
  }

  @Test
  @DisplayName("중복 URL로 생성 시 예외 발생")
  void createSoptStoryDuplicateUrlThrowsException() {
    String url = "https://example.com/article";
    soptStoryService.createSoptStory("제목1", "설명1", null, url);

    assertThatThrownBy(() -> soptStoryService.createSoptStory("제목2", "설명2", null, url))
        .isInstanceOf(SoptStoryException.class)
        .extracting("error")
        .isEqualTo(DUPLICATE_SOPT_STORY_URL);
  }

  @Test
  @DisplayName("좋아요 성공")
  void likeSuccess() {
    Long storyId = createSoptStory();

    Long likeId = soptStoryService.like(storyId, "192.168.0.1");

    assertThat(likeId).isNotNull();
    assertThat(soptStoryRepositoryPort.findById(storyId).orElseThrow().likeCount()).isEqualTo(1);
  }

  @Test
  @DisplayName("여러 사용자가 같은 SoptStory에 좋아요 성공")
  void likeMultipleUsersSuccess() {
    Long storyId = createSoptStory();

    soptStoryService.like(storyId, "192.168.0.1");
    soptStoryService.like(storyId, "192.168.0.2");
    soptStoryService.like(storyId, "192.168.0.3");

    assertThat(soptStoryRepositoryPort.findById(storyId).orElseThrow().likeCount()).isEqualTo(3);
  }

  @Test
  @DisplayName("존재하지 않는 SoptStory에 좋아요 시도하면 예외 발생")
  void likeNotExistingThrowsException() {
    assertThatThrownBy(() -> soptStoryService.like(99999L, "192.168.0.1"))
        .isInstanceOf(SoptStoryException.class)
        .extracting("error")
        .isEqualTo(NOT_FOUND_SOPT_STORY);
  }

  @Test
  @DisplayName("중복 좋아요 시 예외 발생")
  void likeAlreadyThrowsException() {
    Long storyId = createSoptStory();
    String ip = "192.168.0.1";
    soptStoryService.like(storyId, ip);

    assertThatThrownBy(() -> soptStoryService.like(storyId, ip))
        .isInstanceOf(SoptStoryException.class)
        .extracting("error")
        .isEqualTo(ALREADY_LIKED);
  }

  @Test
  @DisplayName("좋아요 취소 성공")
  void unlikeSuccess() {
    Long storyId = createSoptStory();
    String ip = "192.168.0.1";
    soptStoryService.like(storyId, ip);

    soptStoryService.unlike(storyId, ip);

    assertThat(soptStoryRepositoryPort.findById(storyId).orElseThrow().likeCount()).isZero();
  }

  @Test
  @DisplayName("좋아요를 누르지 않은 상태에서 취소 시 예외 발생")
  void unlikeNotLikedThrowsException() {
    Long storyId = createSoptStory();

    assertThatThrownBy(() -> soptStoryService.unlike(storyId, "192.168.0.1"))
        .isInstanceOf(SoptStoryException.class)
        .extracting("error")
        .isEqualTo(NOT_LIKED);
  }

  @Test
  @DisplayName("좋아요, 취소, 다시 좋아요 시나리오")
  void likeUnlikeLikeScenario() {
    Long storyId = createSoptStory();
    String ip = "192.168.0.1";

    soptStoryService.like(storyId, ip);
    assertThat(soptStoryRepositoryPort.findById(storyId).orElseThrow().likeCount()).isEqualTo(1);

    soptStoryService.unlike(storyId, ip);
    assertThat(soptStoryRepositoryPort.findById(storyId).orElseThrow().likeCount()).isZero();

    soptStoryService.like(storyId, ip);
    assertThat(soptStoryRepositoryPort.findById(storyId).orElseThrow().likeCount()).isEqualTo(1);
  }

  @Test
  @DisplayName("최신순 정렬 조회")
  void getSoptStoryListSortByRecent() {
    createSoptStoryWithTitle("제목1", "https://example.com/1");
    createSoptStoryWithTitle("제목2", "https://example.com/2");
    createSoptStoryWithTitle("제목3", "https://example.com/3");

    Page<SoptStory> result = soptStoryService.getSoptStoryList("recent", 1, 10);

    assertThat(result.getContent())
        .extracting(SoptStory::title)
        .containsExactly("제목3", "제목2", "제목1");
  }

  @Test
  @DisplayName("좋아요순 정렬 조회")
  void getSoptStoryListSortByLikes() {
    Long story1 = createSoptStoryWithTitle("제목1", "https://example.com/1");
    Long story2 = createSoptStoryWithTitle("제목2", "https://example.com/2");
    createSoptStoryWithTitle("제목3", "https://example.com/3");

    soptStoryService.like(story2, "192.168.0.1");
    soptStoryService.like(story2, "192.168.0.2");
    soptStoryService.like(story2, "192.168.0.3");
    soptStoryService.like(story1, "192.168.0.4");

    Page<SoptStory> result = soptStoryService.getSoptStoryList("likes", 1, 10);

    assertThat(result.getContent())
        .extracting(SoptStory::title)
        .containsExactly("제목2", "제목1", "제목3");
  }

  @Test
  @DisplayName("페이징 조회")
  void getSoptStoryListPaging() {
    for (int i = 1; i <= 5; i++) {
      createSoptStoryWithTitle("제목" + i, "https://example.com/" + i);
    }

    Page<SoptStory> result = soptStoryService.getSoptStoryList("recent", 1, 3);

    assertThat(result.getContent()).hasSize(3);
    assertThat(result.getTotalElements()).isEqualTo(5);
    assertThat(result.getTotalPages()).isEqualTo(2);
  }

  @Test
  @DisplayName("빈 목록 조회")
  void getSoptStoryListEmpty() {
    Page<SoptStory> result = soptStoryService.getSoptStoryList("recent", 1, 10);

    assertThat(result.getContent()).isEmpty();
    assertThat(result.getTotalElements()).isZero();
  }

  @Test
  @DisplayName("특정 IP가 좋아요한 SoptStory ID 목록 조회")
  void getLikedSoptStoryIds() {
    Long story1 = createSoptStoryWithTitle("제목1", "https://example.com/1");
    Long story2 = createSoptStoryWithTitle("제목2", "https://example.com/2");
    SoptStory first = soptStoryRepositoryPort.findById(story1).orElseThrow();
    SoptStory second = soptStoryRepositoryPort.findById(story2).orElseThrow();
    soptStoryService.like(story2, "192.168.0.1");

    assertThat(soptStoryService.getLikedSoptStoryIds("192.168.0.1", List.of(first, second)))
        .containsExactly(story2);
  }

  @Test
  @DisplayName("IP가 없으면 좋아요한 SoptStory ID 목록은 빈 값으로 조회")
  void getLikedSoptStoryIdsWithoutIp() {
    Long storyId = createSoptStoryWithTitle("제목1", "https://example.com/1");
    SoptStory story = soptStoryRepositoryPort.findById(storyId).orElseThrow();

    assertThat(soptStoryService.getLikedSoptStoryIds(null, List.of(story))).isEmpty();
    assertThat(soptStoryService.getLikedSoptStoryIds("", List.of(story))).isEmpty();
  }

  private Long createSoptStory() {
    return createSoptStoryWithTitle("기본 제목", "https://example.com/default");
  }

  private Long createSoptStoryWithTitle(String title, String url) {
    return soptStoryService.createSoptStory(title, "설명", "https://example.com/thumbnail.jpg", url);
  }

  private static final class InMemorySoptStoryRepositoryPort implements SoptStoryRepositoryPort {

    private final Map<Long, SoptStory> soptStories = new LinkedHashMap<>();
    private long sequence = 1L;
    private int createdAtSequence = 1;

    @Override
    public boolean existsByUrl(String url) {
      return soptStories.values().stream().anyMatch(soptStory -> soptStory.url().equals(url));
    }

    @Override
    public Optional<SoptStory> findById(Long id) {
      return Optional.ofNullable(soptStories.get(id));
    }

    @Override
    public SoptStory save(SoptStory soptStory) {
      Long id = soptStory.id() == null ? sequence++ : soptStory.id();
      LocalDateTime createdAt =
          soptStory.createdAt() == null
              ? LocalDateTime.of(2026, 1, 1, 0, 0).plusSeconds(createdAtSequence++)
              : soptStory.createdAt();
      SoptStory saved =
          new SoptStory(
              id,
              soptStory.title(),
              soptStory.description(),
              soptStory.thumbnailUrl(),
              soptStory.url(),
              soptStory.likeCount(),
              createdAt);
      soptStories.put(id, saved);
      return saved;
    }

    @Override
    public Page<SoptStory> findAll(SoptStorySortType sortType, Pageable pageable) {
      Comparator<SoptStory> comparator =
          sortType == SoptStorySortType.LIKES
              ? Comparator.comparingInt(SoptStory::likeCount).reversed()
              : Comparator.comparing(SoptStory::createdAt).reversed();

      List<SoptStory> sorted = soptStories.values().stream().sorted(comparator).toList();
      int start = (int) pageable.getOffset();
      int end = Math.min(start + pageable.getPageSize(), sorted.size());
      List<SoptStory> content = start >= sorted.size() ? List.of() : sorted.subList(start, end);
      return new PageImpl<>(content, pageable, sorted.size());
    }
  }

  private static final class InMemorySoptStoryLikeRepositoryPort
      implements SoptStoryLikeRepositoryPort {

    private final Map<Long, SoptStoryLike> likes = new LinkedHashMap<>();
    private long sequence = 1L;

    @Override
    public boolean existsBySoptStoryIdAndIp(Long soptStoryId, String ip) {
      return likes.values().stream()
          .anyMatch(like -> like.soptStoryId().equals(soptStoryId) && like.ip().equals(ip));
    }

    @Override
    public Optional<SoptStoryLike> findBySoptStoryIdAndIp(Long soptStoryId, String ip) {
      return likes.values().stream()
          .filter(like -> like.soptStoryId().equals(soptStoryId) && like.ip().equals(ip))
          .findFirst();
    }

    @Override
    public List<SoptStoryLike> findAllByIpAndSoptStoryIn(String ip, List<SoptStory> soptStories) {
      List<Long> storyIds = soptStories.stream().map(SoptStory::id).toList();
      return likes.values().stream()
          .filter(like -> like.ip().equals(ip) && storyIds.contains(like.soptStoryId()))
          .toList();
    }

    @Override
    public SoptStoryLike save(SoptStoryLike like) {
      Long id = like.id() == null ? sequence++ : like.id();
      SoptStoryLike saved = new SoptStoryLike(id, like.soptStoryId(), like.ip());
      likes.put(id, saved);
      return saved;
    }

    @Override
    public void delete(SoptStoryLike like) {
      likes.remove(like.id());
    }
  }

  private static final class FakeSoptStoryScraperPort implements SoptStoryScraperPort {

    @Override
    public ScrapedArticle scrap(String articleUrl) {
      return new ScrapedArticle(
          "https://example.com/thumbnail.jpg", "스크랩 제목", "스크랩 설명", articleUrl, "기타");
    }
  }
}
