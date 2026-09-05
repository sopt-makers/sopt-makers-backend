package org.sopt.makers.domain.playground.post.service;

import static org.sopt.makers.domain.playground.post.exception.PostFailure.ALREADY_REPORTED_POST;
import static org.sopt.makers.domain.playground.post.exception.PostFailure.FORBIDDEN_MEETING_POST;
import static org.sopt.makers.domain.playground.post.exception.PostFailure.NOT_FOUND_MEETING;
import static org.sopt.makers.domain.playground.post.exception.PostFailure.NOT_FOUND_POST;
import static org.sopt.makers.domain.playground.post.exception.PostFailure.NOT_FOUND_USER;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.pagination.PageQuery;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.playground.post.MeetingPostContext;
import org.sopt.makers.domain.playground.post.Post;
import org.sopt.makers.domain.playground.post.PostContentType;
import org.sopt.makers.domain.playground.post.PostNotification;
import org.sopt.makers.domain.playground.post.PostWriter;
import org.sopt.makers.domain.playground.post.exception.PostException;
import org.sopt.makers.domain.playground.post.like.PostLike;
import org.sopt.makers.domain.playground.post.port.MeetingPostAccessPort;
import org.sopt.makers.domain.playground.post.port.MumuPostPolicyPort;
import org.sopt.makers.domain.playground.post.port.PostCommentRepositoryPort;
import org.sopt.makers.domain.playground.post.port.PostLikeRepositoryPort;
import org.sopt.makers.domain.playground.post.port.PostNotificationSenderPort;
import org.sopt.makers.domain.playground.post.port.PostReportRepositoryPort;
import org.sopt.makers.domain.playground.post.port.PostRepositoryPort;
import org.sopt.makers.domain.playground.post.report.PostReport;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.Profile;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.port.PlaygroundPostUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

  private final PostRepositoryPort postRepositoryPort;
  private final PostCommentRepositoryPort commentRepositoryPort;
  private final PostLikeRepositoryPort likeRepositoryPort;
  private final PostReportRepositoryPort reportRepositoryPort;
  private final MeetingPostAccessPort meetingPostAccessPort;
  private final PlaygroundPostUserPort userPort;
  private final MumuPostPolicyPort mumuPostPolicyPort;
  private final PostNotificationSenderPort notificationSenderPort;
  private final Clock clock;

  @Transactional
  public Post createMeetingPost(CreateMeetingPostCommand command, Long userId) {
    validateUser(userId);
    validateMeetingMember(command.meetingId(), userId);
    Post saved =
        postRepositoryPort.save(
            Post.createMeetingPost(
                userId,
                command.meetingId(),
                command.title(),
                command.contents(),
                command.images(),
                command.contentType()));
    saveMumuWriteHistory(saved, userId);
    sendNewPostNotification(saved, userId);
    return saved;
  }

  public PageResult<PostView> findMeetingPosts(Long meetingId, Long userId, int page, int limit) {
    validateUser(userId);
    List<MeetingPostContext> meetings;
    PageResult<Post> posts;
    PageQuery pageQuery = new PageQuery(page, limit);

    if (meetingId != null) {
      MeetingPostContext meeting = validateMeetingMember(meetingId, userId);
      meetings = List.of(meeting);
      posts = postRepositoryPort.findByMeetingId(meetingId, pageQuery);
    } else {
      meetings = meetingPostAccessPort.findMeetingsByUserId(userId);
      List<Long> meetingIds = meetings.stream().map(MeetingPostContext::meetingId).toList();
      if (meetingIds.isEmpty()) {
        return emptyPage(page, limit);
      }
      posts = postRepositoryPort.findByMeetingIds(meetingIds, pageQuery);
    }

    return enrich(posts, meetings, userId);
  }

  public PostView getMeetingPost(Long postId, Long userId) {
    Post post = getPost(postId);
    MeetingPostContext meeting = validateMeetingMember(post.meetingId(), userId);
    return enrich(singlePage(post), List.of(meeting), userId).content().getFirst();
  }

  @Transactional
  public UpdatedPost updatePost(Long postId, UpdatePostCommand command, Long userId) {
    Post post = getPostForUpdate(postId);
    validateMeetingMember(post.meetingId(), userId);
    post.validateWriter(userId);
    Post saved =
        postRepositoryPort.save(post.update(command.title(), command.contents(), command.images()));
    return new UpdatedPost(saved);
  }

  @Transactional
  public PostReport reportPost(Long postId, Long userId) {
    Post post = getPost(postId);
    validateMeetingMember(post.meetingId(), userId);
    if (reportRepositoryPort.existsByPostIdAndReporterId(postId, userId)) {
      throw new PostException(ALREADY_REPORTED_POST);
    }
    return reportRepositoryPort.save(PostReport.create(postId, userId));
  }

  @Transactional
  public boolean togglePostLike(Long postId, Long userId) {
    Post post = getPostForUpdate(postId);
    validateMeetingMember(post.meetingId(), userId);
    if (likeRepositoryPort.existsByPostIdAndUserId(postId, userId)) {
      likeRepositoryPort.deleteByPostIdAndUserId(postId, userId);
      postRepositoryPort.save(post.decreaseLikeCount());
      return false;
    }
    likeRepositoryPort.save(PostLike.create(postId, userId));
    postRepositoryPort.save(post.increaseLikeCount());
    return true;
  }

  @Transactional
  public int increaseViewCount(Long postId, Long userId) {
    Post post = getPostForUpdate(postId);
    validateMeetingMember(post.meetingId(), userId);
    return postRepositoryPort.save(post.increaseViewCount()).viewCount();
  }

  public long countMeetingPosts(Long meetingId) {
    if (meetingPostAccessPort.findMeeting(meetingId, null).isEmpty()) {
      throw new PostException(NOT_FOUND_MEETING);
    }
    return postRepositoryPort.countByMeetingId(meetingId);
  }

  public MumuHome getMumuHome(Long userId) {
    validateUser(userId);
    String mumuText = mumuPostPolicyPort.getCurrentText();
    List<MeetingPostContext> meetings = meetingPostAccessPort.findMeetingsByUserId(userId);
    if (meetings.isEmpty()) {
      return new MumuHome(true, false, false, mumuText, List.of());
    }

    LocalDate today = LocalDate.now(clock);
    boolean written = mumuPostPolicyPort.hasWritten(userId, today);
    if (!written) {
      return new MumuHome(false, false, false, mumuText, List.of());
    }

    LocalDateTime startAt = today.atStartOfDay();
    List<Post> mumuPosts =
        postRepositoryPort.findByMeetingIdsAndContentTypeAndCreatedAtBetweenExcludingWriter(
            meetings.stream().map(MeetingPostContext::meetingId).toList(),
            PostContentType.MUMU,
            startAt,
            startAt.plusDays(1),
            userId);
    PageResult<PostView> views =
        enrich(
            new PageResult<>(
                mumuPosts,
                mumuPosts.size(),
                mumuPosts.isEmpty() ? 0 : 1,
                1,
                Math.max(1, mumuPosts.size()),
                false,
                false),
            meetings,
            userId);
    return new MumuHome(false, true, !views.content().isEmpty(), mumuText, views.content());
  }

  public String getCurrentMumuText() {
    return mumuPostPolicyPort.getCurrentText();
  }

  public void mentionUsers(Long postId, List<Long> mentionedUserIds, String contents, Long userId) {
    Post post = getPost(postId);
    validateMeetingMember(post.meetingId(), userId);
    notificationSenderPort.send(
        new PostNotification(
            mentionedUserIds, "게시글에서 회원님이 언급됐어요.", contents, post.id(), post.meetingId()));
  }

  private void sendNewPostNotification(Post post, Long writerId) {
    PostWriter writer = userPort.findWithActivitiesById(writerId).map(this::toWriter).orElse(null);
    String writerName = writer == null || writer.name() == null ? "멤버" : writer.name();
    notificationSenderPort.send(
        new PostNotification(
            meetingPostAccessPort.findMemberIds(post.meetingId()),
            "새로운 모임 글이 등록됐어요.",
            "[" + writerName + "의 새 글] : \"" + post.title() + "\"",
            post.id(),
            post.meetingId()));
  }

  private void saveMumuWriteHistory(Post post, Long userId) {
    if (post.contentType() != PostContentType.MUMU) {
      return;
    }
    mumuPostPolicyPort.recordWrittenIfAbsent(userId, LocalDate.now(clock));
  }

  private PageResult<PostView> enrich(
      PageResult<Post> posts, List<MeetingPostContext> meetings, Long userId) {
    List<Post> content = posts.content();
    if (content.isEmpty()) {
      return posts.map(post -> null);
    }

    Map<Long, MeetingPostContext> meetingMap =
        meetings.stream()
            .collect(Collectors.toMap(MeetingPostContext::meetingId, Function.identity()));
    Set<Long> likedPostIds =
        likeRepositoryPort.findLikedPostIds(content.stream().map(Post::id).toList(), userId);

    Map<Long, List<Long>> commenterIdsByPostId =
        content.stream()
            .collect(
                Collectors.toMap(
                    Post::id,
                    post -> commentRepositoryPort.findDistinctWriterIdsByPostId(post.id())));

    LinkedHashSet<Long> userIds =
        content.stream()
            .map(Post::writerId)
            .filter(java.util.Objects::nonNull)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    commenterIdsByPostId.values().stream().flatMap(Collection::stream).forEach(userIds::add);

    Map<Long, PostWriter> writerMap = getWriterMap(userIds);

    return posts.map(
        post ->
            new PostView(
                post,
                writerMap.get(post.writerId()),
                meetingMap.get(post.meetingId()),
                likedPostIds.contains(post.id()),
                commenterIdsByPostId.getOrDefault(post.id(), List.of()).stream()
                    .map(writerMap::get)
                    .filter(java.util.Objects::nonNull)
                    .map(PostWriter::profileImage)
                    .filter(java.util.Objects::nonNull)
                    .distinct()
                    .limit(3)
                    .toList(),
                false));
  }

  private Map<Long, PostWriter> getWriterMap(Collection<Long> userIds) {
    if (userIds.isEmpty()) {
      return Map.of();
    }
    return userPort.findAllWithActivitiesByIds(List.copyOf(userIds)).stream()
        .collect(Collectors.toMap(User::id, this::toWriter));
  }

  private PostWriter toWriter(User user) {
    Profile profile = user.profile();
    Activity latestActivity =
        user.activities() == null
            ? null
            : user.activities().activities().stream()
                .max(java.util.Comparator.comparingInt(Activity::generation))
                .orElse(null);
    return new PostWriter(
        user.id(),
        profile == null ? null : profile.name(),
        profile == null ? null : profile.profileImage(),
        latestActivity == null ? null : latestActivity.generation(),
        latestActivity == null || latestActivity.part() == null
            ? null
            : latestActivity.part().getName());
  }

  private MeetingPostContext validateMeetingMember(Long meetingId, Long userId) {
    MeetingPostContext meeting =
        meetingPostAccessPort
            .findMeeting(meetingId, userId)
            .orElseThrow(() -> new PostException(NOT_FOUND_MEETING));
    if (!meeting.member()) {
      throw new PostException(FORBIDDEN_MEETING_POST);
    }
    return meeting;
  }

  private void validateUser(Long userId) {
    if (userPort.findWithActivitiesById(userId).isEmpty()) {
      throw new PostException(NOT_FOUND_USER);
    }
  }

  private Post getPost(Long postId) {
    return postRepositoryPort.findById(postId).orElseThrow(() -> new PostException(NOT_FOUND_POST));
  }

  private Post getPostForUpdate(Long postId) {
    return postRepositoryPort
        .findByIdForUpdate(postId)
        .orElseThrow(() -> new PostException(NOT_FOUND_POST));
  }

  private PageResult<Post> singlePage(Post post) {
    return new PageResult<>(List.of(post), 1, 1, 1, 1, false, false);
  }

  private PageResult<PostView> emptyPage(int page, int limit) {
    return new PageResult<>(List.of(), 0, 0, page, limit, false, page > 1);
  }

  public record CreateMeetingPostCommand(
      Long meetingId,
      String title,
      String contents,
      List<String> images,
      PostContentType contentType) {}

  public record UpdatePostCommand(String title, String contents, List<String> images) {}

  public record UpdatedPost(Post post) {}

  public record PostView(
      Post post,
      PostWriter writer,
      MeetingPostContext meeting,
      boolean liked,
      List<String> commenterThumbnails,
      boolean blocked) {}

  public record MumuHome(
      boolean emptyAppliedMeeting,
      boolean writtenToday,
      boolean hasHomeFeed,
      String mumuText,
      List<PostView> posts) {}
}
