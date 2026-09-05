package org.sopt.makers.storage.db.playground.member.tl.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
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
import org.sopt.makers.domain.playground.member.tl.ServiceType;
import org.sopt.makers.domain.playground.member.tl.TlUser;

// appjam_tl_members 테이블에는 생성/수정 시각 컬럼이 없어 BaseEntity를 상속하지 않는다.
@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "appjam_tl_members")
public class TlUserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "member_id", nullable = false)
  private Long memberUserId;

  @Column(name = "tl_generation", nullable = false)
  private Integer tlGeneration;

  @Enumerated(EnumType.STRING)
  @Column(name = "service_type", nullable = false)
  private ServiceType serviceType;

  @Column(name = "self_introduction", nullable = false, length = 2048)
  private String selfIntroduction;

  @Column(name = "competition_data", nullable = false, length = 2048)
  private String competitionData;

  @Builder(access = PRIVATE)
  private TlUserEntity(
      Long id,
      Long memberUserId,
      Integer tlGeneration,
      ServiceType serviceType,
      String selfIntroduction,
      String competitionData) {
    this.id = id;
    this.memberUserId = memberUserId;
    this.tlGeneration = tlGeneration;
    this.serviceType = serviceType;
    this.selfIntroduction = selfIntroduction;
    this.competitionData = competitionData;
  }

  public TlUser toDomain() {
    return new TlUser(id, memberUserId, tlGeneration, serviceType, selfIntroduction, competitionData);
  }

  public static TlUserEntity fromDomain(TlUser tlUser) {
    return TlUserEntity.builder()
        .id(tlUser.id())
        .memberUserId(tlUser.memberUserId())
        .tlGeneration(tlUser.tlGeneration())
        .serviceType(tlUser.serviceType())
        .selfIntroduction(tlUser.selfIntroduction())
        .competitionData(tlUser.competitionData())
        .build();
  }
}
