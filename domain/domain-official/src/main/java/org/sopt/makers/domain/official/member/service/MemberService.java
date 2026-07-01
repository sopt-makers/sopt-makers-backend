package org.sopt.makers.domain.official.member.service;

import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.member.Member;
import org.sopt.makers.domain.official.member.MemberRole;
import org.sopt.makers.domain.official.member.SnsLinks;
import org.sopt.makers.domain.official.member.port.MemberRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

  private final MemberRepositoryPort memberRepositoryPort;

  @Transactional
  public void bulkCreate(BulkCreateMembersCommand command) {
    memberRepositoryPort.deleteByGenerationId(command.generationId());

    List<Member> members =
        command.members().stream()
            .map(
                data ->
                    new Member(
                        null,
                        command.generationId(),
                        MemberRole.fromString(data.role()),
                        data.name(),
                        data.affiliation(),
                        data.introduction(),
                        data.profileImageUrl(),
                        data.snsLinks() != null
                            ? SnsLinks.of(
                                data.snsLinks().email(),
                                data.snsLinks().linkedin(),
                                data.snsLinks().github(),
                                data.snsLinks().behance())
                            : SnsLinks.empty()))
            .toList();

    memberRepositoryPort.saveAll(command.generationId(), members);
  }

  public List<Member> findByGeneration(Integer generationId) {
    return memberRepositoryPort.findByGenerationId(generationId).stream()
        .sorted(
            Comparator.comparingInt((Member m) -> m.role().getOrder()).thenComparing(Member::name))
        .toList();
  }

  public record BulkCreateMembersCommand(Integer generationId, List<MemberData> members) {

    public record MemberData(
        String role,
        String name,
        String affiliation,
        String introduction,
        String profileImageUrl,
        SnsLinksData snsLinks) {}

    public record SnsLinksData(String email, String linkedin, String github, String behance) {}
  }
}
