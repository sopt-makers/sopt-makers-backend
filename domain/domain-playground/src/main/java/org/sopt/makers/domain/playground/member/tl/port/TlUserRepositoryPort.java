package org.sopt.makers.domain.playground.member.tl.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.playground.member.tl.TlUser;

public interface TlUserRepositoryPort {

  TlUser save(TlUser tlUser);

  Optional<TlUser> findById(Long id);

  /** 지정한 앱잼 TL 기수에 속한 TL 목록을 조회한다. */
  List<TlUser> findAllByTlGeneration(Integer tlGeneration);

  void deleteById(Long id);
}
