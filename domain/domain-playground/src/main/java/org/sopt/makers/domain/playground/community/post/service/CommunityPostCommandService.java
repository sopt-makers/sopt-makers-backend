package org.sopt.makers.domain.playground.community.post.service;

import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.ALREADY_LIKED_POST;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.NOT_FOUND_COMMUNITY_POST;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.NOT_FOUND_WRITER;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.NOT_LIKED_POST;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.UNAUTHORIZED_POST_ACCESS;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.Category;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousProfile;
import org.sopt.makers.domain.playground.community.anonymous.service.AnonymousProfileService;
import org.sopt.makers.domain.playground.community.exception.CommunityException;
import org.sopt.makers.domain.playground.community.member.service.CommunityMemberAssembler;
import org.sopt.makers.domain.playground.community.post.DeletedPost;
import org.sopt.makers.domain.playground.community.post.Post;
import org.sopt.makers.domain.playground.community.post.PostLike;
import org.sopt.makers.domain.playground.community.post.ReportPost;
import org.sopt.makers.domain.playground.community.post.port.DeletedPostRepositoryPort;
import org.sopt.makers.domain.playground.community.post.port.PostLikeRepositoryPort;
import org.sopt.makers.domain.playground.community.post.port.PostRepositoryPort;
import org.sopt.makers.domain.playground.community.post.port.ReportPostRepositoryPort;
import org.sopt.makers.domain.playground.community.service.CategoryQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 커뮤니티 게시글 생성/수정/삭제, 좋아요/좋아요 취소, 신고 유스케이스. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityPostCommandService {

  private final PostRepositoryPort postRepositoryPort;
  private final PostLikeRepositoryPort postLikeRepositoryPort;
  private final ReportPostRepositoryPort reportPostRepositoryPort;
  private final DeletedPostRepositoryPort deletedPostRepositoryPort;
  private final CategoryQueryService categoryQueryService;
  private final CommunityMemberAssembler communityMemberAssembler;
  private final AnonymousProfileService anonymousProfileService;

  public record CreatePostCommand(
      CommunityCategoryCode categoryCode,
      String title,
      String content,
      Boolean isBlindWriter,
      List<String> images,
      String link) {}

  public record UpdatePostCommand(
      CommunityCategoryCode categoryCode,
      String title,
      String content,
      Boolean isBlindWriter,
      List<String> images,
      String link) {}

  public record PostMutationResult(Post post, CommunityCategoryCode categoryCode) {}

  @Transactional
  public PostMutationResult createPost(Long writerId, CreatePostCommand command) {
    validateWriterExists(writerId);
    Category category = categoryQueryService.findActiveCategoryByCode(command.categoryCode());

    // TODO: SOPTICLE 카테고리 URL 스크래핑(Sopticle 외부 API 연동)은 별도 Client Port 마이그레이션 이후 연동 예정.
    // 현재는 요청값을 그대로 저장한다.
    Post created =
        postRepositoryPort.save(
            Post.create(
                writerId,
                category.id(),
                command.title(),
                command.content(),
                command.images(),
                false,
                command.isBlindWriter(),
                command.link()));

    if (Boolean.TRUE.equals(command.isBlindWriter())) {
      AnonymousProfile profile = anonymousProfileService.getOrCreateAnonymousProfile(writerId, created.id());
      created = postRepositoryPort.save(created.withAnonymousProfileId(profile.id()));
    }

    return new PostMutationResult(created, category.code());
  }

  @Transactional
  public PostMutationResult updatePost(Long writerId, Long postId, UpdatePostCommand command) {
    Post post = getPostOrThrow(postId);
    validateOwner(post, writerId);
    Category category = categoryQueryService.findActiveCategoryByCode(command.categoryCode());

    Post updated =
        postRepositoryPort.save(
            post.update(
                category.id(),
                command.title(),
                command.content(),
                command.images(),
                command.isBlindWriter(),
                command.link()));

    return new PostMutationResult(updated, category.code());
  }

  @Transactional
  public void deletePost(Long writerId, Long postId) {
    Post post = getPostOrThrow(postId);
    validateOwner(post, writerId);

    deletedPostRepositoryPort.save(DeletedPost.from(post));
    postRepositoryPort.delete(post);
  }

  @Transactional
  public void likePost(Long userId, Long postId) {
    validateWriterExists(userId);
    getPostOrThrow(postId);

    if (postLikeRepositoryPort.existsByUserIdAndPostId(userId, postId)) {
      throw new CommunityException(ALREADY_LIKED_POST);
    }

    postLikeRepositoryPort.save(PostLike.create(userId, postId));
  }

  @Transactional
  public void unlikePost(Long userId, Long postId) {
    PostLike postLike =
        postLikeRepositoryPort
            .findByUserIdAndPostId(userId, postId)
            .orElseThrow(() -> new CommunityException(NOT_LIKED_POST));

    postLikeRepositoryPort.delete(postLike);
  }

  @Transactional
  public void reportPost(Long reporterId, Long postId) {
    getPostOrThrow(postId);

    // TODO: 신고 Slack 알림 연동은 SlackClient 마이그레이션 이후 별도 처리 예정.
    reportPostRepositoryPort.save(ReportPost.create(postId, reporterId));
  }

  private Post getPostOrThrow(Long postId) {
    return postRepositoryPort.findById(postId).orElseThrow(() -> new CommunityException(NOT_FOUND_COMMUNITY_POST));
  }

  private void validateWriterExists(Long userId) {
    if (communityMemberAssembler.getMemberSummary(userId) == null) {
      throw new CommunityException(NOT_FOUND_WRITER);
    }
  }

  private void validateOwner(Post post, Long userId) {
    if (!Objects.equals(post.writerId(), userId)) {
      throw new CommunityException(UNAUTHORIZED_POST_ACCESS);
    }
  }
}
