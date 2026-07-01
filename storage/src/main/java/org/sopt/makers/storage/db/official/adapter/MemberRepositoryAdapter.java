package org.sopt.makers.storage.db.official.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.member.Member;
import org.sopt.makers.domain.official.member.port.MemberRepositoryPort;
import org.sopt.makers.storage.db.official.entity.MemberEntity;
import org.sopt.makers.storage.db.official.repository.MemberJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberRepositoryAdapter implements MemberRepositoryPort {

  private final MemberJpaRepository memberJpaRepository;

  @Transactional
  @Override
  public List<Member> saveAll(Integer generationId, List<Member> members) {
    List<MemberEntity> entities = members.stream().map(MemberEntity::fromDomain).toList();
    return memberJpaRepository.saveAll(entities).stream().map(MemberEntity::toDomain).toList();
  }

  @Transactional
  @Override
  public void deleteByGenerationId(Integer generationId) {
    memberJpaRepository.deleteByGenerationId(generationId);
  }

  @Override
  public List<Member> findByGenerationId(Integer generationId) {
    return memberJpaRepository
        .findByGenerationIdOrderByRoleAsc(generationId)
        .stream()
        .map(MemberEntity::toDomain)
        .toList();
  }
}
