package org.sopt.makers.storage.db.official.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.official.member.Member;
import org.sopt.makers.domain.official.member.MemberRole;
import org.sopt.makers.domain.official.member.SnsLinks;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "member")
public class MemberEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "generation_id", nullable = false)
  private Integer generationId;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, length = 50)
  private MemberRole role;

  @Column(name = "name", nullable = false, length = 50)
  private String name;

  @Column(name = "affiliation", nullable = false, length = 100)
  private String affiliation;

  @Column(name = "introduction", nullable = false, length = 500)
  private String introduction;

  @Column(name = "profile_image_url", nullable = false, length = 500)
  private String profileImageUrl;

  @Embedded private SnsLinksEmbeddable snsLinks;

  @Builder(access = PRIVATE)
  private MemberEntity(
      Long id,
      Integer generationId,
      MemberRole role,
      String name,
      String affiliation,
      String introduction,
      String profileImageUrl,
      SnsLinksEmbeddable snsLinks) {
    this.id = id;
    this.generationId = generationId;
    this.role = role;
    this.name = name;
    this.affiliation = affiliation;
    this.introduction = introduction;
    this.profileImageUrl = profileImageUrl;
    this.snsLinks = snsLinks;
  }

  public Member toDomain() {
    SnsLinks sns =
        snsLinks != null
            ? SnsLinks.of(snsLinks.email, snsLinks.linkedin, snsLinks.github, snsLinks.behance)
            : SnsLinks.empty();
    return new Member(id, generationId, role, name, affiliation, introduction, profileImageUrl, sns);
  }

  public static MemberEntity fromDomain(Member member) {
    SnsLinksEmbeddable sns =
        member.snsLinks() != null
            ? new SnsLinksEmbeddable(
                member.snsLinks().email(),
                member.snsLinks().linkedin(),
                member.snsLinks().github(),
                member.snsLinks().behance())
            : new SnsLinksEmbeddable("", "", "", "");
    return MemberEntity.builder()
        .id(member.id())
        .generationId(member.generationId())
        .role(member.role())
        .name(member.name())
        .affiliation(member.affiliation())
        .introduction(member.introduction())
        .profileImageUrl(member.profileImageUrl() != null ? member.profileImageUrl() : "")
        .snsLinks(sns)
        .build();
  }
}
