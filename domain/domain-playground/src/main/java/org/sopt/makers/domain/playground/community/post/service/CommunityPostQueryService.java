package org.sopt.makers.domain.playground.community.post.service;

import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.BLOCKED_MEMBER_POST;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.INVALID_CATEGORY_CODE;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.MISSING_CATEGORY_PARAMETER;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.NOT_FOUND_POST;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.Category;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.sopt.makers.domain.playground.community.CommunityCategoryGroup;
import org.sopt.makers.domain.playground.community.CommunityPostListCategory;
import org.sopt.makers.domain.playground.community.CommunityPostListFilter;
import org.sopt.makers.domain.playground.community.CommunityPostSourceType;
import org.sopt.makers.domain.playground.community.CommunityPostTag;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousProfile;
import org.sopt.makers.domain.playground.community.anonymous.service.AnonymousProfileRetriever;
import org.sopt.makers.domain.playground.community.comment.CommentThread;
import org.sopt.makers.domain.playground.community.comment.service.CommentQueryService;
import org.sopt.makers.domain.playground.community.exception.CommunityException;
import org.sopt.makers.domain.playground.community.member.CommunityMemberSummary;
import org.sopt.makers.domain.playground.community.member.service.CommunityMemberAssembler;
import org.sopt.makers.domain.playground.community.post.CommunityDbCursor;
import org.sopt.makers.domain.playground.community.post.CommunityFeedCursor;
import org.sopt.makers.domain.playground.community.post.Post;
import org.sopt.makers.domain.playground.community.post.PostDetail;
import org.sopt.makers.domain.playground.community.post.PostFeedItem;
import org.sopt.makers.domain.playground.community.post.PostFeedResult;
import org.sopt.makers.domain.playground.community.post.PopularPost;
import org.sopt.makers.domain.playground.community.post.PopularPost.PopularPostMember;
import org.sopt.makers.domain.playground.community.post.RecentPost;
import org.sopt.makers.domain.playground.community.post.SopticlePost;
import org.sopt.makers.domain.playground.community.post.crew.CrewMeetingPost;
import org.sopt.makers.domain.playground.community.post.crew.port.CrewMeetingPostPort;
import org.sopt.makers.domain.playground.community.post.crew.port.CrewMeetingPostPort.CrewMeetingFeedPage;
import org.sopt.makers.domain.playground.community.post.port.PostLikeRepositoryPort;
import org.sopt.makers.domain.playground.community.post.port.PostRepositoryPort;
import org.sopt.makers.domain.playground.community.service.CategoryQueryService;
import org.sopt.makers.domain.playground.community.service.CommunityCategoryPolicy;
import org.sopt.makers.domain.playground.community.vote.VoteResult;
import org.sopt.makers.domain.playground.community.vote.service.VoteQueryService;
import org.sopt.makers.domain.playground.member.relation.port.UserBlockRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 커뮤니티 게시글 조회(목록/상세/인기글/최신글/솝티클/핫게시글) 유스케이스. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityPostQueryService {

  private static final int MAX_LIST_LIMIT = 50;
  private static final int HOME_PREVIEW_LIMIT = 3;
  private static final int POPULAR_LOOKBACK_DAYS = 30;
  private static final int POPULAR_COMMENT_WEIGHT = 5;
  private static final int POPULAR_LIKE_WEIGHT = 3;
  private static final int POPULAR_MEETING_MAX_PAGE = 2;
  private static final int MIN_POINTS_FOR_HOT_POST = 10;

  private final PostRepositoryPort postRepositoryPort;
  private final PostLikeRepositoryPort postLikeRepositoryPort;
  private final CategoryQueryService categoryQueryService;
  private final CommunityCategoryPolicy communityCategoryPolicy;
  private final CommunityMemberAssembler communityMemberAssembler;
  private final AnonymousProfileRetriever anonymousProfileRetriever;
  private final CrewMeetingPostPort crewMeetingPostPort;
  private final CommunityFeedCursorCodec communityFeedCursorCodec;
  private final CommentQueryService commentQueryService;
  private final VoteQueryService voteQueryService;
  private final UserBlockRepositoryPort userBlockRepositoryPort;

  private record FeedCandidate(
      CommunityPostSourceType sourceType,
      LocalDateTime createdAt,
      Long communityPostId,
      Integer meetingConsumedCount,
      PostFeedItem item) {}

  private record MeetingCandidate(LocalDateTime createdAt, Integer nextConsumedCount, PostFeedItem item) {}

  private record MeetingFetchResult(List<MeetingCandidate> candidates, boolean hasMore) {}

  private record PopularCandidate(
      CommunityPostSourceType sourceType, int score, LocalDateTime createdAt, Long id, PopularPost post) {}

  private record RecentCandidate(
      CommunityPostSourceType sourceType, LocalDateTime createdAt, Long id, RecentPost post) {}

  private record PostWithPoints(Post post, int points, int hits) {}

  // ==========================================
  // 목록 조회
  // ==========================================

  public PostFeedResult getPosts(
      Long userId,
      CommunityCategoryCode categoryCode,
      CommunityPostListCategory category,
      CommunityPostListFilter filter,
      Boolean isBlockedOn,
      Integer limit,
      String cursor) {
    if (categoryCode == CommunityCategoryCode.MEETING) {
      throw new CommunityException(INVALID_CATEGORY_CODE);
    }

    if (categoryCode == null && category == null) {
      throw new CommunityException(MISSING_CATEGORY_PARAMETER);
    }

    List<CommunityCategoryCode> categoryCodes =
        communityCategoryPolicy.resolveCategoryCodes(categoryCode, category, filter);

    boolean isFreeRequest = categoryCodes.contains(CommunityCategoryCode.FREE);
    CommunityPostListCategory effectiveCategory = toListCategory(categoryCodes.get(0));

    int normalizedLimit = normalizeLimit(limit);
    CommunityFeedCursor decodedCursor = communityFeedCursorCodec.decodeOrInitial(cursor);
    Set<Long> blockedWriterIds = resolveBlockedWriterIds(userId, isBlockedOn);

    if (isFreeRequest) {
      return getFreePostsWithMeetingPosts(userId, normalizedLimit, decodedCursor, categoryCodes, blockedWriterIds);
    }

    return getCommunityOnlyPosts(
        userId, effectiveCategory, normalizedLimit, decodedCursor, categoryCodes, blockedWriterIds);
  }

  private Set<Long> resolveBlockedWriterIds(Long userId, Boolean isBlockedOn) {
    if (userId == null || !Boolean.TRUE.equals(isBlockedOn)) {
      return Set.of();
    }
    return userBlockRepositoryPort.findBlockedUserIdsInvolving(userId);
  }

  private PostFeedResult getCommunityOnlyPosts(
      Long userId,
      CommunityPostListCategory effectiveCategory,
      int limit,
      CommunityFeedCursor cursor,
      List<CommunityCategoryCode> categoryCodes,
      Set<Long> blockedWriterIds) {
    List<Post> posts =
        postRepositoryPort.findByCategoryCodesWithCursor(
            categoryCodes,
            cursorCreatedAt(cursor),
            cursorPostId(cursor),
            cursor.snapshotTime(),
            limit + 1);

    boolean hasNext = posts.size() > limit;
    List<Post> sliced = hasNext ? posts.subList(0, limit) : posts;

    List<PostFeedItem> items =
        toVisiblePostFeedItems(sliced, userId, categoryCodes, blockedWriterIds);

    CommunityDbCursor nextDbCursor =
        sliced.isEmpty()
            ? null
            : new CommunityDbCursor(
                sliced.get(sliced.size() - 1).createdAt(), sliced.get(sliced.size() - 1).id());

    String nextCursor =
        hasNext && nextDbCursor != null
            ? communityFeedCursorCodec.encode(
                new CommunityFeedCursor(cursor.snapshotTime(), nextDbCursor, cursor.safeMeetingConsumedCount()))
            : null;

    return new PostFeedResult(effectiveCategory, hasNext, nextCursor, items);
  }

  private PostFeedResult getFreePostsWithMeetingPosts(
      Long userId,
      int limit,
      CommunityFeedCursor cursor,
      List<CommunityCategoryCode> freeCategoryCodes,
      Set<Long> blockedWriterIds) {
    List<Post> communityPosts =
        postRepositoryPort.findByCategoryCodesWithCursor(
            freeCategoryCodes,
            cursorCreatedAt(cursor),
            cursorPostId(cursor),
            cursor.snapshotTime(),
            limit + 1);

    List<PostFeedItem> communityItems =
        toPostFeedItems(communityPosts, userId, freeCategoryCodes, blockedWriterIds);

    MeetingFetchResult meetingFetchResult =
        fetchMeetingItems(userId, cursor.snapshotTime(), cursor.safeMeetingConsumedCount(), limit + 1);

    List<FeedCandidate> candidates = new ArrayList<>();

    for (int index = 0; index < communityItems.size(); index++) {
      Post post = communityPosts.get(index);
      candidates.add(
          new FeedCandidate(
              CommunityPostSourceType.COMMUNITY, post.createdAt(), post.id(), null, communityItems.get(index)));
    }

    for (MeetingCandidate meetingCandidate : meetingFetchResult.candidates()) {
      candidates.add(
          new FeedCandidate(
              CommunityPostSourceType.MEETING,
              meetingCandidate.createdAt(),
              null,
              meetingCandidate.nextConsumedCount(),
              meetingCandidate.item()));
    }

    List<FeedCandidate> sortedCandidates =
        candidates.stream()
            .sorted(
                Comparator.comparing(FeedCandidate::createdAt)
                    .reversed()
                    .thenComparing(candidate -> candidate.sourceType().name())
                    .thenComparing(candidate -> candidate.item().id(), Comparator.reverseOrder()))
            .toList();

    boolean hasNext = sortedCandidates.size() > limit || meetingFetchResult.hasMore();

    List<FeedCandidate> slicedCandidates =
        sortedCandidates.size() > limit ? sortedCandidates.subList(0, limit) : sortedCandidates;

    Map<Long, Long> writerIdByCommunityPostId =
        communityPosts.stream().collect(Collectors.toMap(Post::id, Post::writerId));

    List<PostFeedItem> items =
        slicedCandidates.stream()
            .filter(candidate -> !isBlockedCommunityCandidate(candidate, writerIdByCommunityPostId, blockedWriterIds))
            .map(FeedCandidate::item)
            .toList();

    CommunityDbCursor nextDbCursor = cursor.community();
    int nextMeetingConsumedCount = cursor.safeMeetingConsumedCount();

    Optional<FeedCandidate> lastCommunityCandidate =
        slicedCandidates.stream()
            .filter(candidate -> candidate.sourceType() == CommunityPostSourceType.COMMUNITY)
            .reduce((previous, current) -> current);

    if (lastCommunityCandidate.isPresent()) {
      FeedCandidate candidate = lastCommunityCandidate.get();
      nextDbCursor = new CommunityDbCursor(candidate.createdAt(), candidate.communityPostId());
    }

    Optional<FeedCandidate> lastMeetingCandidate =
        slicedCandidates.stream()
            .filter(candidate -> candidate.sourceType() == CommunityPostSourceType.MEETING)
            .reduce((previous, current) -> current);

    if (lastMeetingCandidate.isPresent()) {
      nextMeetingConsumedCount = lastMeetingCandidate.get().meetingConsumedCount();
    }

    String nextCursor =
        hasNext
            ? communityFeedCursorCodec.encode(
                new CommunityFeedCursor(cursor.snapshotTime(), nextDbCursor, nextMeetingConsumedCount))
            : null;

    return new PostFeedResult(CommunityPostListCategory.FREE, hasNext, nextCursor, items);
  }

  private MeetingFetchResult fetchMeetingItems(
      Long userId, LocalDateTime snapshotTime, int alreadyConsumedCount, int requiredCount) {
    CrewMeetingFeedPage page = crewMeetingPostPort.getFeed(userId, snapshotTime, alreadyConsumedCount + requiredCount);

    List<CrewMeetingPost> posts = page.safePosts();
    int fromIndex = Math.min(alreadyConsumedCount, posts.size());
    int toIndex = Math.min(fromIndex + requiredCount, posts.size());

    List<MeetingCandidate> candidates = new ArrayList<>();

    for (int index = fromIndex; index < toIndex; index++) {
      CrewMeetingPost post = posts.get(index);
      candidates.add(new MeetingCandidate(post.createdAt(), index + 1, toMeetingPostFeedItem(post, userId)));
    }

    boolean hasMore = page.hasMorePage() || posts.size() > toIndex;

    return new MeetingFetchResult(candidates, hasMore);
  }

  private PostFeedItem toMeetingPostFeedItem(CrewMeetingPost post, Long viewerId) {
    CommunityMemberSummary member =
        new CommunityMemberSummary(
            post.writerOrgId(),
            post.writerName(),
            post.writerProfileImage(),
            new CommunityMemberSummary.Activity(post.writerGeneration(), post.writerPart(), null),
            null);

    return new PostFeedItem(
        CommunityPostSourceType.MEETING,
        post.id(),
        member,
        post.writerOrgId(),
        Objects.equals(post.writerOrgId(), viewerId),
        post.isLiked(),
        post.likeCount(),
        CommunityCategoryGroup.FREE,
        CommunityCategoryCode.FREE,
        "자유",
        List.of(CommunityPostTag.MEETING),
        post.title(),
        post.content(),
        post.viewCount(),
        post.commentCount(),
        List.of(),
        post.images(),
        false,
        null,
        null,
        post.createdAt(),
        post.meetingId(),
        null);
  }

  private List<PostFeedItem> toVisiblePostFeedItems(
      List<Post> posts, Long viewerId, List<CommunityCategoryCode> categoryCodes, Set<Long> blockedWriterIds) {
    List<PostFeedItem> items = toPostFeedItems(posts, viewerId, categoryCodes, blockedWriterIds);
    if (blockedWriterIds.isEmpty()) {
      return items;
    }

    List<PostFeedItem> visibleItems = new ArrayList<>();
    for (int index = 0; index < posts.size(); index++) {
      if (!blockedWriterIds.contains(posts.get(index).writerId())) {
        visibleItems.add(items.get(index));
      }
    }
    return visibleItems;
  }

  private boolean isBlockedCommunityCandidate(
      FeedCandidate candidate, Map<Long, Long> writerIdByCommunityPostId, Set<Long> blockedWriterIds) {
    if (blockedWriterIds.isEmpty() || candidate.sourceType() != CommunityPostSourceType.COMMUNITY) {
      return false;
    }
    Long writerId = writerIdByCommunityPostId.get(candidate.communityPostId());
    return writerId != null && blockedWriterIds.contains(writerId);
  }

  private List<PostFeedItem> toPostFeedItems(
      List<Post> posts, Long viewerId, List<CommunityCategoryCode> categoryCodes, Set<Long> blockedWriterIds) {
    if (posts.isEmpty()) {
      return List.of();
    }

    List<Long> writerIds = posts.stream().map(Post::writerId).distinct().toList();
    List<Long> postIds = posts.stream().map(Post::id).toList();
    List<Long> anonymousProfileIds =
        posts.stream().map(Post::anonymousProfileId).filter(Objects::nonNull).distinct().toList();

    Map<Long, CommunityMemberSummary> memberMap = communityMemberAssembler.getMemberSummaryMap(writerIds);
    Map<Long, Category> categoryMap =
        categoryQueryService.findActiveCategoriesByCodes(categoryCodes).stream()
            .collect(Collectors.toMap(Category::id, category -> category));
    Map<Long, AnonymousProfile> anonymousProfileMap = anonymousProfileRetriever.findAllByIdsAsMap(anonymousProfileIds);
    Map<Long, Boolean> likedMap = getLikedMap(viewerId, postIds);
    Map<Long, Integer> likeCountMap = toIntCountMap(postLikeRepositoryPort.countLikesByPostIds(postIds));
    Map<Long, List<CommentThread>> commentMap =
        commentQueryService.getCommentThreadsByPostIds(viewerId, postIds, blockedWriterIds);
    Map<Long, VoteResult> voteMap = voteQueryService.getVoteResultsByPostIds(postIds, viewerId);

    return posts.stream()
        .map(
            post ->
                toPostFeedItem(
                    post,
                    viewerId,
                    memberMap,
                    categoryMap,
                    anonymousProfileMap,
                    likedMap,
                    likeCountMap,
                    commentMap,
                    voteMap))
        .toList();
  }

  private PostFeedItem toPostFeedItem(
      Post post,
      Long viewerId,
      Map<Long, CommunityMemberSummary> memberMap,
      Map<Long, Category> categoryMap,
      Map<Long, AnonymousProfile> anonymousProfileMap,
      Map<Long, Boolean> likedMap,
      Map<Long, Integer> likeCountMap,
      Map<Long, List<CommentThread>> commentMap,
      Map<Long, VoteResult> voteMap) {
    boolean isBlind = Boolean.TRUE.equals(post.isBlindWriter());
    CommunityMemberSummary member = isBlind ? null : memberMap.get(post.writerId());
    Long writerId = isBlind ? null : post.writerId();
    boolean isMine = Objects.equals(post.writerId(), viewerId);
    Category category = categoryMap.get(post.categoryId());
    AnonymousProfile anonymousProfile =
        isBlind && post.anonymousProfileId() != null ? anonymousProfileMap.get(post.anonymousProfileId()) : null;
    List<CommentThread> comments = commentMap.getOrDefault(post.id(), List.of());
    int commentCount = (int) comments.stream().filter(thread -> !Boolean.TRUE.equals(thread.comment().isDeleted())).count();

    return new PostFeedItem(
        CommunityPostSourceType.COMMUNITY,
        post.id(),
        member,
        writerId,
        isMine,
        likedMap.getOrDefault(post.id(), false),
        likeCountMap.getOrDefault(post.id(), 0),
        category == null ? null : category.categoryGroup(),
        category == null ? null : category.code(),
        category == null ? null : category.name(),
        List.of(),
        post.title(),
        post.content(),
        post.hits(),
        commentCount,
        comments,
        post.images(),
        post.isBlindWriter(),
        post.sopticleUrl(),
        anonymousProfile,
        post.createdAt(),
        null,
        voteMap.get(post.id()));
  }

  // ==========================================
  // 상세 조회
  // ==========================================

  public PostDetail getPostDetail(Long viewerId, Long postId, Boolean isBlockedOn) {
    Post post =
        postRepositoryPort.findByIdWithCategory(postId).orElseThrow(() -> new CommunityException(NOT_FOUND_POST));

    if (Boolean.TRUE.equals(isBlockedOn)
        && viewerId != null
        && !Objects.equals(viewerId, post.writerId())
        && resolveBlockedWriterIds(viewerId, isBlockedOn).contains(post.writerId())) {
      throw new CommunityException(BLOCKED_MEMBER_POST);
    }

    Category category = categoryQueryService.findById(post.categoryId()).orElse(null);
    Category parentCategory =
        category != null && category.parentId() != null
            ? categoryQueryService.findById(category.parentId()).orElse(null)
            : null;

    boolean isBlind = Boolean.TRUE.equals(post.isBlindWriter());
    CommunityMemberSummary member = isBlind ? null : communityMemberAssembler.getMemberSummary(post.writerId());
    boolean isMine = Objects.equals(post.writerId(), viewerId);
    boolean isLiked = postLikeRepositoryPort.existsByUserIdAndPostId(viewerId, postId);
    int likes = postLikeRepositoryPort.countAllByPostId(postId);
    AnonymousProfile anonymousProfile =
        isBlind ? anonymousProfileRetriever.findById(post.anonymousProfileId()).orElse(null) : null;
    VoteResult vote = voteQueryService.getVoteByPostId(postId, viewerId).orElse(null);

    return new PostDetail(post, category, parentCategory, member, isMine, isLiked, likes, anonymousProfile, vote);
  }

  // ==========================================
  // 인기글
  // ==========================================

  public List<PopularPost> getPopularPosts(Long userId, int limitCount) {
    int normalizedLimit = normalizePreviewLimit(limitCount);

    LocalDateTime snapshotTime = LocalDateTime.now().withSecond(0).withNano(0);
    LocalDateTime since = snapshotTime.minusDays(POPULAR_LOOKBACK_DAYS);

    List<Post> posts = postRepositoryPort.findPopularCandidatePosts(since);

    List<Long> postIds = posts.stream().map(Post::id).toList();
    List<Long> writerIds = posts.stream().map(Post::writerId).distinct().toList();
    List<Long> anonymousProfileIds =
        posts.stream().map(Post::anonymousProfileId).filter(Objects::nonNull).distinct().toList();
    List<Long> categoryIds = posts.stream().map(Post::categoryId).distinct().toList();

    Map<Long, CommunityMemberSummary> memberMap = communityMemberAssembler.getMemberSummaryMap(writerIds);
    Map<Long, AnonymousProfile> anonymousProfileMap = anonymousProfileRetriever.findAllByIdsAsMap(anonymousProfileIds);
    Map<Long, Integer> likeCountMap = toIntCountMap(postLikeRepositoryPort.countLikesByPostIds(postIds));
    Map<Long, Category> categoryMap = categoryQueryService.findAllByIdsAsMap(categoryIds);
    Map<Long, Integer> commentCountMap = commentQueryService.countNonDeletedCommentsByPostIds(postIds);

    List<PopularCandidate> candidates = new ArrayList<>();

    for (Post post : posts) {
      Long postId = post.id();
      boolean isBlind = Boolean.TRUE.equals(post.isBlindWriter());

      AnonymousProfile anonymousProfile =
          post.anonymousProfileId() == null ? null : anonymousProfileMap.get(post.anonymousProfileId());
      CommunityMemberSummary member = memberMap.get(post.writerId());

      if (isBlind && anonymousProfile == null) {
        continue;
      }
      if (!isBlind && member == null) {
        continue;
      }

      int likeCount = likeCountMap.getOrDefault(postId, 0);
      int commentCount = commentCountMap.getOrDefault(postId, 0);
      int score = calculatePopularScore(post.hits(), commentCount, likeCount);

      Category category = categoryMap.get(post.categoryId());
      CommunityPostTag tag =
          communityCategoryPolicy.resolvePreviewTag(
              category == null ? null : category.categoryGroup(), category == null ? null : category.code());

      PopularPostMember popularPostMember =
          isBlind
              ? new PopularPostMember(
                  anonymousProfile.id(), anonymousProfile.nickname().nickname(), anonymousProfile.profileImage().imageUrl())
              : new PopularPostMember(member.id(), member.name(), member.profileImage());

      PopularPost popularPost =
          new PopularPost(
              CommunityPostSourceType.COMMUNITY,
              post.id(),
              post.title(),
              popularPostMember,
              post.hits(),
              likeCount,
              commentCount,
              tag);

      candidates.add(
          new PopularCandidate(CommunityPostSourceType.COMMUNITY, score, post.createdAt(), post.id(), popularPost));
    }

    List<CrewMeetingPost> meetingPosts = getMeetingPostsForPopular(userId, since, snapshotTime);

    for (CrewMeetingPost meetingPost : meetingPosts) {
      int score =
          calculatePopularScore(meetingPost.viewCount(), meetingPost.commentCount(), meetingPost.likeCount());

      PopularPost popularPost =
          new PopularPost(
              CommunityPostSourceType.MEETING,
              meetingPost.id(),
              meetingPost.title(),
              new PopularPostMember(
                  meetingPost.writerOrgId(), meetingPost.writerName(), meetingPost.writerProfileImage()),
              meetingPost.viewCount(),
              meetingPost.likeCount(),
              meetingPost.commentCount(),
              CommunityPostTag.MEETING);

      candidates.add(
          new PopularCandidate(
              CommunityPostSourceType.MEETING, score, meetingPost.createdAt(), meetingPost.id(), popularPost));
    }

    return candidates.stream()
        .sorted(
            Comparator.comparingInt(PopularCandidate::score)
                .reversed()
                .thenComparing(PopularCandidate::createdAt, Comparator.reverseOrder())
                .thenComparing(candidate -> candidate.sourceType().name())
                .thenComparing(PopularCandidate::id, Comparator.reverseOrder()))
        .limit(normalizedLimit)
        .map(PopularCandidate::post)
        .toList();
  }

  private int calculatePopularScore(int viewCount, int commentCount, int likeCount) {
    return viewCount + commentCount * POPULAR_COMMENT_WEIGHT + likeCount * POPULAR_LIKE_WEIGHT;
  }

  private List<CrewMeetingPost> getMeetingPostsForPopular(Long userId, LocalDateTime since, LocalDateTime snapshotTime) {
    if (userId == null) {
      return List.of();
    }

    CrewMeetingFeedPage page =
        crewMeetingPostPort.getPopularPreview(userId, snapshotTime, since, POPULAR_MEETING_MAX_PAGE);

    return page.safePosts().stream()
        .filter(post -> !post.createdAt().isAfter(snapshotTime))
        .filter(post -> !post.createdAt().isBefore(since))
        .toList();
  }

  // ==========================================
  // 솝티클 최신글
  // ==========================================

  public List<SopticlePost> getRecentSopticlePosts() {
    List<CommunityCategoryCode> sopticleCodes =
        communityCategoryPolicy.resolveCategoryCodes(CommunityPostListCategory.SOPTICLE, CommunityPostListFilter.ALL);

    List<Post> posts = postRepositoryPort.findTop5ByCategoryCodesOrderByCreatedAtDesc(sopticleCodes);

    List<Long> writerIds = posts.stream().map(Post::writerId).distinct().toList();
    Map<Long, CommunityMemberSummary> memberMap = communityMemberAssembler.getMemberSummaryMap(writerIds);

    return posts.stream()
        .map(
            post ->
                new SopticlePost(
                    post.id(),
                    memberMap.get(post.writerId()),
                    post.createdAt(),
                    post.title(),
                    post.content(),
                    post.images(),
                    post.sopticleUrl()))
        .toList();
  }

  // ==========================================
  // 홈 - 모든 카테고리 최신글
  // ==========================================

  public List<RecentPost> getRecentPosts(Long memberId) {
    List<Post> posts =
        postRepositoryPort.findTop3ByCategoryGroupsOrderByCreatedAtDesc(
            List.of(CommunityCategoryGroup.FREE, CommunityCategoryGroup.PROMOTION));

    List<Long> postIds = posts.stream().map(Post::id).toList();
    List<Long> categoryIds = posts.stream().map(Post::categoryId).distinct().toList();

    Map<Long, Integer> likeCountMap = toIntCountMap(postLikeRepositoryPort.countLikesByPostIds(postIds));
    Map<Long, Category> categoryMap = categoryQueryService.findAllByIdsAsMap(categoryIds);
    Map<Long, Integer> commentCountMap = commentQueryService.countNonDeletedCommentsByPostIds(postIds);
    Map<Long, Integer> totalVoteCountMap = voteQueryService.getTotalVoteCountMapByPostIds(postIds);

    List<RecentCandidate> candidates = new ArrayList<>();

    for (Post post : posts) {
      Category category = categoryMap.get(post.categoryId());
      CommunityPostTag tag =
          communityCategoryPolicy.resolvePreviewTag(
              category == null ? null : category.categoryGroup(), category == null ? null : category.code());

      RecentPost recentPost =
          new RecentPost(
              CommunityPostSourceType.COMMUNITY,
              post.id(),
              post.title(),
              post.content(),
              post.createdAt(),
              likeCountMap.getOrDefault(post.id(), 0),
              commentCountMap.getOrDefault(post.id(), 0),
              tag,
              totalVoteCountMap.get(post.id()));

      candidates.add(new RecentCandidate(CommunityPostSourceType.COMMUNITY, post.createdAt(), post.id(), recentPost));
    }

    List<CrewMeetingPost> meetingPosts = getMeetingPostsForPreview(memberId, HOME_PREVIEW_LIMIT);

    for (CrewMeetingPost meetingPost : meetingPosts) {
      RecentPost recentPost =
          new RecentPost(
              CommunityPostSourceType.MEETING,
              meetingPost.id(),
              meetingPost.title(),
              meetingPost.content(),
              meetingPost.createdAt(),
              meetingPost.likeCount(),
              meetingPost.commentCount(),
              CommunityPostTag.MEETING,
              null);

      candidates.add(
          new RecentCandidate(CommunityPostSourceType.MEETING, meetingPost.createdAt(), meetingPost.id(), recentPost));
    }

    return candidates.stream()
        .sorted(
            Comparator.comparing(RecentCandidate::createdAt)
                .reversed()
                .thenComparing(candidate -> candidate.sourceType().name())
                .thenComparing(RecentCandidate::id, Comparator.reverseOrder()))
        .limit(HOME_PREVIEW_LIMIT)
        .map(RecentCandidate::post)
        .toList();
  }

  private List<CrewMeetingPost> getMeetingPostsForPreview(Long userId, int requiredCount) {
    if (userId == null || requiredCount <= 0) {
      return List.of();
    }

    LocalDateTime snapshotTime = LocalDateTime.now().withSecond(0).withNano(0);
    CrewMeetingFeedPage page = crewMeetingPostPort.getPreview(userId, snapshotTime, requiredCount);

    return page.safePosts().stream().limit(requiredCount).toList();
  }

  // ==========================================
  // 핫 게시물(deprecated)
  // ==========================================

  public Optional<Post> getTodayHotPost() {
    List<Post> todayPosts = getTodayPosts();
    Post hotPost = findTodayHotPost(todayPosts);

    if (hotPost != null) {
      return Optional.of(hotPost);
    }

    return postRepositoryPort.findMostRecentHotPost();
  }

  private List<Post> getTodayPosts() {
    LocalDate yesterday = LocalDate.now().minusDays(1);
    LocalDateTime startOfDay = yesterday.atStartOfDay().minusHours(9);
    LocalDateTime endOfDay = yesterday.atTime(LocalTime.MAX).minusHours(9);
    return postRepositoryPort.findAllByCreatedAtBetween(startOfDay, endOfDay);
  }

  private Post findTodayHotPost(List<Post> posts) {
    List<Long> postIds = posts.stream().map(Post::id).toList();
    Map<Long, Integer> commentCountMap = commentQueryService.countNonDeletedCommentsByPostIds(postIds);

    return posts.stream()
        .map(post -> toPostWithPoints(post, commentCountMap))
        .filter(postWithPoints -> postWithPoints.points() >= MIN_POINTS_FOR_HOT_POST)
        .max(Comparator.comparingInt(PostWithPoints::points).thenComparingInt(PostWithPoints::hits))
        .map(PostWithPoints::post)
        .orElse(null);
  }

  private PostWithPoints toPostWithPoints(Post post, Map<Long, Integer> commentCountMap) {
    int commentCount = commentCountMap.getOrDefault(post.id(), 0);
    int likeCount = postLikeRepositoryPort.countAllByPostId(post.id());
    int points = commentCount * 2 + likeCount;
    return new PostWithPoints(post, points, post.hits());
  }

  // ==========================================
  // 공통 유틸
  // ==========================================

  private Map<Long, Boolean> getLikedMap(Long viewerId, List<Long> postIds) {
    if (postIds.isEmpty()) {
      return Map.of();
    }

    List<Long> likedPostIds = postLikeRepositoryPort.findLikedPostIdsByUserIdAndPostIds(viewerId, postIds);
    Set<Long> likedPostIdSet = Set.copyOf(likedPostIds);

    return postIds.stream().collect(Collectors.toMap(postId -> postId, likedPostIdSet::contains));
  }

  private Map<Long, Integer> toIntCountMap(Map<Long, Long> counts) {
    return counts.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().intValue()));
  }

  private LocalDateTime cursorCreatedAt(CommunityFeedCursor cursor) {
    return cursor.community() == null ? null : cursor.community().createdAt();
  }

  private Long cursorPostId(CommunityFeedCursor cursor) {
    return cursor.community() == null ? null : cursor.community().postId();
  }

  private CommunityPostListCategory toListCategory(CommunityCategoryCode code) {
    return switch (code) {
      case FREE, MEETING -> CommunityPostListCategory.FREE;
      case PROMOTION, PROMOTION_EVENT, PROMOTION_PROJECT, PROMOTION_RECRUIT, PROMOTION_ETC ->
          CommunityPostListCategory.PROMOTION;
      case SOPTICLE,
          SOPTICLE_PLAN,
          SOPTICLE_DESIGN,
          SOPTICLE_SERVER,
          SOPTICLE_WEB,
          SOPTICLE_IOS,
          SOPTICLE_ANDROID,
          SOPTICLE_ETC ->
          CommunityPostListCategory.SOPTICLE;
    };
  }

  private int normalizeLimit(Integer limit) {
    if (limit == null || limit <= 0 || limit > MAX_LIST_LIMIT) {
      return MAX_LIST_LIMIT;
    }
    return limit;
  }

  private int normalizePreviewLimit(int limit) {
    if (limit <= 0 || limit > HOME_PREVIEW_LIMIT) {
      return HOME_PREVIEW_LIMIT;
    }
    return limit;
  }
}
