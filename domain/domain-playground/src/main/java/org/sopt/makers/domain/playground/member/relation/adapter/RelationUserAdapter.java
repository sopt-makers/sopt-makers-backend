package org.sopt.makers.domain.playground.member.relation.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.member.relation.port.RelationUserPort;
import org.sopt.makers.domain.user.port.PlaygroundRelationUserPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RelationUserAdapter implements RelationUserPort {

  private final PlaygroundRelationUserPort playgroundRelationUserPort;

  @Override
  public boolean existsById(Long userId) {
    return playgroundRelationUserPort.existsById(userId);
  }
}
