package org.sopt.makers.domain.playground.community.anonymous.service;

import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.NOT_FOUND_ANONYMOUS_NICKNAME;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousNickname;
import org.sopt.makers.domain.playground.community.anonymous.port.AnonymousNicknameRepositoryPort;
import org.sopt.makers.domain.playground.community.exception.CommunityException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnonymousNicknameRetriever {

  private final AnonymousNicknameRepositoryPort anonymousNicknameRepositoryPort;

  public AnonymousNickname findRandomAnonymousNickname(
      List<AnonymousNickname> recentUsedAnonymousNicknames) {
    if (recentUsedAnonymousNicknames.isEmpty()) {
      return anonymousNicknameRepositoryPort.findRandomOne();
    }

    return anonymousNicknameRepositoryPort.findRandomOneExcludingIds(
        recentUsedAnonymousNicknames.stream().map(AnonymousNickname::id).toList());
  }

  public void validateAnonymousNicknames(String[] nicknames) {
    if (nicknames == null || nicknames.length == 0) {
      return;
    }

    List<String> nicknameList = Arrays.asList(nicknames);
    List<AnonymousNickname> foundNicknames =
        anonymousNicknameRepositoryPort.findAllByNicknameIn(nicknameList);

    Set<String> foundNicknameSet =
        foundNicknames.stream().map(AnonymousNickname::nickname).collect(Collectors.toSet());

    for (String nickname : nicknames) {
      if (!foundNicknameSet.contains(nickname)) {
        throw new CommunityException(NOT_FOUND_ANONYMOUS_NICKNAME);
      }
    }
  }
}
