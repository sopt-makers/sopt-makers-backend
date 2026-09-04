package org.sopt.makers.domain.playground.community.anonymous.service;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousNickname;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousProfile;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousProfileImage;
import org.sopt.makers.domain.playground.community.anonymous.port.AnonymousProfileRepositoryPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnonymousProfileModifier {

  private final AnonymousProfileRepositoryPort anonymousProfileRepositoryPort;

  public AnonymousProfile createAnonymousProfile(
      Long userId, Long postId, AnonymousNickname nickname, AnonymousProfileImage profileImage) {
    return anonymousProfileRepositoryPort.save(
        AnonymousProfile.create(userId, postId, nickname, profileImage));
  }
}
