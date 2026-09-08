package org.sopt.makers.domain.playground.community.anonymous.service;

import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousNickname;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousProfile;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousProfileImage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnonymousProfileService {

  private static final int RECENT_NICKNAME_LIMIT = 50;

  private final AnonymousProfileRetriever anonymousProfileRetriever;
  private final AnonymousProfileModifier anonymousProfileModifier;
  private final AnonymousProfileImageRetriever anonymousProfileImageRetriever;
  private final AnonymousNicknameRetriever anonymousNicknameRetriever;

  /** userId + postId 조합으로 익명 프로필 조회 또는 생성. 같은 사용자가 같은 게시글에서는 항상 같은 익명 프로필을 사용한다. */
  @Transactional
  public AnonymousProfile getOrCreateAnonymousProfile(Long userId, Long postId) {
    return anonymousProfileRetriever
        .findByUserIdAndPostId(userId, postId)
        .orElseGet(() -> createAnonymousProfile(userId, postId));
  }

  private AnonymousProfile createAnonymousProfile(Long userId, Long postId) {
    // 해당 게시글에서 이미 사용된 닉네임 제외
    List<AnonymousProfile> existingProfilesInPost =
        anonymousProfileRetriever.findAllByPostId(postId);
    List<AnonymousNickname> excludeNicknamesInPost =
        existingProfilesInPost.stream().map(AnonymousProfile::nickname).toList();

    // 최근 50개 프로필에서 사용된 닉네임도 제외
    List<AnonymousProfile> recentProfiles =
        anonymousProfileRetriever.getRecentProfiles(RECENT_NICKNAME_LIMIT);
    List<AnonymousNickname> recentNicknames =
        recentProfiles.stream().map(AnonymousProfile::nickname).toList();

    // 중복 없이 합치기
    List<AnonymousNickname> excludeNicknames =
        Stream.concat(excludeNicknamesInPost.stream(), recentNicknames.stream())
            .distinct()
            .toList();

    AnonymousNickname nickname =
        anonymousNicknameRetriever.findRandomAnonymousNickname(excludeNicknames);
    AnonymousProfileImage profileImage = anonymousProfileImageRetriever.getAnonymousProfileImage();

    return anonymousProfileModifier.createAnonymousProfile(userId, postId, nickname, profileImage);
  }
}
