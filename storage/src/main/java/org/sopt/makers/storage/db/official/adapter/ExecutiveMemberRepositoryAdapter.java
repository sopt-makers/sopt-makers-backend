package org.sopt.makers.storage.db.official.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.member.Member;
import org.sopt.makers.domain.official.member.port.MemberRepositoryPort;
import org.sopt.makers.storage.db.official.entity.ExecutiveMemberEntity;
import org.sopt.makers.storage.db.official.repository.ExecutiveMemberJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExecutiveMemberRepositoryAdapter implements MemberRepositoryPort {

  private final ExecutiveMemberJpaRepository memberJpaRepository;

  @Transactional
  @Override
  public List<Member> saveAll(Integer generationId, List<Member> members) {
    List<ExecutiveMemberEntity> entities =
        members.stream().map(ExecutiveMemberEntity::fromDomain).toList();
    return memberJpaRepository.saveAll(entities).stream()
        .map(ExecutiveMemberEntity::toDomain)
        .toList();
  }

  @Transactional
  @Override
  public void deleteByGenerationId(Integer generationId) {
    memberJpaRepository.deleteByGenerationId(generationId);
  }

  @Override
  public List<Member> findByGenerationId(Integer generationId) {
    return memberJpaRepository.findByGenerationIdOrderByRoleAsc(generationId).stream()
        .map(ExecutiveMemberEntity::toDomain)
        .toList();
  }
}
