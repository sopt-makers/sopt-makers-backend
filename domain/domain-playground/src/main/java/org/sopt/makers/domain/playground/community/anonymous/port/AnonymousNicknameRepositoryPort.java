package org.sopt.makers.domain.playground.community.anonymous.port;

import java.util.List;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousNickname;

public interface AnonymousNicknameRepositoryPort {

  AnonymousNickname findRandomOne();

  AnonymousNickname findRandomOneExcludingIds(List<Long> excludeIds);

  List<AnonymousNickname> findAllByNicknameIn(List<String> nicknames);

  List<AnonymousNickname> findAllByIds(List<Long> ids);
}
